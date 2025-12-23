package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.*;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.ShiftAdjustmentRequestApprovalStage;
import com.hrms.enums.ShiftAdjustmentRequestStatus;
import com.hrms.exception.ServiceException;
import com.hrms.model.HistoryReview;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.ShiftAdjustmentCancelBO;
import com.hrms.model.vo.ShiftAdjustmentRequestVO;
import com.hrms.model.vo.ShiftSchedulePeriodVo;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.ShiftAdjustmentRequestRepository;
import com.hrms.repository.ShiftSchedulesRepository;
import com.hrms.util.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.hrms.util.DepartmentUtils.*;
import static com.hrms.util.HistoryUtils.appendHistoryReview;

/**
 * <p>
 * 班表調整申請單 (含審核暫鎖控制) 服務實現類
 * </p>
 *
 * @author System
 * @since 2025-11-06
 */
@Slf4j
@Service
@Transactional
public class ShiftAdjustmentRequestService extends ServiceImpl<ShiftAdjustmentRequestRepository, ShiftAdjustmentRequest> {

    @Resource
    private ShiftSchedulesRepository shiftSchedulesRepository;
    @Resource
    private ShiftAdjustmentRequestRepository shiftAdjustmentRequestRepository;
    @Resource
    private DepartmentRepository departmentRepository;
    @Resource
    private ApprovalFlowConfigService approvalFlowConfigService;
    @Resource
    private EmployeeRepository employeeRepository;
    @Resource
    private EmailService emailService;
    @Resource
    private ShiftSchedulesService shiftSchedulesService;
    @Resource
    private ConfigService configService;

    /**
     * 取得待審核的班表調整申請（依登入者權限與目前節點）
     * - HR / Admin：可看全部（status=1 審核中）
     * - 其他：僅可看自己有權限審核的節點
     * 獲取待審核與可檢視的班表調整申請
     */
    public List<ShiftAdjustmentRequestVO> getPendingShiftAdjustments(UserInfo userInfo) {
        Integer userId = userInfo.getId();
        Employee user = employeeRepository.selectById(userId);
        Department department = departmentRepository.selectById(user.getDepartmentId());

        // 使用 Set 去重
        Set<ShiftAdjustmentRequest> resultSet = new HashSet<>();

        // HR 或 Admin 直接取全部
        if (isHRDepartment(department) || isAdminDepartment(department)) {
            List<ShiftAdjustmentRequest> all = shiftAdjustmentRequestRepository.selectList(null);
            return convertToShiftAdjustmentRequestVO(all);
        }

        // 獲取當前用戶管理的部門 ID 列表
        List<Integer> managedDepartmentIds = departmentRepository.findByManagerId(userId)
                .stream().map(Department::getId).toList();

        // 取得所有 "審核中" 的單據
        List<ShiftAdjustmentRequest> allSubmittedRequests = shiftAdjustmentRequestRepository.selectByStatus(ShiftAdjustmentRequestStatus.SUBMITTED.getValue());

        // 全部門與全員工
        List<Employee> allEmployees = employeeRepository.selectList(null);
        Map<Integer, Department> departmentMap = departmentRepository.selectList(null).stream()
                .collect(Collectors.toMap(Department::getId, d -> d));


        //根據部門角色確定審核與檢視邏輯
        if (isTechnicalLeadDepartment(department)) {
            // ---技術長處理邏輯 ---

            // (1) 審核權：取得目前階段為 TECH_LEAD_REVIEW 的單據
            List<ShiftAdjustmentRequest> techApprovalList = filterByStage(allSubmittedRequests, ShiftAdjustmentRequestApprovalStage.TECH_LEAD_REVIEW);
            resultSet.addAll(techApprovalList);

            // (2) 檢視權：過濾部門為非 HR/GM/Admin 的員工，並查看他們的所有單據
            List<Integer> targetEmpIds = allEmployees.stream()
                    .filter(emp -> {
                        Department empDept = departmentMap.get(emp.getDepartmentId());
                        return empDept != null &&
                                !(isHRDepartment(empDept) || isGMDepartment(empDept) || isAdminDepartment(empDept));
                    })
                    .map(Employee::getId)
                    .toList();

            if (!targetEmpIds.isEmpty()) {
                resultSet.addAll(shiftAdjustmentRequestRepository.selectList(
                        new LambdaQueryWrapper<ShiftAdjustmentRequest>().in(ShiftAdjustmentRequest::getApplicantId, targetEmpIds)
                ));
            }

        } else if (isGMDepartment(department)) {
            // --- 總經理處理邏輯 ---
            List<ShiftAdjustmentRequest> gmApprovalList = filterByStage(allSubmittedRequests, ShiftAdjustmentRequestApprovalStage.GM_REVIEW);
            resultSet.addAll(gmApprovalList);
        } else if (isDepartmentManager(userId, department)) {
            // --- 部門主管處理邏輯 ---

            // (1) 審核權：取得目前卡在 LEADER_REVIEW 且 申請人屬於管轄部門 的單據
            List<ShiftAdjustmentRequest> leaderApprovalList = filterByStageAndDepts(allSubmittedRequests, ShiftAdjustmentRequestApprovalStage.LEADER_REVIEW, managedDepartmentIds);
            resultSet.addAll(leaderApprovalList);

            // (2) 檢視權：管轄部門內的所有員工
            List<Integer> deptEmpIds = allEmployees.stream()
                    .filter(emp -> managedDepartmentIds.contains(emp.getDepartmentId()))
                    .map(Employee::getId)
                    .toList();

            if (!deptEmpIds.isEmpty()) {
                // 查詢部門內所有員工記錄
                resultSet.addAll(shiftAdjustmentRequestRepository.selectList(
                        new LambdaQueryWrapper<ShiftAdjustmentRequest>().in(ShiftAdjustmentRequest::getApplicantId, deptEmpIds)
                ));
            }
        }

        //申請者本人要能看到自己的所有單據
        //
        List<ShiftAdjustmentRequest> myRequests = shiftAdjustmentRequestRepository.selectList(
                new LambdaQueryWrapper<ShiftAdjustmentRequest>().eq(ShiftAdjustmentRequest::getApplicantId, userId)
        );
        resultSet.addAll(myRequests);

        // 4. 轉成 List 並排序 (按建立時間倒序)
        List<ShiftAdjustmentRequest> sortedList = new ArrayList<>(resultSet);
        sortedList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return convertToShiftAdjustmentRequestVO(sortedList);
    }

