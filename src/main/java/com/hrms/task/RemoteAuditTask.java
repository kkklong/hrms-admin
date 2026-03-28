package com.hrms.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hrms.constant.RedisKeyConstants;
import com.hrms.entity.Employee;
import com.hrms.entity.RemoteAuditConfig;
import com.hrms.entity.RemoteAuditRecord;
import com.hrms.enums.RemoteAuditStatus;
import com.hrms.integration.telegram.HrmsTelegramBot;
import com.hrms.model.RemoteAuditShift;
import com.hrms.repository.DepartmentRepository;
import com.hrms.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class RemoteAuditTask {
    @Resource
    private RemoteAttendancePeriodService remoteAttendancePeriodService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private RemoteAuditRecordService remoteAuditRecordService;

    @Resource
    private ShiftSchedulesService shiftSchedulesService;

    @Resource
    private RemoteAuditConfigService remoteAuditConfigService;

    @Resource
    private DepartmentRepository departmentRepository;

    @Autowired(required = false)
    private HrmsTelegramBot hrmsTelegramBot;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REMOTE_AUDIT_ALERT_MESSAGE = "當日未回應到達%s次，請注意";


    // 每兩分鐘修正 遠端稽核的狀態
    @Scheduled(initialDelay = 1000, fixedRate = 120000)
    public void processRemoteAuditStatus() {
        boolean success = remoteAuditRecordService.update(new LambdaUpdateWrapper<RemoteAuditRecord>()
                .isNull(RemoteAuditRecord::getRepliedAt)
                .le(RemoteAuditRecord::getDeadlineAt, LocalDateTime.now())
                .ge(RemoteAuditRecord::getSentAt, LocalDate.now())
                .set(RemoteAuditRecord::getStatus, RemoteAuditStatus.TIMEOUT.getValue()));
        if(success) redisTemplate.delete(RedisKeyConstants.REMOTE_AUDIT_RECORD+"*");
    }

    // 每十分鐘執行一次稽核週期
    @Scheduled(initialDelay = 100, fixedRate = 60000)
    public void processAuditLifecycle() {
//        log.info("遠端稽核任務週期開始");

        if (hrmsTelegramBot == null) {
            log.warn("Telegram Bot 未啟用，跳過稽核通知");
            return;
        }

        try {
            initiateAudits();

        } catch (Exception e) {
            log.error("遠端稽核任務發生錯誤", e);
        }


//        log.info("遠端稽核任務週期結束");
    }

    /**
     * 發起新的稽核請求
     */
    private void initiateAudits() {
        LocalDate today = LocalDate.now();

        // 獲取需要遠端稽核的員工
        List<Employee> employees = getRemoteEmployeesForAudit(today);
        if (employees.isEmpty()) {
            String cacheKey = RedisKeyConstants.REMOTE_AUDIT_EMPTY_LIST;
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(cacheKey, today, Duration.ofHours(1)))) {
                log.info("無需發送遠端稽核通知");
            }
            return;
        }
