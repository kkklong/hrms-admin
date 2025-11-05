package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.common.alertRecipient.service.AttendanceAlertRecipientService;
import com.hrms.common.telegram.TelegramService;
import com.hrms.entity.Config;
import com.hrms.entity.Employee;
import com.hrms.entity.LeaveRecordsDateTime;
import com.hrms.entity.RawAttendanceRecords;
import com.hrms.enums.AlertEventType;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.RawAttendanceRecordsBO;
import com.hrms.model.vo.RawAttendanceRecordsVO;
import com.hrms.repository.LeaveRecordsDateTimeRepository;
import com.hrms.repository.RawAttendanceRecordsRepository;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RawAttendanceRecordsService 處理原始打卡資料相關的業務邏輯。
 * 主要職責：
 * - 提供查詢原始打卡記錄的介面
 * - 每日排程檢查未打卡的員工（上班、下班）
 * - 判斷請假或異常狀況並發送通知（Email / Telegram）
 *
 * @author System
 * @since 2024-10-05
 */
@Slf4j
@Service
@Transactional
public class RawAttendanceRecordsService extends ServiceImpl<RawAttendanceRecordsRepository, RawAttendanceRecords> {

    @Resource
    private RawAttendanceRecordsRepository rawAttendanceRecordsRepository;
    @Resource
    private ConfigService configService;
    @Resource
    private LeaveRecordsDateTimeRepository leaveRecordsDateTimeRepository;
    @Resource
    private EmailService emailService;
    @Resource
    private TelegramService telegramService;

//    @Resource
//    private AttendanceAlertRecipientService attendanceAlertRecipientService;

    public List<RawAttendanceRecordsVO> query(RawAttendanceRecordsBO rawAttendanceRecordsBO) {
        if (rawAttendanceRecordsBO.getShowDetail()) {
            return rawAttendanceRecordsRepository.queryAll(rawAttendanceRecordsBO);
        } else {
            return rawAttendanceRecordsRepository.query(rawAttendanceRecordsBO);
        }
    }

//    public void checkClockIn(String shiftTypeKey) {
//        ShiftTimeInfo timeInfo = resolveShiftTimeAndDate(shiftTypeKey, true);
//        log.info("檢查上班未打卡記錄 班別：{}({}) 開始", timeInfo.shiftType().getName(), timeInfo.shiftType().getConfigKey());
//        List<Employee> employees = rawAttendanceRecordsRepository.queryNonClock(
//                timeInfo.attendanceDate(), shiftTypeKey, timeInfo.expectedClockInTime().minusHours(1), LocalDateTime.now());
//        List<Employee> ccEmployees = attendanceAlertRecipientService.getCcEmployeesByEvent(AlertEventType.CLOCK_IN_MISS);
//        List<String> ccEmails = ccEmployees.stream()
//                .map(Employee::getEmail)
//                .filter(e -> e != null && !e.isBlank())
//                .distinct()
//                .toList();
//        processNonClockEmployees(employees, timeInfo.shiftType().getName(), "上班未打卡提醒", timeInfo.attendanceDate(),
//                timeInfo.expectedClockInTime(), timeInfo.expectedClockOutTime(), true, ccEmails);
//        log.info("檢查上班未打卡記錄 班別：{}({}) 結束", timeInfo.shiftType().getName(), timeInfo.shiftType().getConfigKey());
//    }

    public void checkClockOut(String shiftTypeKey) {
        ShiftTimeInfo timeInfo = resolveShiftTimeAndDate(shiftTypeKey, false);
        log.info("檢查下班未打卡記錄 班別：{}({}) 開始", timeInfo.shiftType().getName(), timeInfo.shiftType().getConfigKey());
        List<Employee> employees = rawAttendanceRecordsRepository.queryNonClock(
                timeInfo.attendanceDate(), shiftTypeKey, timeInfo.expectedClockOutTime().minusHours(1), LocalDateTime.now());
        processNonClockEmployees(employees, timeInfo.shiftType().getName(), "下班未打卡提醒", timeInfo.attendanceDate(),
                timeInfo.expectedClockInTime(), timeInfo.expectedClockOutTime(), false, Collections.emptyList());
        log.info("檢查下班未打卡記錄 班別：{}({}) 結束", timeInfo.shiftType().getName(), timeInfo.shiftType().getConfigKey());
    }