    /**
     * 從列表中過濾出特定審核階段，且申請人屬於特定部門的單據 (給部門主管用)
     */
    private List<ShiftAdjustmentRequest> filterByStageAndDepts(List<ShiftAdjustmentRequest> source,
                                                               ShiftAdjustmentRequestApprovalStage targetStage,
                                                               List<Integer> allowedDeptIds) {
        List<ShiftAdjustmentRequest> result = new ArrayList<>();

        for (ShiftAdjustmentRequest req : source) {
            FlowContext ctx = parseFlowContext(req.getApprovalContext());
            if (currentStage(ctx) == targetStage) {
                // 檢查申請人部門
                Employee applicant = employeeRepository.selectById(req.getApplicantId());
                if (applicant != null && allowedDeptIds.contains(applicant.getDepartmentId())) {
                    result.add(req);
                }
            }
        }
        return result;
    }


    /**
     * 從列表中過濾出特定審核階段的單據
     */
    private List<ShiftAdjustmentRequest> filterByStage(List<ShiftAdjustmentRequest> source, ShiftAdjustmentRequestApprovalStage targetStage) {
        List<ShiftAdjustmentRequest> result = new ArrayList<>();
        for (ShiftAdjustmentRequest req : source) {
            FlowContext ctx = parseFlowContext(req.getApprovalContext());
            if (currentStage(ctx) == targetStage) {
                result.add(req);
            }
        }
        return result;
    }

    /**
     * 查詢個人班表調整紀錄 (查詢所有狀態：草稿、審核中、通過、駁回)
     */
    public List<ShiftAdjustmentRequestVO> queryByApplicantId(Integer applicantId) {
        List<ShiftAdjustmentRequest> list = shiftAdjustmentRequestRepository.selectList(
                new LambdaQueryWrapper<ShiftAdjustmentRequest>()
                        .eq(ShiftAdjustmentRequest::getApplicantId, applicantId)
                        .orderByDesc(ShiftAdjustmentRequest::getCreatedAt)
        );
        return convertToShiftAdjustmentRequestVO(list);
    }


    /**
     * 批量申請調班
     *
     * @param requestShiftMap Key: ShiftScheduleID, Value: TargetShiftType
     */
    @Transactional
    public Long apply(Map<Integer, String> requestShiftMap,
                      String reason,
                      Integer applicantId) {

        if (requestShiftMap == null || requestShiftMap.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_SHIFT_MAP);
        }

        // 驗證並取得所有相關的班表
        List<Integer> shiftIds = new ArrayList<>(requestShiftMap.keySet());
        List<ShiftSchedules> originSchedules = shiftSchedulesRepository.selectBatchIds(shiftIds);
        if (originSchedules.size() != shiftIds.size()) {
            throw new ServiceException(ErrorCode.EMPTY_SHIFT_SCHEDULES);
        }

        // ---申請人必須包含在的班表擁有者之中 ---
        boolean isApplicantInvolved = originSchedules.stream()
                .anyMatch(s -> s.getEmployeeId().equals(applicantId));

        if (!isApplicantInvolved) {
            throw new ServiceException(ErrorCode.APPLICANT_MUST_BE_INVOLVED);
        }

        Map<String, Map<String, String>> jsonStorage = new HashMap<>();