//        log.info("遠端稽核通知發送中，員工數量: {}", employees.size());
        // 獲取稽核配置
        RemoteAuditConfig config = remoteAuditConfigService.queryEntity();

        // 對每個員工進行稽核處理
        for (Employee employee : employees) {
            try {
                processEmployeeAudit(employee, config);
            } catch (NumberFormatException e) {
                log.error("Telegram ID 格式錯誤: {} 員工: {}({})", employee.getTelegram(), employee.getAccount(), employee.getFullName());
            } catch (Exception e) {
                log.error("處理員工稽核失敗: {}({})", employee.getAccount(), employee.getFullName(), e);
            }
        }
    }


    private void processEmployeeAudit(Employee employee, RemoteAuditConfig config) {
        // 獲取員工最近的稽核記錄
        List<RemoteAuditRecord> records = remoteAuditRecordService.queryRecent(employee.getId());

        // 1. 提取變量，提高可讀性
        String dailyLimitKey = RedisKeyConstants.REMOTE_AUDIT_DAILY_LIMIT + employee.getId();
        int currentCount = records.size();
        int maxCount = config.getDailyCheckTotal();

        // 2. 檢查是否達到上限
        if (currentCount >= maxCount) {
            // 3. 使用 Redis 的 SETNX (setIfAbsent) 原子操作確保並發情況下只執行一次日誌記錄
            // 這裡的鎖定時間設為 12 小時，與業務邏輯保持一致
            boolean isLimitLocked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(dailyLimitKey, employee.getId(), Duration.ofHours(12))
            );

            if (isLimitLocked) {
                log.warn("員工 {}({}) 今日點名次數已達上限 (當前: {}/{})",
                        employee.getAccount(), employee.getFullName(), currentCount, maxCount);
            }
            return;
        }

        // 檢查是否需要發送告警
        checkAndSendAlert(employee, records, config);

        // 檢查是否需要發送新的稽核請求
        SendDecision decision = shouldSendNewAuditRequest(records, config);

        if (decision == SendDecision.DO_NOT_SEND) {
            return;
        }

        if (decision == SendDecision.SEND_RETRY) {
            // 重試場景：直接發送，不經過隨機
            sendAuditRequest(employee, config);
        } else if (decision == SendDecision.SEND_NORMAL) {
            // 常規場景：經過隨機策略判斷
            if (shouldSendAudit(config)) {
                sendAuditRequest(employee, config);
            }
        }
    }

    /**
     * 檢查並發送告警
     */
    private void checkAndSendAlert(Employee employee, List<RemoteAuditRecord> records, RemoteAuditConfig config) {
        // 獲取超時記錄數量
        int timeoutSize = (int) records.stream()
                .filter(record -> Objects.equals(record.getStatus(), (int) RemoteAuditStatus.TIMEOUT.getValue()))
                .count();
        // 檢查是否達到告警閾值
        if (timeoutSize >= config.getAlertThresholdMiss()) {
            // 發送告警給主管
            if (config.getAlertToLeader()) {
                sendAlertToManager(employee, config);
            }

            // 發送告警給人事
            if (config.getAlertToHr()) {
                sendAlertToHr(employee, config);
            }
        }
    }

    /**
     * 發送告警給主管
     */
    private void sendAlertToManager(Employee employee, RemoteAuditConfig config) {
        // 1. 檢查緩存，如果已發送過則直接返回
        if (isAlertSent(RedisKeyConstants.REMOTE_AUDIT_MANAGER_ALERT, employee.getId())) {
            return;
        }

        // 2. 獲取主管信息
        Employee manager = departmentRepository.findManagerByDepartmentId(employee.getDepartmentId());
        if (manager == null || Strings.isEmpty(manager.getTelegram())) {
            log.info("員工 {}({}) 沒有設置主管或主管沒有設置Telegram ID",
                    employee.getAccount(), employee.getFullName());
            return;
        }

        // 3. 發送告警
        sendTelegramAlert(Long.valueOf(manager.getTelegram()), employee.getNickName(),
                String.format(REMOTE_AUDIT_ALERT_MESSAGE, config.getAlertThresholdMiss()),
                "主管", manager.getFullName());
    }

    /**
     * 發送告警給人事
     */
    private void sendAlertToHr(Employee employee, RemoteAuditConfig config) {
        // 1. 檢查緩存，如果已發送過則直接返回
        if (isAlertSent(RedisKeyConstants.REMOTE_AUDIT_CC_ALERT, employee.getId())) {
            return;
        }

        // 2. 參數校驗
        if (Strings.isEmpty(config.getAlertCcEmails())) {
            return;
        }

        // 3. 解析並發送告警
        List<String> chatIds = Arrays.stream(config.getAlertCcEmails().split(","))
                .filter(email -> !email.trim().isEmpty())
                .toList();

        for (String chatId : chatIds) {
            sendTelegramAlert(Long.valueOf(chatId), employee.getNickName(),
                    String.format(REMOTE_AUDIT_ALERT_MESSAGE, config.getAlertThresholdMiss()),
                    "人事", chatId);
        }
    }

    /**
     * 輔助方法：檢查是否已發送過告警（利用 Redis setIfAbsent）
     *
     * @param keyPrefix Redis Key 前綴
     * @param employeeId 員工 ID
     * @return true 表示已發送過（緩存存在），false 表示未發送過
     */
    private boolean isAlertSent(String keyPrefix, Integer employeeId) {
        // setIfAbsent 返回 true 表示設置成功（之前沒有緩存），返回 false 表示設置失敗（已有緩存）
        // 這裡我們取反，返回 true 代表「已發送過」
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(keyPrefix + employeeId, true, Duration.ofHours(12));
        return Boolean.FALSE.equals(isNew);
    }

    /**
     * 輔助方法：統一處理 Telegram 發送邏輯與異常捕獲
     */
    private void sendTelegramAlert(Long chatId, String employeeName, String message, String role, String targetName) {
        try {
            hrmsTelegramBot.sendAlert(chatId, employeeName, message);
            log.info("已發送告警給{}: {} (ChatID: {})", role, targetName, chatId);
        } catch (Exception e) {
            log.error("發送告警給{}失敗: {} (ChatID: {})", role, targetName, chatId, e);
        }
    }



    /**
     * 計算連續超時次數
     */
    private int calculateConsecutiveUnrespondedCount(List<RemoteAuditRecord> records) {
        int unrespondedCount = 0;
        for (int i = records.size() - 1; i >= 0; i--) {
            RemoteAuditRecord record = records.get(i);
            // 判斷是否為未回應狀態
            boolean isUnresponded = Objects.equals(record.getStatus(), (int) RemoteAuditStatus.SEND.getValue())
                    || Objects.equals(record.getStatus(), (int) RemoteAuditStatus.TIMEOUT.getValue());

            if (isUnresponded) {
                unrespondedCount++;
            } else {
                // 遇到已回覆或其他狀態，停止計數
                break;
            }
        }
        return unrespondedCount;
    }

    /**
     * 獲取需要進行遠端稽核的員工列表
     */
    private List<Employee> getRemoteEmployeesForAudit(LocalDate date) {
        List<RemoteAuditShift> employeeList = shiftSchedulesService.getRemoteEmployees(date);
        if (employeeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> employeeIds = employeeList.stream()
                .map(RemoteAuditShift::getEmployeeId)
                .toList();


        return employeeService.lambdaQuery()
                .isNotNull(Employee::getTelegram)
                .ne(Employee::getTelegram, "")
                .in(Employee::getId, employeeIds)
                .list();
    }

    /**
     * 發送稽核請求
     */
    private void sendAuditRequest(Employee employee, RemoteAuditConfig config) {
        // 獲取回應截止時間（分鐘）
        Integer deadlineMin = config.getResponseTimeoutMin();

        // 生成會話令牌
        String uuid = UUID.randomUUID().toString();

        // 獲取聊天ID
        Long chatId = Long.valueOf(employee.getTelegram());

        // 記錄當前時間
        LocalDateTime now = LocalDateTime.now();

        // 呼叫Bot發送訊息
        Message sentMsg = hrmsTelegramBot.sendRemoteAudit(chatId, now, deadlineMin, uuid);

        if (sentMsg != null) {
            log.info("已發送稽核通知給: {}({}) (ChatID: {}, MsgID: {})",
                    employee.getNickName(), employee.getFullName(), chatId, sentMsg.getMessageId());

            // 創建並保存稽核記錄
            RemoteAuditRecord record = createAuditRecord(employee, uuid, chatId, now, deadlineMin);
            saveAuditRecord(record);
        } else {
            log.error("發送稽核通知失敗: {}({})", employee.getAccount(), employee.getFullName());
        }
    }

    /**
     * 創建稽核記錄
     */
    private RemoteAuditRecord createAuditRecord(Employee employee, String uuid, Long chatId,
                                                LocalDateTime now, Integer deadlineMin) {
        RemoteAuditRecord record = new RemoteAuditRecord();
        record.setEmployeeId(employee.getId().longValue());
        record.setChatId(employee.getTelegram());
        record.setChannel("TELEGRAM");
        record.setAuditType("RANDOM");
        record.setSessionToken(uuid);
        record.setSentAt(now);
        record.setDeadlineAt(now.plusMinutes(deadlineMin));
        record.setStatus((int) RemoteAuditStatus.SEND.getValue());
        return record;
    }

    /**
     * 保存稽核記錄
     */

    private void saveAuditRecord(RemoteAuditRecord record) {
        try {
            remoteAuditRecordService.save(record);
            log.info("保存稽核記錄成功，記錄ID: {}", record.getId());
            String cacheKey = RedisKeyConstants.REMOTE_AUDIT_RECORD + record.getEmployeeId();
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("保存稽核記錄失敗，記錄內容: {}", record, e);
        }
    }

    /**
     * 均匀分布策略：基于固定概率随机决定是否发送稽核
     * 目前固定均勻分布，未來可以擴增權重，輪詢隨機
     *
     * @param auditConfig 稽核配置
     * @return true表示需要发送稽核，false表示不需要
     */
    private boolean
    shouldSendAudit(RemoteAuditConfig auditConfig) {
        // 获取随机概率配置（0-100之间的值）
        Integer randomProbability = auditConfig.getRandomValue();

        // 如果未配置概率，默认为100%（总是发送）
        if (randomProbability == null) {
            randomProbability = 100;
        }

        // 如果概率为0，则不发送
        if (randomProbability <= 0) {
            log.debug("均匀分布策略: 随机稽核概率设置为0，不发送稽核请求");
            return false;
        }

        // 如果概率为100，则总是发送
        if (randomProbability >= 100) {
            log.debug("均匀分布策略: 随机稽核概率设置为100%，总是发送稽核请求");
            return true;
        }

        // 生成0-100之间的随机数
        int randomValue = ThreadLocalRandom.current().nextInt(100);

        // 判断是否需要发送稽核
        boolean shouldSend = randomValue < randomProbability;

        log.info("均匀分布策略: 随机稽核判断: 随机值={}, 概率阈值={}, 结果={}",
                randomValue, randomProbability, shouldSend ? "发送" : "不发送");

        return shouldSend;
    }


    /**
     * 發送決策枚舉
     */
    private enum SendDecision {
        SEND_RETRY,    // 需要發送（重試場景，強制發送）
        SEND_NORMAL,   // 需要發送（常規場景，需經過隨機）
        DO_NOT_SEND    // 不發送
    }

    /**
     * 判斷是否需要發送新的稽核請求
     * @return SendDecision 決策結果
     */
    private SendDecision shouldSendNewAuditRequest(List<RemoteAuditRecord> records, RemoteAuditConfig config) {
        if (records.isEmpty()) {
            return SendDecision.SEND_NORMAL;
        }
        RemoteAuditRecord last = records.getLast();
        // 檢查最後一條記錄是否在重試延遲時間內
        // 如果最後一條記錄不是已回覆狀態，且發送時間已超過重試延遲時間，則允許發送
        if (!Objects.equals(last.getStatus(), (int) RemoteAuditStatus.RESPONDED.getValue()) &&
                last.getSentAt().isBefore(LocalDateTime.now().minusMinutes(config.getRetryDelayMin()))) {
            // 這是因為超時/未回應觸發的重試，直接返回 SEND_RETRY，跳過隨機
            int unrespondedCount = calculateConsecutiveUnrespondedCount(records);
//            log.info("連續未回應或超時次數: {}", unrespondedCount);
            // 如果連續超時次數未達上限，則發送新的稽核請求
            if(unrespondedCount < config.getRetryMaxTimes()) return SendDecision.SEND_RETRY;
        }

        // 檢查是否在排除最近時間內
        if (last.getSentAt().isAfter(LocalDateTime.now().minusMinutes(config.getExcludeRecentMin()))) {
            return SendDecision.DO_NOT_SEND;
        }

        return SendDecision.SEND_NORMAL;
    }

}