    /**
     * 根據班別設定取得應出勤日期與上下班時間
     *
     * @param shiftTypeKey 班別代碼
     * @param isStartCheck 是否為上班檢查
     * @return 時間資料包裝物件
     */
    private ShiftTimeInfo resolveShiftTimeAndDate(String shiftTypeKey, boolean isStartCheck) {
        Config shiftType = configService.getShiftType().stream()
                .filter(config -> config.getConfigKey().equals(shiftTypeKey))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_SHIFT_TYPE));

        LocalDate baseDate = LocalDate.now();
        LocalDateTime startTime = baseDate.atTime(LocalTime.parse(shiftType.getConfigValue1()));
        LocalDateTime endTime = baseDate.atTime(LocalTime.parse(shiftType.getConfigValue2()));

        // 處理跨日班別
        if (startTime.isAfter(endTime)) {
            if (isStartCheck) {
                endTime = endTime.plusDays(1);
            } else {
                startTime = startTime.minusDays(1);
                baseDate = startTime.toLocalDate();
            }
        }

        return new ShiftTimeInfo(shiftType, baseDate, startTime, endTime);
    }


    private record ShiftTimeInfo(Config shiftType, LocalDate attendanceDate,
                                 LocalDateTime expectedClockInTime, LocalDateTime expectedClockOutTime) {
    }

    /**
     * 處理未打卡員工的條件檢查及通知邏輯
     *
     * @param employees            未打卡員工清單
     * @param shiftTypeName        班別名稱
     * @param notificationTitle    通知標題
     * @param attendanceDate       出勤日期
     * @param expectedClockInTime  預期上班時間
     * @param expectedClockOutTime 預期下班時間
     * @param isClockIn            是否為上班打卡檢查
     */
    private void processNonClockEmployees(List<Employee> employees, String shiftTypeName, String notificationTitle,
                                          LocalDate attendanceDate, LocalDateTime expectedClockInTime, LocalDateTime expectedClockOutTime,
                                          boolean isClockIn, List<String> ccEmails) {
        for (Employee employee : employees) {
            if (employee.getStatus() != 1) {
                log.info("略過非在職員工 account：{}", employee.getAccount());
                continue;
            }
            if (employee.getEmail() == null || employee.getEmail().isBlank()) {
                log.info("略過 email 無效員工 account：{}", employee.getAccount());
                continue;
            }
            List<LeaveRecordsDateTime> leaveRecordsDateTimes = leaveRecordsDateTimeRepository.findByDate(
                    employee.getId(), expectedClockInTime, expectedClockOutTime);
            boolean hasLeave;
            if (isClockIn) {
                hasLeave = !leaveRecordsDateTimes.isEmpty() && leaveRecordsDateTimes.getFirst().getStartDate().equals(expectedClockInTime);
            } else {
                hasLeave = !leaveRecordsDateTimes.isEmpty() && leaveRecordsDateTimes.getLast().getEndDate().equals(expectedClockOutTime);
            }
            if (hasLeave) {
                log.info("員工 account：{} 有請假，跳過{}未打卡提醒", employee.getAccount(), isClockIn ? "上班" : "下班");
                continue;
            }
            String content = String.format("%s 班別：%s 查無%s打卡記錄！", attendanceDate, shiftTypeName, isClockIn ? "上班" : "下班");
            try {
                emailService.sendEmail(employee.getEmail(), notificationTitle, content);
                // 只有「上班未打卡」且有清單時，再寄出通知
                if (isClockIn && ccEmails != null && !ccEmails.isEmpty()) {
                    for (String cc : ccEmails) {
                        if (cc == null || cc.isBlank()) continue;
                        if (cc.equalsIgnoreCase(employee.getEmail())) continue;
                        String ccSubject = "[通知]人員" + notificationTitle;
                        String ccContent = String.format(
                                "員工 %s  於 %s   班別：%s 查無上班打卡記錄！",
                                employee.getFullName(),
                                attendanceDate, shiftTypeName
                        );
                        emailService.sendEmail(cc, ccSubject, ccContent);
                    }
                }
//               telegramService.sendMessage(employee.getNickName() + " " + content);
                log.info("發送{}未打卡提醒成功 account：{} email：{} content：{}", isClockIn ? "上班" : "下班", employee.getAccount(), employee.getEmail(), content);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.info("發送{}未打卡提醒失敗 account：{} email：{} content：{}", isClockIn ? "上班" : "下班", employee.getAccount(), employee.getEmail(), content, e);
            }
        }
    }

    // ---- 測試用dao ----

    public List<RawAttendanceRecords> voToEntities(List<RawAttendanceRecordsVO> voList) {
        List<RawAttendanceRecords> entities = new ArrayList<>();

        for (RawAttendanceRecordsVO vo : voList) {
            String account = vo.getAccount();

            if (vo.getFirstCheckInTime() != null) {
                RawAttendanceRecords checkInRecord = new RawAttendanceRecords();
                checkInRecord.setAccount(account);
                checkInRecord.setDateTime(vo.getFirstCheckInTime());
                entities.add(checkInRecord);
            }

            if (vo.getLastCheckOutTime() != null) {
                RawAttendanceRecords checkOutRecord = new RawAttendanceRecords();
                checkOutRecord.setAccount(account);
                checkOutRecord.setDateTime(vo.getLastCheckOutTime());
                entities.add(checkOutRecord);
            }
        }
        return entities;
    }

    public boolean updaterawAttendance(List<RawAttendanceRecords> raws, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            if (raws.isEmpty()) {
                return false;
            }

            // 查詢該時間區間內，這些帳號的現有資料
            List<RawAttendanceRecords> existList = rawAttendanceRecordsRepository.selectList(
                    new QueryWrapper<RawAttendanceRecords>()
                            .in("account", raws.stream()
                                    .map(RawAttendanceRecords::getAccount)
                                    .collect(Collectors.toSet()))
                            .between("date_time", startDate, endDate)
            );
            // 組合現有資料的 key (account + dateTime)
            Set<String> existKeys = existList.stream()
                    .map(e -> e.getAccount() + "|" + e.getDateTime())
                    .collect(Collectors.toSet());

            // 過濾掉已存在的資料
            List<RawAttendanceRecords> filteredList = raws.stream()
                    .filter(e -> !existKeys.contains(e.getAccount() + "|" + e.getDateTime()))
                    .toList();

            // 寫入新資料
            if (!filteredList.isEmpty()) {
                this.saveBatch(filteredList);
                return true;
            }
        } catch (PersistenceException e) {
            throw new ServiceException(ErrorCode.DATABASE_ERROR);
        }
        return false;
    }
}