        //基礎狀態檢核
        for (ShiftSchedules s : originSchedules) {
            // 檢查是否為請假狀態 (status=1為請假)
            if (s.getStatus() != null && s.getStatus() == 1) {
                throw new ServiceException(ErrorCode.CANT_APPLY_FOR_SHIFT_CHANGE_WHEN_LEAVE);
            }
            // 檢查是否已被其他單據鎖定
            if (s.getReviewLockRequestId() != null) {
                throw new ServiceException(ErrorCode.SHIFT_LOCKED);
            }
            Map<String, String> detail = new HashMap<>();
            detail.put("original", s.getShiftTypes());
            detail.put("target", requestShiftMap.get(s.getId()));
            jsonStorage.put(String.valueOf(s.getId()), detail);
        }
        //模擬並檢核班表 (人力 + 法規)
        validateBatchAdjustment(originSchedules, requestShiftMap);

        //  建立申請單
        ShiftAdjustmentRequest req = new ShiftAdjustmentRequest();
        req.setApplicantId(applicantId);
        req.setReason(reason);
        req.setStatus(ShiftAdjustmentRequestStatus.SUBMITTED.getValue());
        req.setShiftMap(JsonUtils.toJSON(jsonStorage));

        // 寫入審核路徑快照（僅用 LT_24；故 ge24=false
        Employee applicant = employeeRepository.selectById(applicantId);
        var dept = departmentRepository.selectById(applicant.getDepartmentId());
        List<ShiftAdjustmentRequestApprovalStage> route = approvalFlowConfigService.resolve(applicant, dept, false, ShiftAdjustmentRequestApprovalStage.class);

        if (route != null && !route.isEmpty()) {
            Map<String, Object> ctx = new HashMap<>();
            ctx.put("route", route); // 直接存 Enum 陣列，JsonUtils 會序列化為字串
            ctx.put("current", 0);
            req.setApprovalContext(JsonUtils.toJSON(ctx));
        }

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        appendHistoryReview(req::getHistoryReview,
                req::setHistoryReview,
                "申請班表調整",
                userInfo);

        shiftAdjustmentRequestRepository.insert(req); // 自動帶回 id

        // 3) 暫鎖涉及班表
        List<Long> lockIds = shiftIds.stream().map(Integer::longValue).toList();
        shiftSchedulesRepository.updateReviewLock(req.getId(), lockIds);

        // 4) 通知下階段審核者（比照請假流程）
        notifyNextApprover(req, applicant);
        return req.getId();
    }

    /**
     * 批量檢核核心邏輯
     */
    private void validateBatchAdjustment(List<ShiftSchedules> originSchedules,
                                         Map<Integer, String> modifications) {

        // 人力下限檢核
        validateStaffingLevels(originSchedules, modifications);

        //排班規則檢核 (針對每一位涉及的員工)
        Set<Integer> employeeIds = originSchedules.stream()
                .map(ShiftSchedules::getEmployeeId)
                .collect(Collectors.toSet());

        // 找出變更範圍的日期區間 (取 min 與 max)
        LocalDate minDate = originSchedules.stream().map(ShiftSchedules::getShiftDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = originSchedules.stream().map(ShiftSchedules::getShiftDate).max(LocalDate::compareTo).orElse(LocalDate.now());

        // 取得涵蓋這些日期的完整雙週週期
        List<ShiftSchedulePeriodVo> periods = shiftSchedulesService.getShiftSchedulePeriods(minDate, maxDate);
        if (periods == null || periods.isEmpty()) {
            throw new ServiceException(ErrorCode.INVALID_PERIOD_START_DATE);
        }
        LocalDate queryStart = periods.getFirst().getStartDate();
        LocalDate queryEnd = periods.getLast().getEndDate();

        // 取得例假日班別設定
        List<Config> holidayConfigs = configService.getHoliday();
        Set<String> restDayShiftTypes = holidayConfigs.stream()
                .map(Config::getConfigKey)
                .collect(Collectors.toSet());

        // 對每一位員工進行模擬檢查
        for (Integer empId : employeeIds) {
            // 查詢該員工在此大區間的所有班表
            List<ShiftSchedules> empShifts = shiftSchedulesRepository.findByEmployeeIdAndShiftDateBetween(empId, queryStart, queryEnd);

            // 模擬班表調整後的結果
            applyModificationsInMemory(empShifts, modifications);

            // 連續工作天數與休息時間檢核
            shiftSchedulesService.validateConsecutiveWorkDaysAndRestHours(empShifts);

            // 雙週變形工時 (一例一休) 檢核
            validateDoubleWeekRestRuleWithPeriods(empId, empShifts, restDayShiftTypes, periods);
        }
    }

    /**
     * 模擬班表變更的結果 (只改班別)
     */
    private void applyModificationsInMemory(List<ShiftSchedules> shifts, Map<Integer, String> modifications) {
        for (ShiftSchedules s : shifts) {
            if (modifications.containsKey(s.getId())) {
                s.setShiftTypes(modifications.get(s.getId()));
            }
        }
        // 重新排序確保檢核邏輯正確
        shifts.sort(Comparator.comparing(ShiftSchedules::getShiftDate));
    }


    /**
     * 驗證人力下限
     * 計算每個日期、每個部門在變更後的淨值變化
     */
    private void validateStaffingLevels(List<ShiftSchedules> originSchedules, Map<Integer, String> modifications) {

        // ---人力下限檢核 ---
        // 統計此次變更造成的增減
        Map<String, Integer> netChanges = new HashMap<>();

        for (ShiftSchedules origin : originSchedules) {
            String newType = modifications.get(origin.getId());
            // 如果班別沒變，不影響人力統計
            if (Objects.equals(origin.getShiftTypes(), newType)) {
                continue;
            }
            String keyPrefix = origin.getShiftDate().toString() + "_" + origin.getDepartmentId() + "_";

            // 原班別 Count - 1
            String originKey = keyPrefix + origin.getShiftTypes();
            netChanges.merge(originKey, -1, Integer::sum);

            // 新班別 Count + 1
            String targetKey = keyPrefix + newType;
            netChanges.merge(targetKey, 1, Integer::sum);
        }
        //休假類型的 Set (不需要檢查人力下限的班別)
        Set<String> holidayShiftTypes = configService.getHoliday().stream()
                .map(Config::getConfigKey)
                .collect(Collectors.toSet());

        //針對有減少的班別進行 DB 檢核
        for (Map.Entry<String, Integer> entry : netChanges.entrySet()) {
            int change = entry.getValue();
            if (change >= 0) continue; // 人數增加或持平，不需檢查下限

            String[] parts = entry.getKey().split("_", 3); // Date, DeptId, ShiftType
            if (parts.length < 3) continue;
            String shiftType = parts[2];
            //如果是休假或例假，不需要檢查人力下限
            if (holidayShiftTypes.contains(shiftType)) {
                continue;
            }

            LocalDate date = LocalDate.parse(parts[0]);
            int deptId = Integer.parseInt(parts[1]);
            int currentDbCount = shiftSchedulesRepository.countAvailable(deptId, shiftType, date);
            int minRequired = departmentRepository.findMinRequired(deptId, shiftType);

            if ((currentDbCount + change) < minRequired) {
                throw new ServiceException(ErrorCode.BELOW_THE_LOWER_LIMIT,
                        String.format("日期 %s 部門 %d 班別 %s 人力不足 (變更後: %d, 下限: %d)",
                                date, deptId, shiftType, (currentDbCount + change), minRequired));
            }
        }
    }

    /**
     * 檢查員工模擬班表是否符合雙週規則：
     * - 每週 REGULAR_HOLIDAY 1 天
     * - 雙週 REGULAR_HOLIDAY 共 2 天，REST_HOLIDAY 共 2 天
     */
    private void validateDoubleWeekRestRuleWithPeriods(Integer employeeId,
                                                       List<ShiftSchedules> shifts,
                                                       Set<String> restDayShiftTypes,
                                                       List<ShiftSchedulePeriodVo> periods) {
        if (periods == null || periods.isEmpty()) {
            return; // 沒有可檢查的雙週區間
        }

        // 將ShiftSchedules轉成map，方便搜尋（同日多筆以最後一筆覆蓋）
        Map<LocalDate, ShiftSchedules> shiftByDate = shifts.stream()
                .collect(Collectors.toMap(ShiftSchedules::getShiftDate, s -> s, (a, b) -> b));

        String employeeName = Optional.ofNullable(employeeRepository.selectById(employeeId))
                .map(Employee::getNickName)
                .orElse(String.valueOf(employeeId));

        // 檢查雙週是否符合規則
        for (ShiftSchedulePeriodVo period : periods) {
            LocalDate start = period.getStartDate();
            LocalDate end = period.getEndDate();
            LocalDate week1End = start.plusDays(6);

            int week1Regular = 0;       // 第1週 REGULAR_HOLIDAY 次數
            int week2Regular = 0;       // 第2週 REGULAR_HOLIDAY 次數
            int restHolidayDays = 0;    // 兩週內 REST_HOLIDAY 總數

            // 掃描此雙週的 14 天
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                ShiftSchedules s = shiftByDate.get(d);
                if (s == null) continue;

                String type = s.getShiftTypes();
                if (!restDayShiftTypes.contains(type)) continue;

                boolean inWeek1 = !d.isAfter(week1End);

                if ("REGULAR_HOLIDAY".equals(type)) {
                    if (inWeek1) week1Regular++;
                    else week2Regular++;
                } else if ("REST_HOLIDAY".equals(type)) {
                    restHolidayDays++;
                }
            }

            int totalRegular = week1Regular + week2Regular;

            // 規則：每週 1 天例假（REGULAR_HOLIDAY），雙週合計 2 天例假 + 2 天休假（REST_HOLIDAY）
            if (week1Regular != 1 || week2Regular != 1 || totalRegular != 2 || restHolidayDays != 2) {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("員工：").append(employeeName).append("\r")
                        .append("雙週日期範圍：").append(start).append(" 至 ").append(end).append("\r")
                        .append("規定：每週例假日 1 天，雙週共 2 天例假日、2 天休假日").append("\r")
                        .append("實際：第1週例假日=").append(week1Regular)
                        .append(" 天，第2週例假日=").append(week2Regular)
                        .append(" 天，雙週例假日共=").append(totalRegular)
                        .append(" 天，休假日=").append(restHolidayDays).append(" 天");

                throw new ServiceException(ErrorCode.REST_DAY_RULE_VIOLATION, errorMsg.toString());
            }
        }
    }


    /**
     * 通知下階段審核者
     */
    private void notifyNextApprover(ShiftAdjustmentRequest req, Employee applicant) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        FlowContext ctx = parseFlowContext(req.getApprovalContext());
        ShiftAdjustmentRequestApprovalStage nextStage = currentStage(ctx);
        if (nextStage == null) return;

        List<Employee> approvers = resolveApprovers(nextStage, applicant);

        String detailsHtml = buildAdjustmentDetailsHtml(req);

        for (Employee approver : approvers) {
            if (approver.getEmail() != null && !approver.getEmail().isEmpty()) {
                try {
                    String text = String.format(
                            "%s 您好:<br><br>" +
                                    "申請人：<b>%s</b> 提交了班表調整申請<br>" +
                                    "申請原因：%s<br><br>" +
                                    "<b>調整內容：</b><br>%s" +
                                    "<br>請盡速登入系統審核，謝謝。",
                            approver.getNickName(),
                            applicant.getNickName(),
                            req.getReason(),
                            detailsHtml
                    );
                    emailService.sendEmail(approver.getEmail(), "班表調整審核通知", text);
                } catch (Exception e) {
                    appendHistoryReview(req::getHistoryReview, req::setHistoryReview,
                            "郵件發送失敗: " + approver.getFullName(), userInfo);
                    shiftAdjustmentRequestRepository.updateById(req);
                }
            }
        }
    }


    private String buildAdjustmentDetailsHtml(ShiftAdjustmentRequest req) {
        if (req.getShiftMap() == null) return "無調整內容";

        // 解析 Map
        Map<String, Map> detailsMap = JsonUtils.toMap(req.getShiftMap(), Map.class);
        if (detailsMap == null || detailsMap.isEmpty()) return "無調整明細";

        List<Integer> ids = detailsMap.keySet().stream().map(Integer::valueOf).toList();
        List<ShiftSchedules> schedules = shiftSchedulesRepository.selectBatchIds(ids);

        Set<Integer> empIds = schedules.stream().map(ShiftSchedules::getEmployeeId).collect(Collectors.toSet());
        Map<Integer, String> empNameMap = employeeRepository.selectBatchIds(empIds).stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getNickName));

        // 日期排序
        schedules.sort(Comparator.comparing(ShiftSchedules::getShiftDate));

        StringBuilder sb = new StringBuilder();
        sb.append("<ul style='list-style-type: disc; padding-left: 20px;'>");

        for (ShiftSchedules s : schedules) {
            String empName = empNameMap.getOrDefault(s.getEmployeeId(), String.valueOf(s.getEmployeeId()));
            Map<String, String> detail = detailsMap.get(String.valueOf(s.getId()));
            String original = detail.get("original");
            String target = detail.get("target");

            // 格式： 2023-11-25 [Billy] 早班 -> 晚班
            sb.append(String.format("<li>%s [%s] %s &rarr; <b>%s</b></li>",
                    s.getShiftDate(), empName, original, target));
        }
        sb.append("</ul>");
        return sb.toString();
    }

    /**
     * 最終核准
     */
    @Transactional
    public void approve(Long requestId, Integer approverUserId) {
        ShiftAdjustmentRequest req = shiftAdjustmentRequestRepository.selectByIdForUpdate(requestId);
        if (req == null) throw new ServiceException(ErrorCode.NOT_FOUND);

        Map<String, Map> detailsMap = JsonUtils.toMap(req.getShiftMap(), Map.class);
        if (detailsMap == null || detailsMap.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_SHIFT_MAP, "無變更明細");
        }

        Map<Integer, String> finalShiftMap = new HashMap<>();
        detailsMap.forEach((k, v) -> {
            Object targetObj = v.get("target");
            if (targetObj != null) {
                finalShiftMap.put(Integer.valueOf(k), String.valueOf(targetObj));
            }
        });

        // 鎖住所有被暫鎖的班表
        List<ShiftSchedules> lockedSchedules = shiftSchedulesRepository.selectByReviewLockRequestIdForUpdate(requestId);
        if (lockedSchedules.isEmpty()) throw new ServiceException(ErrorCode.CONFIG_ID_NOT_FOUND, "找不到鎖定的班表");

        // 二次檢核
        validateBatchAdjustment(lockedSchedules, finalShiftMap);

        for (Map.Entry<Integer, String> entry : finalShiftMap.entrySet()) {
            Integer shiftId = entry.getKey();
            String targetType = entry.getValue();
            // 只更新班別
            ShiftSchedules updateEntity = new ShiftSchedules();
            updateEntity.setId(shiftId);
            updateEntity.setShiftTypes(targetType);
            shiftSchedulesRepository.updateById(updateEntity);
        }

        // 清鎖
        shiftSchedulesRepository.clearReviewLock(requestId);
        req.setStatus(ShiftAdjustmentRequestStatus.APPROVED.getValue());
        setContextToCompleted(req);

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        appendHistoryReview(
                req::getHistoryReview,
                req::setHistoryReview,
                "審核通過",
                userInfo
        );
        shiftAdjustmentRequestRepository.updateById(req);
    }

    /**
     * 駁回
     */
    @Transactional
    public void reject(Long requestId, String rejectReason, Integer approverUserId) {
        ShiftAdjustmentRequest req = shiftAdjustmentRequestRepository.selectById(requestId);
        if (req == null) throw new ServiceException(ErrorCode.NOT_FOUND);

        shiftSchedulesRepository.clearReviewLock(requestId);

        req.setStatus(ShiftAdjustmentRequestStatus.REJECTED.getValue());
        setContextToCompleted(req);

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        appendHistoryReview(
                req::getHistoryReview,
                req::setHistoryReview,
                "駁回",
                userInfo
        );
        shiftAdjustmentRequestRepository.updateById(req);
    }

    /**
     * 取消班表調整申請
     */
    @Transactional
    public void cancelShiftAdjustment(ShiftAdjustmentCancelBO cancelBO) {
        ShiftAdjustmentRequest req = shiftAdjustmentRequestRepository.selectById(cancelBO.getId());
        if (req == null) throw new ServiceException(ErrorCode.NOT_FOUND);

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee canceller = employeeRepository.selectById(userInfo.getId());

        // 僅允許 "送審中(SUBMITTED)" 的紀錄取消
        if (!Objects.equals(req.getStatus(), ShiftAdjustmentRequestStatus.SUBMITTED.getValue())) {
            throw new ServiceException(ErrorCode.SHIFT_ADJUSTMENT_CANCEL_FAIL);
        }

        //驗證取消操作者是否為申請人本人
        if (!canceller.getId().equals(req.getApplicantId())) {
            throw new ServiceException(ErrorCode.NO_CANCEL_PERMISSION);
        }

        //解除班表鎖定，讓班表恢復可被排班狀態
        shiftSchedulesRepository.clearReviewLock(req.getId());

        //更新狀態為 CANCELLED
        req.setStatus(ShiftAdjustmentRequestStatus.CANCELLED.getValue());
        setContextToCompleted(req);

        appendHistoryReview(
                req::getHistoryReview,
                req::setHistoryReview,
                "撤銷",
                userInfo
        );

        //寄信通知前面已經核准過的主管
        if (req.getHistoryReview() != null && !req.getHistoryReview().isEmpty()) {
            try {
                List<HistoryReview> reviewList = JsonUtils.toList(req.getHistoryReview(), HistoryReview.class);

                if (reviewList != null) {
                    Set<Integer> emailRecipients = reviewList.stream()
                            // 篩選出 action 包含 "核准" 的紀錄
                            .filter(r -> r.getAction() != null && r.getAction().contains("核准"))
                            .map(HistoryReview::getId)
                            .collect(Collectors.toSet());

                    String subject = "班表調整申請取消通知";
                    Employee applicant = employeeRepository.selectById(req.getApplicantId());

                    for (Integer recipientId : emailRecipients) {
                        Employee recipient = employeeRepository.selectById(recipientId);
                        if (recipient != null && recipient.getEmail() != null && !recipient.getEmail().isEmpty()) {
                            try {
                                String text = String.format("%s 您好:<br>申請人 <b>%s</b> 已取消了班表調整申請。<br>請知悉，無需再進行後續審核。",
                                        recipient.getNickName(), applicant.getNickName());
                                emailService.sendEmail(recipient.getEmail(), subject, text);
                            } catch (Exception e) {
                                log.error("取消通知郵件發送失敗: {}", recipient.getNickName(), e);
                                appendHistoryReview(req::getHistoryReview,
                                        req::setHistoryReview, "取消通知郵件發送失敗: " + recipient.getNickName(), userInfo);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("解析歷史紀錄失敗或發送郵件流程錯誤", e);
            }
        }
        shiftAdjustmentRequestRepository.updateById(req);
    }

    /**
     * 單一節點審核通過（由審核頁面呼叫）
     * - 讀取 approval_context 的 route/current
     * - 驗證目前節點 == 傳入 stage
     * - 若為最後一關：呼叫 approve()
     * - 否則：current+1 並寫回 approval_context，通知下一關
     */
    @Transactional
    public void approveStage(Long requestId, ShiftAdjustmentRequestApprovalStage stage, Integer approverUserId, String remark) {
        ShiftAdjustmentRequest req = shiftAdjustmentRequestRepository.selectByIdForUpdate(requestId);
        if (req == null) throw new ServiceException(ErrorCode.NOT_FOUND);

        FlowContext ctx = parseFlowContext(req.getApprovalContext());
        if (ctx.invalid()) throw new ServiceException(ErrorCode.INVALID_FLOW_JSON);

        ShiftAdjustmentRequestApprovalStage expectedStage = currentStage(ctx);
        if (expectedStage == null || !expectedStage.equals(stage)) {
            throw new ServiceException(ErrorCode.INVALID_FLOW_JSON);
        }

        Employee approver = employeeRepository.selectById(approverUserId);
        Department approverDept = departmentRepository.selectById(approver.getDepartmentId());

        Employee applicant = employeeRepository.selectById(req.getApplicantId());
        Department applicantDept = departmentRepository.selectById(applicant.getDepartmentId());

        if (!hasApprovalPermissionForAdjustment(approverDept, applicantDept, approver, expectedStage)) {
            throw new ServiceException(ErrorCode.NO_APPROVAL_PERMISSION);
        }

        // 最後一關 → 完成；否則推進
        if (ctx.current == ctx.route.size() - 1) {
            approve(requestId, approverUserId);
            return;
        }
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        appendHistoryReview(
                req::getHistoryReview,
                req::setHistoryReview,
                "核准",
                userInfo
        );
        shiftAdjustmentRequestRepository.updateById(req);
        updateContextCurrent(requestId, ctx, ctx.current + 1);
        notifyNextApprover(req, applicant);
    }

    private List<ShiftAdjustmentRequestVO> convertToShiftAdjustmentRequestVO(List<ShiftAdjustmentRequest> reqList) {
        if (reqList == null || reqList.isEmpty()) {
            return Collections.emptyList();
        }
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee approver = employeeRepository.selectById(userInfo.getId());
        Department approverDept = departmentRepository.selectById(approver.getDepartmentId());

        // 取得所有申請人 ID
        Set<Integer> applicantIds = reqList.stream()
                .map(ShiftAdjustmentRequest::getApplicantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        //批量查詢員工資料
        Map<Integer, Employee> employeeMap = new HashMap<>();
        if (!applicantIds.isEmpty()) {
            List<Employee> employees = employeeRepository.selectBatchIds(applicantIds);
            employeeMap = employees.stream()
                    .collect(Collectors.toMap(Employee::getId, e -> e));
        }

        // 批量查詢申請人部門資料
        Set<Integer> deptIds = employeeMap.values().stream()
                .map(Employee::getDepartmentId)
                .collect(Collectors.toSet());
        Map<Integer, Department> deptMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<Department> departments = departmentRepository.selectBatchIds(deptIds);
            deptMap = departments.stream()
                    .collect(Collectors.toMap(Department::getId, d -> d));
        }

        Map<Integer, Employee> finalEmployeeMap = employeeMap;
        Map<Integer, Department> finalDeptMap = deptMap;

        return reqList.stream().map(req -> {
            ShiftAdjustmentRequestVO vo = new ShiftAdjustmentRequestVO();
            vo.setId(req.getId());
            vo.setApplicantId(req.getApplicantId());
            vo.setReason(req.getReason());
            vo.setStatus(req.getStatus());
            vo.setApprovalContext(req.getApprovalContext());
            vo.setCreatedAt(req.getCreatedAt());
            vo.setHistoryReview(req.getHistoryReview());
            vo.setEligibleForApproval(false);

            Employee applicant = finalEmployeeMap.get(req.getApplicantId());
            if (applicant != null) {
                vo.setDepartmentId(applicant.getDepartmentId());
                vo.setApplicantName(applicant.getNickName());

                if (Objects.equals(req.getStatus(), ShiftAdjustmentRequestStatus.SUBMITTED.getValue())) {
                    Department applicantDept = finalDeptMap.get(applicant.getDepartmentId());
                    FlowContext ctx = parseFlowContext(req.getApprovalContext());
                    ShiftAdjustmentRequestApprovalStage currentStage = currentStage(ctx);
                    if (currentStage != null && applicantDept != null) {
                        boolean canApprove = hasApprovalPermissionForAdjustment(approverDept, applicantDept, approver, currentStage);
                        vo.setEligibleForApproval(canApprove);
                    }
                }
            }
            if (req.getShiftMap() != null) {
                Map<String, Map> details = JsonUtils.toMap(req.getShiftMap(), Map.class);

                if (details != null) {
                    Map<Integer, String> targetMap = new HashMap<>();
                    Map<Integer, String> originalMap = new HashMap<>();

                    details.forEach((k, v) -> {
                        Integer shiftId = Integer.valueOf(k);
                        targetMap.put(shiftId, String.valueOf(v.get("target")));
                        originalMap.put(shiftId, String.valueOf(v.get("original")));
                    });
                    vo.setShiftMap(targetMap);
                    vo.setOriginalShiftMap(originalMap);
                }
            }
            return vo;
        }).toList();
    }

    /**
     * 單一節點駁回（由審核頁面呼叫）
     * - 直接走業務駁回，釋放暫鎖與更新單據狀態
     */
    @Transactional
    public void rejectStage(Long requestId, ShiftAdjustmentRequestApprovalStage stage, Integer approverUserId, String reason) {
        ShiftAdjustmentRequest req = shiftAdjustmentRequestRepository.selectByIdForUpdate(requestId);
        if (req == null) throw new ServiceException(ErrorCode.NOT_FOUND);

        // 驗證 Stage
        FlowContext ctx = parseFlowContext(req.getApprovalContext());
        ShiftAdjustmentRequestApprovalStage currentStage = currentStage(ctx);
        if (currentStage == null || !currentStage.equals(stage)) {
            throw new ServiceException(ErrorCode.INVALID_FLOW_JSON);
        }

        // --- 【新增】權限檢核 ---
        Employee approver = employeeRepository.selectById(approverUserId);
        Department approverDept = departmentRepository.selectById(approver.getDepartmentId());
        Employee applicant = employeeRepository.selectById(req.getApplicantId());
        Department applicantDept = departmentRepository.selectById(applicant.getDepartmentId());

        if (!hasApprovalPermissionForAdjustment(approverDept, applicantDept, approver, currentStage)) {
            throw new ServiceException(ErrorCode.NO_APPROVAL_PERMISSION);
        }
        // --------------------

        // 執行駁回
        reject(requestId, reason, approverUserId);
    }


    /**
     * 審核上下文封裝
     */
    private static class FlowContext {
        final List<String> route;
        final int current;

        FlowContext(List<String> route, int current) {
            this.route = route;
            this.current = current;
        }

        boolean invalid() {
            return route == null || route.isEmpty() || current < 0 || current >= route.size();
        }
    }

    /**
     * 解析 approval_context JSON → FlowContext
     */
    private FlowContext parseFlowContext(String ctxJson) {
        if (ctxJson == null || ctxJson.isEmpty()) return new FlowContext(null, -1);
        Map<String, Object> map = JsonUtils.toMap(ctxJson);
        @SuppressWarnings("unchecked")
        List<Object> raw = (List<Object>) map.get("route");
        if (raw == null || raw.isEmpty()) return new FlowContext(null, -1);
        List<String> route = new ArrayList<>(raw.size());
        for (Object o : raw) route.add(String.valueOf(o));
        int current = 0;
        Object curObj = map.get("current");
        if (curObj instanceof Number) current = ((Number) curObj).intValue();
        return new FlowContext(route, current);
    }

    /**
     * 由 FlowContext 取得目前節點 Enum；若不合法回傳 null
     */
    private ShiftAdjustmentRequestApprovalStage currentStage(FlowContext ctx) {
        if (ctx == null || ctx.invalid()) return null;
        try {
            return ShiftAdjustmentRequestApprovalStage.valueOf(ctx.route.get(ctx.current));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 將 current 寫回 DB
     */
    private void updateContextCurrent(Long requestId, FlowContext ctx, int newCurrent) {
        Map<String, Object> map = new HashMap<>();
        map.put("route", ctx.route);
        map.put("current", newCurrent);
        shiftAdjustmentRequestRepository.updateApprovalContext(requestId, JsonUtils.toJSON(map));
    }

    /**
     * 將審核流程上下文推至 "COMPLETED" 狀態
     */
    private void setContextToCompleted(ShiftAdjustmentRequest req) {
        FlowContext ctx = parseFlowContext(req.getApprovalContext());

        List<String> newRoute = new ArrayList<>();
        if (ctx.route != null) {
            newRoute.addAll(ctx.route);
        }
        newRoute.add(ShiftAdjustmentRequestApprovalStage.COMPLETED.name());

        Map<String, Object> map = new HashMap<>();
        map.put("route", newRoute);
        map.put("current", newRoute.size() - 1);
        req.setApprovalContext(JsonUtils.toJSON(map));
    }


    /**
     * 依節點決定審核人列表
     */
    private List<Employee> resolveApprovers(ShiftAdjustmentRequestApprovalStage stage, Employee applicant) {
        List<Employee> approvers = new ArrayList<>();
        if (stage == null || applicant == null) return approvers;
        switch (stage) {
            case LEADER_REVIEW -> {
                Employee manager = departmentRepository.findManagerByDepartmentId(applicant.getDepartmentId());
                if (manager != null) approvers.add(manager);
            }
            case HR_REVIEW -> approvers.addAll(departmentRepository.findEmployeesByDepartmentName("人資"));
            case TECH_LEAD_REVIEW -> approvers.addAll(departmentRepository.findEmployeesByDepartmentName("技術長"));
            case GM_REVIEW -> approvers.addAll(departmentRepository.findEmployeesByDepartmentName("總經理"));
            default -> {
            }
        }
        return approvers;
    }

    /**
     * 判斷調班申請在當前節點，登入者是否有審核權限
     * 規則與請假模組一致
     */
    private boolean hasApprovalPermissionForAdjustment(Department approverDept,
                                                       Department applicantDept,
                                                       Employee approver,
                                                       ShiftAdjustmentRequestApprovalStage stage) {
        boolean sameDepartment = approverDept.getId().equals(applicantDept.getId());
        return switch (stage) {
            case LEADER_REVIEW -> sameDepartment && isDepartmentManager(approver.getId(), approverDept);
            case HR_REVIEW -> isHRDepartment(approverDept);
            case TECH_LEAD_REVIEW -> isTechnicalLeadDepartment(approverDept);
            case GM_REVIEW -> isGMDepartment(approverDept);
            default -> false;
        };
    }

}