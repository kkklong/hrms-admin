package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.*;
import com.hrms.entity.mapstruct.LeaveRecordsDateTimeMapper;
import com.hrms.entity.mapstruct.LeaveRecordsMapper;
import com.hrms.enums.*;
import com.hrms.exception.ServiceException;
import com.hrms.model.HistoryReview;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.*;
import com.hrms.model.excel.LeaveRecordsSheet;
import com.hrms.model.excel.OverTimeRecordsSheet;
import com.hrms.model.vo.FileDataVO;
import com.hrms.model.vo.LeaveRecordsVO;
import com.hrms.repository.*;
import com.hrms.util.DateUtils;
import com.hrms.util.DepartmentUtils;
import com.hrms.util.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.hrms.util.DepartmentUtils.*;
import static com.hrms.util.HistoryUtils.appendHistoryReview;
import static com.hrms.util.RemarkUtils.appendReviewComment;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
@Slf4j
@Service
@Transactional
public class LeaveRecordsService extends ServiceImpl<LeaveRecordsRepository, LeaveRecords> {

    @Resource
    LeaveRecordsRepository leaveRecordsRepository;
    @Resource
    LeaveSpecialRecordsRepository leaveSpecialRecordsRepository;
    @Resource
    EmployeeRepository employeeRepository;
    @Resource
    DepartmentRepository departmentRepository;
    @Resource
    LeaveRecordsDateTimeRepository leaveRecordsDateTimeRepository;
//    @Resource
//    OvertimeRecordsRepository overtimeRecordsRepository;
    @Resource
    private ConfigService configService;
    @Resource
    private FileDataService fileDataService;
    @Resource
    EmailService emailService;
    @Resource
    ShiftSchedulesRepository shiftSchedulesRepository;
    @Resource
    ApprovalFlowService approvalFlowService;


    public void saveLeaveRecord(LeaveRecordsBO leaveRecordsBO, MultipartFile file) throws IOException {
        if (leaveRecordsBO.getId() != null)
            throw new ServiceException(ErrorCode.ID_AUTO_INCREMENT);
        createLeaveRecord(leaveRecordsBO, file);
    }

    /**
     * 取得待審核的請假記錄（以登入者ID判斷他可以取得哪些審核記錄）
     */
    public List<LeaveRecordsVO> getPendingLeaveRecords() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Integer employeeId = userInfo.getId();
        Employee employee = employeeRepository.selectById(employeeId);
        Department department = departmentRepository.selectById(employee.getDepartmentId());
        if (isHRDepartment(department) || isAdminDepartment(department)) {
            return getAllLeaveRecords();
        } else {
            List<LeaveRecords> pendingLeaveRecords = getPendingLeaveRecords(employeeId);
            return convertToLeaveRecordsVO(pendingLeaveRecords);
        }
    }

    /**
     * 根據員工id取得對應的請假紀錄
     */
    public List<LeaveRecordsVO> queryByEmployeeIds(List<Integer> employeeIds) {
        List<LeaveRecords> leaveRecords = leaveRecordsRepository.findByEmployeeIdsAndStatus(employeeIds, List.of(0, 1, 2, 3, 4));
        return convertToLeaveRecordsVO(leaveRecords);
    }


    private List<LeaveRecordsVO> getAllLeaveRecords() {

        List<LeaveRecords> leaveRecords = leaveRecordsRepository.selectList(null);
        return convertToLeaveRecordsVO(leaveRecords);
    }

    /**
     * 獲取待審核的請假記錄（以登入者ID判斷他可以取得哪些審核記錄，HR除外）
     */
    private List<LeaveRecords> getPendingLeaveRecords(Integer userId) {

        // 獲取當前用戶的部門
        Employee employee = employeeRepository.selectById(userId);
        Integer departmentId = employee.getDepartmentId();
        Department department = departmentRepository.selectById(departmentId);
        Set<LeaveRecords> pendingLeaveRecords = new HashSet<>();

        // 獲取當前用戶管理的部門
        List<Department> managedDepartments = departmentRepository.findByManagerId(userId);
        List<Integer> managedDepartmentIds = managedDepartments.stream()
                .map(Department::getId)
                .toList();

        List<Employee> employees = employeeRepository.selectList(null);
        List<Department> departments = departmentRepository.selectList(null);
        Map<Integer, Department> departmentMap = departmentRepository.selectList(null).stream()
                .collect(Collectors.toMap(Department::getId, d -> d));

        // 根據用戶部門確定審核階段
        if (isTechnicalLeadDepartment(department)) {
            // 技術長組的處理邏輯
            pendingLeaveRecords.addAll(leaveRecordsRepository.findPendingLeaveRecordsByApprovalStage(LeaveRecordApprovalStage.TECH_LEAD_REVIEW.getValue()));
            // 過濾部門為非 HR 和 GM 的員工
            List<Integer> nonHRAndGMEmployeeIds = employees.stream()
                    .filter(emp -> {
                        Department empDepartment = departmentMap.get(emp.getDepartmentId());
                        return empDepartment != null &&
                                !(DepartmentUtils.isHRDepartment(empDepartment) || DepartmentUtils.isGMDepartment(empDepartment) || DepartmentUtils.isAdminDepartment(empDepartment));
                    })
                    .map(Employee::getId)
                    .collect(Collectors.toList());
            // 根據員工 ID 查詢請假記錄，並進行去重操作
            if (!nonHRAndGMEmployeeIds.isEmpty()) {
                List<Integer> statusList = Arrays.stream(LeaveRecordStatus.values()).map(status -> status.getValue().intValue()).toList();
                pendingLeaveRecords.addAll(leaveRecordsRepository.findByEmployeeIdsAndStatus(nonHRAndGMEmployeeIds, statusList));
            }

        } else if (isGMDepartment(department)) {
            // 總經理的處理邏輯
            pendingLeaveRecords.addAll(leaveRecordsRepository.findPendingLeaveRecordsByApprovalStage(LeaveRecordApprovalStage.GM_REVIEW.getValue()));
        } else if (isDepartmentManager(userId, department)) {
            // 部門主管的處理邏輯
            pendingLeaveRecords.addAll(leaveRecordsRepository.findPendingLeaveRecordsByDepartments(LeaveRecordApprovalStage.LEADER_REVIEW.getValue(), managedDepartmentIds));
            // 過濾部門內的所有員工
            List<Integer> departmentEmployeeIds = employees.stream()
                    .filter(emp -> emp.getDepartmentId().equals(departmentId))
                    .map(Employee::getId)
                    .collect(Collectors.toList());
            // 如果部門內有員工，則查詢他們的所有請假記錄
            if (!departmentEmployeeIds.isEmpty()) {
                List<Integer> statusList = Arrays.stream(LeaveRecordStatus.values())
                        .map(status -> status.getValue().intValue())
                        .toList();
                pendingLeaveRecords.addAll(leaveRecordsRepository.findByEmployeeIdsAndStatus(departmentEmployeeIds, statusList));
            }
        }
        return new ArrayList<>(pendingLeaveRecords);
    }

    private List<LeaveRecordsVO> convertToLeaveRecordsVO(List<LeaveRecords> leaveRecordsList) {

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee currentEmployee = employeeRepository.selectById(userInfo.getId());
        Department department = departmentRepository.selectById(currentEmployee.getDepartmentId());

        // 獲取員工訊息
        List<Integer> employeeIds = leaveRecordsList.stream()
                .map(LeaveRecords::getEmployeeId)
                .distinct()
                .toList();
        if (employeeIds.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Integer, Employee> employeeMap = employeeRepository.selectBatchIds(employeeIds).stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        // 轉換LeaveRecords實體為LeaveRecordsVO對象
        // 查詢對應的 LeaveRecordsDateTime
        // 將 LeaveRecords 實體轉換為 VO 並賦值 leaveDates
        return leaveRecordsList.stream()
                .map(leaveRecord -> {
                    Employee recordEmployee = employeeMap.get(leaveRecord.getEmployeeId());
                    Department recordDepartment = departmentRepository.selectById(recordEmployee.getDepartmentId());
                    List<FileDataVO> files = fileDataService.getFilesByCaseIdAndTableName(leaveRecord.getId(), "leaveRecords");

                    // 查詢對應的 LeaveRecordsDateTime
                    List<LeaveRecordsDateTime> leaveDates = leaveRecordsDateTimeRepository.findByLeaveRecordsId(leaveRecord.getId());
                    LeaveRecordsVO leaveRecordsVO = LeaveRecordsMapper.INSTANCE.leaveRecordsToLeaveRecordsVO(leaveRecord, recordEmployee, files);
                    // 將 LeaveRecords 實體轉換為 VO 並賦值 leaveDates
                    leaveRecordsVO.setLeaveDates(leaveDates);
                    if (checkApprovalPermission(department, recordDepartment, leaveRecord, currentEmployee))
                        leaveRecordsVO.setEligibleForApproval(true);
                    return leaveRecordsVO;
                })
                .toList();
    }

    public void  createLeaveRecord(LeaveRecordsBO leaveRecordsBO, MultipartFile file) throws IOException {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee employee = employeeRepository.selectById(userInfo.getId());
        Department department = departmentRepository.selectById(employee.getDepartmentId());

        LeaveRecords leaveRecords = validateLeaveRecord(leaveRecordsBO, userInfo);

        leaveRecords.setEmployeeId(userInfo.getId());
        leaveRecords.setStatus(LeaveRecordStatus.SUBMITTED.getValue());
        initializeApprovalStage(employee, department, leaveRecords);
        appendHistoryReview(leaveRecords::getHistoryReview,
                leaveRecords::setHistoryReview, "創建", userInfo);
        leaveRecordsRepository.insert(leaveRecords);
        // 處理個別的請假紀錄
        List<LeaveRecordsDateTimeBO> leaveRecordsDateTimeBOs = leaveRecordsBO.getLeaveDates();
        List<LeaveRecordsDateTime> leaveDates = leaveRecordsDateTimeBOs.stream()
                .map(LeaveRecordsDateTimeMapper.INSTANCE::LeaveRecordsDateTimeBOToLeaveRecordsDateTime)
                .toList();
        for (LeaveRecordsDateTime leaveDateTime : leaveDates) {
            leaveDateTime.setLeaveRecordsId(leaveRecords.getId());
            leaveRecordsDateTimeRepository.insert(leaveDateTime);
        }
        float totalCountVal = leaveDates.stream()
                .map(LeaveRecordsDateTime::getCountVal)
                .reduce(0.0f, Float::sum);
        totalCountVal = Math.round(totalCountVal * 10.0f) / 10.0f;
        leaveRecords.setCountVal(totalCountVal);
        leaveRecordsRepository.updateById(leaveRecords);
        if (file != null)
            fileDataService.uploadFile(file, "leaveRecords", leaveRecords.getId());
        // 通知下階段的審核者
        notifyNextApprover(leaveRecords, employee);
    }

    private LeaveRecords validateLeaveRecord(LeaveRecordsBO leaveRecordsBO, UserInfo userInfo) throws ServiceException {
        List<LeaveSpecialRecords> leaveSpecialRecordsList = leaveSpecialRecordsRepository.getByEmployeeId(userInfo.getId());
        LeaveSpecialRecords matchedLeaveSpecialRecord = leaveSpecialRecordsList.stream()
                .filter(record -> record.getId().equals(leaveRecordsBO.getLeaveSpecialRecordsId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ErrorCode.LEAVE_RECORD_TYPE_NOT_IN_RECORD_SETTINGS));

        LocalDateTime now = LocalDateTime.now();

        // 查詢當前員工的所有LeaveRecords
        List<LeaveRecords> employeeLeaveRecords = leaveRecordsRepository.findByEmployeeIdsAndStatus(List.of(userInfo.getId()), List.of(0, 1));
        // 從所有的LeaveRecords中獲取對應的LeaveRecordsDateTime
        List<LeaveRecordsDateTime> existingLeaveRecordsDateTimes = new ArrayList<>();
        for (LeaveRecords leaveRecord : employeeLeaveRecords) {
            List<LeaveRecordsDateTime> leaveRecordsDateTimes = leaveRecordsDateTimeRepository.findByLeaveRecordsId(leaveRecord.getId());
            existingLeaveRecordsDateTimes.addAll(leaveRecordsDateTimes);
        }

        List<LeaveRecordsDateTimeBO> leaveRecordsDateTimeBOs = leaveRecordsBO.getLeaveDates();
        if (leaveRecordsDateTimeBOs == null || leaveRecordsDateTimeBOs.isEmpty()) {
            throw new ServiceException(ErrorCode.LEAVE_DATES_CANNOT_BE_EMPTY);
        }
        //搜尋所有班別的資訊
        List<Config> shiftTypes = configService.getShiftType();

        // 驗證每筆請假紀錄
        for (LeaveRecordsDateTimeBO leaveDateTimeBO : leaveRecordsDateTimeBOs) {

            // 驗證假單上是否時間有重疊
            for (LeaveRecordsDateTimeBO otherLeaveDateTimeBO : leaveRecordsDateTimeBOs) {
                if (leaveDateTimeBO != otherLeaveDateTimeBO &&
                        leaveDateTimeBO.getStartDate().isBefore(otherLeaveDateTimeBO.getEndDate()) &&
                        leaveDateTimeBO.getEndDate().isAfter(otherLeaveDateTimeBO.getStartDate())) {
                    throw new ServiceException(ErrorCode.LEAVE_RECORD_TIME_OVERLAPS);
                }
            }

            // 驗證假期時間是否在有效期內
            if (leaveDateTimeBO.getStartDate().isBefore(matchedLeaveSpecialRecord.getStartDate()) ||
                    leaveDateTimeBO.getEndDate().isAfter(matchedLeaveSpecialRecord.getEndDate())) {
                throw new ServiceException(ErrorCode.LEAVE_RECORD_TYPE_EXPIRED_OR_NOT_EFFECTIVE);
            }

            // 驗證開始時間是否晚於結束時間
            if (leaveDateTimeBO.getEndDate().isBefore(leaveDateTimeBO.getStartDate())) {
                throw new ServiceException(ErrorCode.LEAVE_RECORD_END_TIME_EARLIER_THAN_START);
            }

            validateShiftTimes(leaveDateTimeBO, shiftTypes);

            float durationInHours = Duration.between(leaveDateTimeBO.getStartDate(), leaveDateTimeBO.getEndDate()).toMinutes() / 60.0f;

            // 確保請假時數不超過最大限制
            if (durationInHours > 9) {
                throw new ServiceException(ErrorCode.LEAVE_RECORD_DURATION_EXCEEDS_MAX_LIMIT);
            }

            durationInHours = validateLunchBreak(leaveDateTimeBO, shiftTypes, durationInHours, leaveRecordsDateTimeBOs, matchedLeaveSpecialRecord.getLeaveTypes());

            // 驗證請假時長是否符合最小單位限制
            float minLeaveUnit = matchedLeaveSpecialRecord.getMinLeaveUnit();
            float ratio = durationInHours / minLeaveUnit;
            if (Math.abs(ratio - Math.round(ratio)) > 0.001) {
                throw new ServiceException(ErrorCode.LEAVE_RECORD_DURATION_BELOW_MIN_UNIT);
            }

            leaveDateTimeBO.setCountVal(durationInHours);

            // 驗證須符合提前申請的假（必須提前至少一天）
            if (matchedLeaveSpecialRecord.getAdvanceApplication()) {
                if (!now.toLocalDate().isBefore(leaveDateTimeBO.getStartDate().toLocalDate())) {
                    throw new ServiceException(ErrorCode.LEAVE_RECORD_ADVANCE_APPLICATION_REQUIRED);
                }
            }

            // 驗證是否與已有的請假記錄重疊
            for (LeaveRecordsDateTime existingLeave : existingLeaveRecordsDateTimes) {
                if (leaveDateTimeBO.getStartDate().isBefore(existingLeave.getEndDate()) &&
                        leaveDateTimeBO.getEndDate().isAfter(existingLeave.getStartDate())) {
                    throw new ServiceException(ErrorCode.LEAVE_RECORD_TIME_OVERLAPS);
                }
            }
        }
        //選休假必須要等到特休假休完才能休
        if (matchedLeaveSpecialRecord.getLeaveTypes().equals(LeaveType.OPTIONAL_LEAVE.getLeaveType())) {
            LocalDateTime optionalStart = matchedLeaveSpecialRecord.getStartDate();
            LocalDateTime optionalEnd = matchedLeaveSpecialRecord.getEndDate();
            // 根據選休假別，找出對應的特休假
            LeaveSpecialRecords correspondingAnnualLeaveRecord = leaveSpecialRecordsList.stream()
                    .filter(record -> record.getLeaveTypes().equals(LeaveType.ANNUAL_LEAVE.getLeaveType()))
                    .filter(record ->
                            !optionalStart.isBefore(record.getStartDate()) &&
                                    !optionalEnd.isAfter(record.getEndDate())
                    )
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(ErrorCode.ANNUAL_LEAVE_NOT_FOUND));
            // 取得特休假的最大時數
            double maxAnnualLeaveHours = correspondingAnnualLeaveRecord.getMaxLeaveDays() * 8.0;

            // 計算已使用的特休假時數
            Double usedAnnualLeaveHours = leaveRecordsRepository.sumLeaveHoursByTypeAndEmployeeId(
                    LeaveType.ANNUAL_LEAVE.getLeaveType(),
                    matchedLeaveSpecialRecord.getEmployeeId(),
                    correspondingAnnualLeaveRecord.getStartDate(),
                    correspondingAnnualLeaveRecord.getEndDate(),
                    correspondingAnnualLeaveRecord.getId()
            );
            usedAnnualLeaveHours = usedAnnualLeaveHours != null ? usedAnnualLeaveHours : 0.0;
            // 如果已使用的特休假時數小於最大特休假時數，則不能申請選休假
            if (usedAnnualLeaveHours < maxAnnualLeaveHours) {
                throw new ServiceException(ErrorCode.ANNUAL_LEAVE_MUST_BE_EXHAUSTED_BEFORE_OPTIONAL_LEAVE);
            }

            //選休假必須排在該週年特休最後一天之後
            Set<Integer> annualLeaveRecordIds = employeeLeaveRecords.stream()
                    .filter(lr -> lr.getLeaveSpecialRecordsId() != null &&
                            lr.getLeaveSpecialRecordsId()
                                    .equals(correspondingAnnualLeaveRecord.getId()))
                    .map(LeaveRecords::getId)
                    .collect(Collectors.toSet());

            LocalDateTime lastAnnualLeaveDate = existingLeaveRecordsDateTimes.stream()
                    .filter(dt -> annualLeaveRecordIds.contains(dt.getLeaveRecordsId()))
                    .map(LeaveRecordsDateTime::getEndDate)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            //找出這次「選休假」最早的請假日期
            LocalDateTime earliestOptionalDate = leaveRecordsDateTimeBOs.stream()
                    .map(LeaveRecordsDateTimeBO::getStartDate)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            if (lastAnnualLeaveDate != null &&
                    earliestOptionalDate != null &&
                    !earliestOptionalDate.isAfter(lastAnnualLeaveDate) && !earliestOptionalDate.equals(lastAnnualLeaveDate)) {
                throw new ServiceException(ErrorCode.OPTIONAL_LEAVE_MUST_AFTER_LAST_ANNUAL_LEAVE);
            }
        }

        // 計算新請假紀錄的總時數
        double totalLeaveVal = leaveRecordsDateTimeBOs.stream()
                .mapToDouble(LeaveRecordsDateTimeBO::getCountVal)
                .sum();

        //剩餘的假別時數
        Double availableLeaveHours = calculateAvailableLeaveHours(matchedLeaveSpecialRecord);

        if (totalLeaveVal > availableLeaveHours) {
            throw new ServiceException(ErrorCode.LEAVE_RECORD_EXCEEDS_MAX_DAYS);
        }
        //是否需要一次性請完所有天數
        if (matchedLeaveSpecialRecord.getContinuousLeave()) {
            double totalCountVal = matchedLeaveSpecialRecord.getMaxLeaveDays() * 8.0;
            if (Math.abs(totalLeaveVal - totalCountVal) > 0.001) {
                throw new ServiceException(ErrorCode.LEAVE_RECORD_MUST_BE_CONTINUOUS);
            }
        }
        LeaveRecords leaveRecords = LeaveRecordsMapper.INSTANCE.leaveRecordBOToLeaveRecords(leaveRecordsBO);
        leaveRecords.setLeaveTypes(matchedLeaveSpecialRecord.getLeaveTypes());
        return leaveRecords;
    }

    /**
     * 員工申請請假時若該班別人數 < 最低限制，就發信通知組長
     */
    private void checkAndNotifyShiftShortage(List<LeaveRecordsDateTime> leaveDates,
                                             Department department,
                                             Employee applicant) {

        // 取得日期範圍內，相同部門的排班資料
        LocalDate minDate = leaveDates.stream()
                .map(d -> d.getStartDate().toLocalDate())
                .min(LocalDate::compareTo).orElseThrow();
        LocalDate maxDate = leaveDates.stream()
                .map(d -> d.getStartDate().toLocalDate())
                .max(LocalDate::compareTo).orElseThrow();

        List<ShiftSchedules> schedules =
                shiftSchedulesRepository.queryByDepartmentAndDateRange(
                        department.getId(), minDate, maxDate);

        // 取得班別對應的時段
        Map<String, String> shiftTypeCategoryMap = configService.getShiftType().stream()
                .filter(c -> StringUtils.isNotBlank(c.getConfigValue6()))
                .collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue6));

        int minMorning = department.getEveryDayMorningCount() != null ? department.getEveryDayMorningCount() : 0;
        int minAfternoon = department.getEveryDayAfternoonCount() != null ? department.getEveryDayAfternoonCount() : 0;
        int minNight = department.getEveryDayNightCount() != null ? department.getEveryDayNightCount() : 0;

        //統計各日期每個時段的上班人數
        record DaySlot(LocalDate date, String category) {
        }
        Map<DaySlot, Long> workingCounts = schedules.stream()
                .filter(s -> !ShiftScheduleStatus.LEAVE.getValue().equals(s.getStatus())) // 排掉請假
                .filter(s -> !s.getShiftTypes().contains("HOLIDAY"))                      // 排掉假日
                .collect(Collectors.groupingBy(
                        s -> new DaySlot(s.getShiftDate(),
                                shiftTypeCategoryMap.get(s.getShiftTypes())),
                        Collectors.counting()
                ));

        //若申請者當天也上班則扣除
        for (ShiftSchedules s : schedules) {
            if (!s.getEmployeeId().equals(applicant.getId()))
                continue;
            if (ShiftScheduleStatus.LEAVE.getValue().equals(s.getStatus()))
                continue;
            if (s.getShiftTypes().contains("HOLIDAY"))
                continue;
            DaySlot ds = new DaySlot(s.getShiftDate(),
                    shiftTypeCategoryMap.get(s.getShiftTypes()));
            Long cnt = workingCounts.get(ds);
            if (cnt != null && cnt > 0) {
                workingCounts.put(ds, cnt - 1);
            }
        }
        // 檢查此次請假影響的日期和班別
        record LeaveKey(LocalDate date, String shiftType) {
        }
        leaveDates.stream()
                .map(ld -> new LeaveKey(ld.getStartDate().toLocalDate(), ld.getShiftType()))
                .distinct()
                .forEach(key -> {
                    long current = workingCounts.getOrDefault(
                            new DaySlot(key.date,
                                    shiftTypeCategoryMap.get(key.shiftType)), 0L);
                    // 比對
                    String category = shiftTypeCategoryMap.get(key.shiftType);
                    int minRequired = switch (category) {
                        case "morning" -> minMorning;
                        case "afternoon" -> minAfternoon;
                        case "night" -> minNight;
                        default -> 0;
                    };
                    if (current < minRequired) {
                        sendShortageMail(department,
                                key.date,
                                category,
                                applicant,
                                minRequired,
                                (int) current);
                    }
                });
    }

    private void sendShortageMail(Department dept,
                                  LocalDate date,
                                  String category,
                                  Employee applicant,
                                  int minRequired,
                                  int current) {
        Employee leader = employeeRepository.selectById(dept.getManagerId());
        if (leader == null || StringUtils.isEmpty(leader.getEmail()))
            throw new ServiceException(ErrorCode.NO_GROUP_LEADER_OR_EMAIL_NOT_SET,
                    "部門：" + dept.getDepartmentName() + " - " +
                            (leader == null ? "未設定組長" : "組長(" + leader.getNickName() + ")未設定信箱"));

        String zhCategory = switch (category) {
            case "morning" -> "早上時段";
            case "afternoon" -> "中午時段";
            case "night" -> "晚上時段";
            default -> category;
        };
        String subject = "排班人數不足通知";
        String content = String.format(
                "員工 %s 申請請假，<br>" +
                        "導致該時段可能低於部門最低上班人數，請留意並調度人力。<br>" +
                        "▸ 日期　　　：%s<br>" +
                        "▸ 時段　　　：%s<br>" +
                        "▸ 最低人數　：%d<br>" +
                        "▸ 目前人數　：%d<br>",
                applicant.getNickName(),
                date,
                zhCategory,
                minRequired,
                current);
        try {
            emailService.sendEmail(leader.getEmail(), subject, content);
        } catch (Exception e) {
            log.error("排班人數不足通知 email 發送失敗；leader={} email={}", leader.getAccount(), leader.getEmail(), e);
        }
    }

    /**
     * 根據班別的設定檢查並處理午休時間
     */
    private float validateLunchBreak(LeaveRecordsDateTimeBO leaveDateTimeBO, List<Config> shiftTypes, float durationInHours, List<LeaveRecordsDateTimeBO> leaveRecordsDateTimeBOs, String leaveType) throws ServiceException {
        // 查找班別的設定
        Config shiftType = shiftTypes.stream()
                .filter(st -> st.getConfigKey().equals(leaveDateTimeBO.getShiftType()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_SHIFT_TYPE));

        String lunchStartStr = shiftType.getConfigValue3();  // 午休開始時間
        String lunchEndStr = shiftType.getConfigValue4();    // 午休結束時間

        if (leaveDateTimeBO.getIncludesBreak() == null)
            throw new ServiceException(ErrorCode.INCLUDE_BREAK_ERROR);

        // 特殊時段日班邏輯：如果請假的時間是 13:00 ~ 18:00，自動扣除 1 小時午休時間
        if ((shiftType.getConfigKey().equals("SHIFT_TYPE_GENERAL_DAY") || shiftType.getConfigKey().equals("SHIFT_TYPE_SCHEDULED_DAY")) &&
                leaveDateTimeBO.getStartDate().toLocalTime().getHour() == 13 &&
                leaveDateTimeBO.getEndDate().toLocalTime().getHour() == 18 &&
                leaveDateTimeBO.getEndDate().toLocalTime().getMinute() == 0 &&
                isEligibleLeaveType(leaveType)) {
            durationInHours -= 1.0f;
            leaveDateTimeBO.setIncludesBreak(true);
        } else if (lunchStartStr != null && lunchEndStr != null) { // 有固定午休時間的處理邏輯
            LocalTime lunchStart = LocalTime.parse(lunchStartStr);
            LocalTime lunchEnd = LocalTime.parse(lunchEndStr);
            leaveDateTimeBO.setIncludesBreak(false);
            // 如果請假時間覆蓋或部分跨越午休時間，根據重疊部分扣減請假時長
            if (leaveDateTimeBO.getStartDate().toLocalTime().isBefore(lunchEnd) &&
                    leaveDateTimeBO.getEndDate().toLocalTime().isAfter(lunchStart)) {
                // 計算重疊的午休時間
                LocalTime overlapStart = leaveDateTimeBO.getStartDate().toLocalTime().isBefore(lunchStart) ? lunchStart : leaveDateTimeBO.getStartDate().toLocalTime();
                LocalTime overlapEnd = leaveDateTimeBO.getEndDate().toLocalTime().isAfter(lunchEnd) ? lunchEnd : leaveDateTimeBO.getEndDate().toLocalTime();
                // 計算重疊的小時數
                long overlapMinutes = Duration.between(overlapStart, overlapEnd).toMinutes();
                float overlapHours = overlapMinutes / 60.0f;
                // 扣除重疊的午休時間
                durationInHours -= overlapHours;
                leaveDateTimeBO.setIncludesBreak(true);
            }
        } else if (lunchStartStr == null && lunchEndStr == null && leaveDateTimeBO.getIncludesBreak() && durationInHours > 1) {// 自訂午休時間處理邏輯
            boolean hasOtherSameDayRecords = leaveRecordsDateTimeBOs.stream()
                    .anyMatch(other -> other != leaveDateTimeBO &&
                            other.getStartDate().toLocalDate().equals(leaveDateTimeBO.getStartDate().toLocalDate()) &&
                            other.getIncludesBreak());
            // 只扣除一次午休時間
            if (!hasOtherSameDayRecords) {
                durationInHours -= 1.0f;
            }
        }
        return durationInHours;
    }

    /**
     * 判斷給定的假別類型是否屬於特殊午休時段適用的假別類型。
     */
    private boolean isEligibleLeaveType(String leaveType) {
        List<String> eligibleLeaveTypes = List.of(
                LeaveType.ANNUAL_LEAVE.getLeaveType(),        // 特別休假
                LeaveType.OPTIONAL_LEAVE.getLeaveType(),      // 選休假
                LeaveType.FUNERAL_LEAVE_1.getLeaveType(),     // 喪假
                LeaveType.FUNERAL_LEAVE_2.getLeaveType(),     // 喪假
                LeaveType.FUNERAL_LEAVE_3.getLeaveType(),     // 喪假
                LeaveType.PATERNITY_LEAVE.getLeaveType(),     // 陪產假
                LeaveType.PRENATAL_CHECKUP_LEAVE.getLeaveType(), // 產檢假
                LeaveType.ANTE_NATAL_LEAVE.getLeaveType(),    // 安胎假
                LeaveType.MATERNITY_LEAVE.getLeaveType()      // 產假
        );
        return eligibleLeaveTypes.contains(leaveType);
    }

    /**
     * 計算剩餘的請假時數
     */
    public Double calculateAvailableLeaveHours(LeaveSpecialRecords leaveSpecialRecord) {

        if (leaveSpecialRecord == null)
            throw new ServiceException(ErrorCode.LEAVE_SPECIAL_RECORD_ID_NOT_FOUND);

        // 若是假別為補休則獨立處理
//        if (LeaveType.COMPENSATORY_LEAVE.getLeaveType().equals(leaveSpecialRecord.getLeaveTypes())) {
//            return calculateCompensatoryLeaveHours(leaveSpecialRecord);
//        }
        Double maxLeaveHours = (leaveSpecialRecord.getMaxLeaveDays() != null ? leaveSpecialRecord.getMaxLeaveDays() : 0) * 8.0;

        Double totalLeaveHours = leaveRecordsRepository.sumLeaveHoursByTypeAndEmployeeId(
                leaveSpecialRecord.getLeaveTypes(),
                leaveSpecialRecord.getEmployeeId(),
                leaveSpecialRecord.getStartDate(),
                leaveSpecialRecord.getEndDate(),
                leaveSpecialRecord.getId()
        );
        totalLeaveHours = totalLeaveHours != null ? totalLeaveHours : 0.0;
        return maxLeaveHours - totalLeaveHours;
    }

    /**
     * 計算「補休」(換假) 的可用時數
     */
//    private Double calculateCompensatoryLeaveHours(LeaveSpecialRecords leaveSpecialRecord) {
//        // 取得該員工已批准且換假 (conversionType = 1) 的加班總時數
//        List<OvertimeRecords> approvedOvertime = overtimeRecordsRepository
//                .findByEmployeeIdAndStatuses(leaveSpecialRecord.getEmployeeId(),
//                        List.of(LeaveRecordStatus.APPROVED.getValue().intValue()));
//        float totalOvertimeHours = approvedOvertime.stream()
//                .filter(o -> o.getConversionType().equals((byte) 1))
//                .map(OvertimeRecords::getCountVal)
//                .reduce(0f, Float::sum);
//        // 查詢員工在補休假別有效期間 (startDate ~ endDate) 內已使用的補休時數
//        Double usedCompLeaveHours = leaveRecordsRepository.sumLeaveHoursByTypeAndEmployeeId(
//                LeaveType.COMPENSATORY_LEAVE.getLeaveType(),
//                leaveSpecialRecord.getEmployeeId(),
//                leaveSpecialRecord.getStartDate(),
//                leaveSpecialRecord.getEndDate(),
//                leaveSpecialRecord.getId()
//        );
//        if (usedCompLeaveHours == null) {
//            usedCompLeaveHours = 0.0;
//        }
//        //剩餘補休時數：加班總時數 - 已使用補休時數
//        return totalOvertimeHours - usedCompLeaveHours;
//    }

    /**
     * 驗證時間段是否符合班次的要求
     */
    private void validateShiftTimes(LeaveRecordsDateTimeBO leaveDateTimeBO, List<Config> shiftTypes) throws ServiceException {
        LocalTime startTime = leaveDateTimeBO.getStartDate().toLocalTime().withNano(0);
        LocalTime endTime = leaveDateTimeBO.getEndDate().toLocalTime().withNano(0);

        // 使用 DateTimeFormatter 來轉換 config_value1 和 config_value2
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (shiftTypes == null || shiftTypes.isEmpty()) {
            throw new ServiceException(ErrorCode.INVALID_SHIFT_TYPE);
        }
        // 在 shiftTypes 中查找對應的班次配置
        Config matchedShiftConfig = shiftTypes.stream()
                .filter(config -> leaveDateTimeBO.getShiftType().equals(config.getConfigKey()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_SHIFT_TYPE));

        // 將 configValue1 和 configValue2 轉換為 LocalTime
        LocalTime shiftStartTime = LocalTime.parse(matchedShiftConfig.getConfigValue1(), formatter);
        LocalTime shiftEndTime = LocalTime.parse(matchedShiftConfig.getConfigValue2(), formatter);

        // 檢查是否允許彈性上下班
        boolean flexibleWork = Boolean.parseBoolean(matchedShiftConfig.getConfigValue());
        if (flexibleWork) {
            // 彈性上下班的情況，將結束時間延後30分鐘
            shiftEndTime = shiftEndTime.plusMinutes(30);
        }

        // 處理跨日的班次
        if (shiftStartTime.isAfter(shiftEndTime)) {
            // 跨日班次處理邏輯
            if (!(startTime.isAfter(shiftStartTime) || startTime.equals(shiftStartTime)) &&
                    !(endTime.isBefore(shiftEndTime) || endTime.equals(shiftEndTime))) {
                throw new ServiceException(ErrorCode.INVALID_SHIFT_TIME);
            }
        } else {
            // 常規班次處理邏輯
            if (startTime.isBefore(shiftStartTime) || startTime.isAfter(shiftEndTime) || endTime.isBefore(shiftStartTime) || endTime.isAfter(shiftEndTime)) {
                throw new ServiceException(ErrorCode.INVALID_SHIFT_TIME);
            }
        }
    }

    /**
     * 請假補件
     */
    public void uploadSupplementaryFile(Integer leaveRecordsId, MultipartFile file) throws IOException {
        LeaveRecords existingRecord = leaveRecordsRepository.selectById(leaveRecordsId);
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        if (existingRecord == null)
            throw new ServiceException(ErrorCode.LEAVE_RECORD_ID_NOT_FOUND);
        if (file != null) {
            fileDataService.uploadFile(file, "leaveRecords", existingRecord.getId());
            appendHistoryReview(existingRecord::getHistoryReview,
                    existingRecord::setHistoryReview, "補件", userInfo);
            leaveRecordsRepository.updateById(existingRecord);
        }
    }

    /**
     * 審核請假紀錄
     */
    public void approveLeaveRecord(LeaveApprovalBO leaveApprovalBO) {
        LeaveRecords leaveRecords = leaveRecordsRepository.selectById(leaveApprovalBO.getId());
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee approver = employeeRepository.selectById(userInfo.getId());
        Department approverDepartment = departmentRepository.selectById(approver.getDepartmentId());
        Employee applicant = employeeRepository.selectById(leaveRecords.getEmployeeId());
        Department applicantDepartment = departmentRepository.selectById(applicant.getDepartmentId());
        if (LeaveRecordApprovalStage.COMPLETED.getValue().equals(leaveRecords.getApprovalStage()))
            throw new ServiceException(ErrorCode.LEAVE_RECORD_ALREADY_PROCESSED);
        if (!checkApprovalPermission(approverDepartment, applicantDepartment, leaveRecords, approver))
            throw new ServiceException(ErrorCode.NO_APPROVAL_PERMISSION);
        if (!leaveRecords.getStatus().equals(LeaveRecordStatus.SUBMITTED.getValue()))
            throw new ServiceException(ErrorCode.NO_APPROVAL_PERMISSION);
        updateApprovalStage(leaveRecords, applicantDepartment);
        if (leaveApprovalBO.getRemark() != null && !leaveApprovalBO.getRemark().isEmpty())
            appendReviewComment(leaveRecords::getRemark,
                    leaveRecords::setRemark, leaveApprovalBO.getRemark(), userInfo);
        leaveRecords.setAttachmentRequired(leaveApprovalBO.getAttachmentRequired());
        if (leaveRecords.getApprovalStage().equals(LeaveRecordApprovalStage.COMPLETED.getValue())) {
            leaveRecords.setStatus(LeaveRecordStatus.APPROVED.getValue());
            if (applicant.getEmail() != null && !applicant.getEmail().isEmpty()) {
                try {
                    String text = generateText(leaveRecords, approver, applicant, true);
                    emailService.sendEmail(applicant.getEmail(), "請假審核通過通知", text + "請假審核通過，請知悉!");
                } catch (Exception e) {
                    appendHistoryReview(leaveRecords::getHistoryReview,
                            leaveRecords::setHistoryReview, "審核通過郵件發送失敗", userInfo);
                }
            }
        } else {
            notifyNextApprover(leaveRecords, applicant);
        }

        appendHistoryReview(leaveRecords::getHistoryReview,
                leaveRecords::setHistoryReview, "核准", userInfo);
        int count = leaveRecordsRepository.updateById(leaveRecords);

        //如果審核完成，更新班表狀態
        if (count == 1 && LeaveRecordStatus.APPROVED.getValue().equals(leaveRecords.getStatus())) {
            QueryWrapper<LeaveRecordsDateTime> leaveRecordsDateTimeQueryWrapper = new QueryWrapper<>();
            leaveRecordsDateTimeQueryWrapper.eq("leave_records_id", leaveRecords.getId());
            List<LeaveRecordsDateTime> leaveRecordsDateTimes = leaveRecordsDateTimeRepository.selectList(leaveRecordsDateTimeQueryWrapper);
            leaveRecordsDateTimes.forEach(e -> {
                //判斷是否為一整天，請假8小時
                if (e.getCountVal() == 8.0) {
                    DateTimeFormatter dateFormat = DateUtils.DatePattern.YYYY_MM_DD_DASH.getFormatter();
                    UpdateWrapper<ShiftSchedules> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("shift_date", e.getStartDate().format(dateFormat));
                    updateWrapper.eq("employee_id", leaveRecords.getEmployeeId());
                    updateWrapper.set("status", ShiftScheduleStatus.LEAVE.getValue());
                    shiftSchedulesRepository.update(updateWrapper);
                }
            });
            //若請假導致班別人數不足，發信通知組長
            checkAndNotifyShiftShortage(
                    leaveRecordsDateTimes,
                    applicantDepartment,
                    applicant);
        }
    }

    /**
     * 通知下階段審核者
     */
    private void notifyNextApprover(LeaveRecords leaveRecords, Employee applicant) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();

        LeaveRecordApprovalStage nextStage =
                LeaveRecordApprovalStage.getApprovalStage(leaveRecords.getApprovalStage());
        if (nextStage == null) return;

        List<Employee> approvers = new ArrayList<>();

        switch (nextStage) {
            case LEADER_REVIEW -> {
                // 透過 manager_id 找到部門主管
                Employee manager = departmentRepository.findManagerByDepartmentId(applicant.getDepartmentId());
                if (manager != null) {
                    approvers.add(manager);
                }
            }
            case HR_REVIEW -> {
                approvers.addAll(departmentRepository.findEmployeesByDepartmentName("人資"));
            }
            case TECH_LEAD_REVIEW -> {
                approvers.addAll(departmentRepository.findEmployeesByDepartmentName("技術長"));
            }
            case GM_REVIEW -> {
                approvers.addAll(departmentRepository.findEmployeesByDepartmentName("總經理"));
            }
            default -> {
                return;
            }
        }
        for (Employee approver : approvers) {
            if (approver.getEmail() != null && !approver.getEmail().isEmpty()) {
                try {
                    String text = generateText(leaveRecords, approver, applicant, false);
                    emailService.sendEmail(
                            approver.getEmail(),
                            "請假審核通知",
                            text + "有新的請假單需要審核!"
                    );
                } catch (Exception e) {
                    appendHistoryReview(
                            leaveRecords::getHistoryReview,
                            leaveRecords::setHistoryReview,
                            "通知下一階段審核者郵件發送失敗: " + approver.getFullName(),
                            userInfo
                    );
                }
            }
        }
    }

    private String generateText(LeaveRecords leaveRecords, Employee userinfo, Employee applicant, boolean isApproval) {
        LeaveSpecialRecords leaveSpecialRecords = leaveSpecialRecordsRepository.selectById(leaveRecords.getLeaveSpecialRecordsId());
        String leaveTypeName = LeaveType.getChineseNameByLeaveType(leaveSpecialRecords.getLeaveTypes());
        List<LeaveRecordsDateTime> leaveRecordsDateTimeList = leaveRecordsDateTimeRepository.findByLeaveRecordsId(leaveRecords.getId());
        // 找到最早的開始時間和最晚的結束時間
        LeaveRecordsDateTime earliestDateTime = leaveRecordsDateTimeList.stream()
                .min(Comparator.comparing(LeaveRecordsDateTime::getStartDate))
                .orElse(null);
        LeaveRecordsDateTime latestDateTime = leaveRecordsDateTimeList.stream()
                .max(Comparator.comparing(LeaveRecordsDateTime::getEndDate))
                .orElse(null);
        String leaveDate = "";
        if (earliestDateTime != null) {
            leaveDate = String.format("%s - %s",
                    earliestDateTime.getStartDate(),
                    latestDateTime.getEndDate());
        }
        // 總時數
        float totalHours = leaveRecords.getCountVal();
        // 生成通知內容
        return String.format("%s 您好:<br><br>" +
                        "申請人：%s<br>" +
                        "假別：%s<br>" +
                        "申請時間：%s<br>" +
                        "請假日期：%s<br>" +
                        "總計時數：%.1f 小時<br>" +
                        "審核狀態：%s<br>",
                isApproval ? applicant.getNickName() : userinfo.getNickName(),
                applicant.getNickName(),
                leaveTypeName,
                leaveRecords.getCreatedDate().toString(),
                leaveDate,
                totalHours,
                LeaveRecordStatus.values()[leaveRecords.getStatus()].getName());
    }

    /**
     * 駁回請假紀錄
     */
    public void rejectLeaveRecord(LeaveRejectionBO leaveRejectionBO) {
        LeaveRecords leaveRecords = leaveRecordsRepository.selectById(leaveRejectionBO.getId());
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee approver = employeeRepository.selectById(userInfo.getId());
        Employee applicant = employeeRepository.selectById(leaveRecords.getEmployeeId());
        Department approverDepartment = departmentRepository.selectById(approver.getDepartmentId());
        Department applicantDepartment = departmentRepository.selectById(applicant.getDepartmentId());
        if (!checkApprovalPermission(approverDepartment, applicantDepartment, leaveRecords, approver))
            throw new ServiceException(ErrorCode.NO_APPROVAL_PERMISSION);
        if (!leaveRecords.getStatus().equals(LeaveRecordStatus.SUBMITTED.getValue()))
            throw new ServiceException(ErrorCode.LEAVE_RECORD_REJECT_FAIL);
        leaveRecords.setStatus(LeaveRecordStatus.REJECTED.getValue());
        leaveRecords.setApprovalStage(LeaveRecordApprovalStage.COMPLETED.getValue());
        if (leaveRejectionBO.getRemark() != null && !leaveRejectionBO.getRemark().isEmpty())
            appendReviewComment(leaveRecords::getRemark,
                    leaveRecords::setRemark, leaveRejectionBO.getRemark(), userInfo);
        appendHistoryReview(leaveRecords::getHistoryReview,
                leaveRecords::setHistoryReview, "駁回", userInfo);
        //寄信通知給前面的審核及申請人
        List<HistoryReview> reviewList = JsonUtils.toList(leaveRecords.getHistoryReview(), HistoryReview.class);
        Set<Integer> emailRecipients = reviewList.stream()
                .filter(r -> "核准".equals(r.getAction()) || "創建".equals(r.getAction()))
                .map(HistoryReview::getId)
                .collect(Collectors.toSet());
        String subject = "請假申請駁回通知";
        for (Integer recipientId : emailRecipients) {
            Employee recipient = employeeRepository.selectById(recipientId);
            if (recipient != null && recipient.getEmail() != null && !recipient.getEmail().isEmpty()) {
                try {
                    String text = generateText(leaveRecords, recipient, applicant, false);
                    emailService.sendEmail(recipient.getEmail(), subject, text + "請假申請駁回! 請知悉");
                } catch (Exception e) {
                    appendHistoryReview(leaveRecords::getHistoryReview,
                            leaveRecords::setHistoryReview, "請假駁回郵件發送失敗", userInfo);
                    break;
                }
            }
        }
        leaveRecordsRepository.updateById(leaveRecords);
    }

    /**
     * 銷假請假紀錄
     */
    public void cancelLeaveRecord(LeaveCancellationBO leaveCancellationBO) {
        LeaveRecords leaveRecords = leaveRecordsRepository.selectById(leaveCancellationBO.getId());
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee canceller = employeeRepository.selectById(userInfo.getId());
        Employee applicant = employeeRepository.selectById(leaveRecords.getEmployeeId());
        Department cancellerDepartment = departmentRepository.selectById(canceller.getDepartmentId());

        //  "送審" 或 "批准" 狀態的紀錄才可以銷假
        if (!(leaveRecords.getStatus().equals(LeaveRecordStatus.SUBMITTED.getValue())
                || leaveRecords.getStatus().equals(LeaveRecordStatus.APPROVED.getValue()))) {
            throw new ServiceException(ErrorCode.LEAVE_RECORD_CANCEL_FAIL);
        }
        List<LeaveRecordsDateTime> leaveDateTimeList = leaveRecordsDateTimeRepository.findByLeaveRecordsId(leaveRecords.getId());

        // 取最早的開始時間和最晚的結束時間
        LocalDateTime startTime = leaveDateTimeList.stream()
                .map(LeaveRecordsDateTime::getStartDate)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new ServiceException(ErrorCode.LEAVE_DATES_CANNOT_BE_EMPTY));

        LocalDateTime endTime = leaveDateTimeList.stream()
                .map(LeaveRecordsDateTime::getEndDate)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new ServiceException(ErrorCode.LEAVE_DATES_CANNOT_BE_EMPTY));

        LocalDateTime currentTime = LocalDateTime.now();

        // 如果銷假時間早於最早的請假開始時間
        if (currentTime.isBefore(startTime)) {
            // 檢查申請人是不是執行銷假的人
            if (!canceller.getId().equals(applicant.getId())) {
                throw new ServiceException(ErrorCode.NO_CANCEL_PERMISSION);
            }
        }
        // 如果執行銷假時間已經超過最早的請假開始時間且為已批准的假單
        else if (currentTime.isAfter(startTime) &&
                leaveRecords.getStatus().equals(LeaveRecordStatus.APPROVED.getValue())) {
            // 檢查是否為HR
            if (!isHRDepartment(cancellerDepartment)) {
                throw new ServiceException(ErrorCode.NO_CANCEL_PERMISSION);
            }
            // 檢查是否已經超出最晚的請假結束時間
            if (currentTime.isAfter(endTime)) {
                throw new ServiceException(ErrorCode.LEAVE_RECORD_CANCEL_FAIL);
            }
        }

        //特休假銷假驗證
        if (LeaveType.ANNUAL_LEAVE.getLeaveType().equals(leaveRecords.getLeaveTypes())) {
            ensureNoOptionalLeaveInSameAnniversary(leaveRecords, applicant.getId());
        }

        leaveRecords.setStatus(LeaveRecordStatus.CANCELLED.getValue());
        leaveRecords.setApprovalStage(LeaveRecordApprovalStage.COMPLETED.getValue());
        if (leaveCancellationBO.getRemark() != null && !leaveCancellationBO.getRemark().isEmpty()) {
            appendReviewComment(
                    leaveRecords::getRemark,
                    leaveRecords::setRemark,
                    leaveCancellationBO.getRemark(),
                    userInfo
            );
        }
        appendHistoryReview(leaveRecords::getHistoryReview,
                leaveRecords::setHistoryReview, "銷假", userInfo);
        //寄信通知給前面的審核及申請人
        List<HistoryReview> reviewList = JsonUtils.toList(leaveRecords.getHistoryReview(), HistoryReview.class);
        Set<Integer> emailRecipients = reviewList.stream()
                .filter(r -> "核准".equals(r.getAction()) || "創建".equals(r.getAction()))
                .map(HistoryReview::getId)
                .collect(Collectors.toSet());
        String subject = "申請銷假通知";
        for (Integer recipientId : emailRecipients) {
            Employee recipient = employeeRepository.selectById(recipientId);
            if (recipient != null && recipient.getEmail() != null && !recipient.getEmail().isEmpty()) {
                try {
                    String text = generateText(leaveRecords, recipient, applicant, false);
                    emailService.sendEmail(recipient.getEmail(), subject, text + "申請已銷假! 請知悉");
                } catch (Exception e) {
                    appendHistoryReview(leaveRecords::getHistoryReview,
                            leaveRecords::setHistoryReview, "銷假郵件發送失敗", userInfo);
                    break;
                }
            }
        }

        int count = leaveRecordsRepository.updateById(leaveRecords);

        //如果審核完成，更新班表狀態
        if (count == 1 && LeaveRecordStatus.CANCELLED.getValue().equals(leaveRecords.getStatus())) {
            QueryWrapper<LeaveRecordsDateTime> leaveRecordsDateTimeQueryWrapper = new QueryWrapper<>();
            leaveRecordsDateTimeQueryWrapper.eq("leave_records_id", leaveRecords.getId());
            List<LeaveRecordsDateTime> leaveRecordsDateTimes = leaveRecordsDateTimeRepository.selectList(leaveRecordsDateTimeQueryWrapper);
            leaveRecordsDateTimes.forEach(e -> {
                //判斷是否為一整天，請假8小時
                if (e.getCountVal() == 8.0) {
                    DateTimeFormatter dateFormat = DateUtils.DatePattern.YYYY_MM_DD_DASH.getFormatter();
                    UpdateWrapper<ShiftSchedules> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("shift_date", e.getStartDate().format(dateFormat));
                    updateWrapper.eq("employee_id", leaveRecords.getEmployeeId());
                    updateWrapper.set("status", ShiftScheduleStatus.WORKING_AND_HOLIDAY.getValue());
                    shiftSchedulesRepository.update(updateWrapper);
                }
            });
        }
    }

    /**
     * 撤銷特休前，確認同一週年範圍內沒有「送審／批准」中的選休假。
     */
    private void ensureNoOptionalLeaveInSameAnniversary(LeaveRecords annualLeaveRecord,
                                                        Integer employeeId) {

        LeaveSpecialRecords spec =
                leaveSpecialRecordsRepository.selectById(annualLeaveRecord.getLeaveSpecialRecordsId());
        if (spec == null) return;

        LocalDateTime yearStart = spec.getStartDate();
        LocalDateTime yearEnd = spec.getEndDate();

        // 是否已存在選休假
        boolean optionalLeaveExists = leaveRecordsRepository
                .findByEmployeeIdsAndStatus(
                        List.of(employeeId),
                        List.of((int) LeaveRecordStatus.SUBMITTED.getValue(),
                                (int) LeaveRecordStatus.APPROVED.getValue()))
                .stream()
                .filter(lr -> LeaveType.OPTIONAL_LEAVE.getLeaveType().equals(lr.getLeaveTypes()))
                .anyMatch(lr -> leaveRecordsDateTimeRepository.findByLeaveRecordsId(lr.getId())
                        .stream()
                        .anyMatch(dt ->
                                !dt.getStartDate().isBefore(yearStart) &&
                                        !dt.getEndDate().isAfter(yearEnd)));
        if (optionalLeaveExists) {
            throw new ServiceException(
                    ErrorCode.OPTIONAL_LEAVE_ALREADY_APPLIED);
        }
    }

    /**
     * 初始化審核階段
     */
    public void initializeApprovalStage(Employee employee, Department department, LeaveRecords leaveRecords) {
//        boolean isTechLeadDept = isTechnicalLeadDepartment(department);
//        boolean isHRDept = isHRDepartment(department);
//        boolean isDeptManager = isDepartmentManager(employee.getId(), department);
//        if (isTechLeadDept || (isDeptManager && isHRDept)) {
//            leaveRecords.setApprovalStage(LeaveRecordApprovalStage.GM_REVIEW.getValue());
//        } else if (isDeptManager) {
//            leaveRecords.setApprovalStage(LeaveRecordApprovalStage.TECH_LEAD_REVIEW.getValue());
//        } else {
//            leaveRecords.setApprovalStage(LeaveRecordApprovalStage.LEADER_REVIEW.getValue());
//        }
        boolean ge24 = leaveRecords.getCountVal() != null && leaveRecords.getCountVal() >= 24;
        List<LeaveRecordApprovalStage> route = approvalFlowService.resolve(employee, department, ge24);

        if (route != null && !route.isEmpty()) {
            leaveRecords.setApprovalStage(route.get(0).getValue());
            // 建議存起來，updateApprovalStage 可直接使用
            leaveRecords.setCustomRouteJson(JsonUtils.toJSON(route));
            return;
        }
    }

    public List<LeaveRecordsSheet> queryLeaveRecordsSheet(Integer id, LocalDate startDate, LocalDate endDate) {
        return leaveRecordsRepository.queryLeaveRecordsSheet(id, startDate, endDate);
    }

    public List<OverTimeRecordsSheet> queryOverTimeRecordsSheet(Integer id, LocalDate startDate, LocalDate endDate) {
        return leaveRecordsRepository.queryOverTimeRecordsSheet(id, startDate, endDate);
    }

    /**
     * 更新審核階段
     */
    private void updateApprovalStage(LeaveRecords leaveRecords, Department department) {
        /* === ① 新增：依 custom_route_json 推進 === */
        if (StringUtils.isNotBlank(leaveRecords.getCustomRouteJson())) {
            List<LeaveRecordApprovalStage> route =
                    JsonUtils.toList(leaveRecords.getCustomRouteJson(), LeaveRecordApprovalStage.class);

            LeaveRecordApprovalStage current =
                    LeaveRecordApprovalStage.getApprovalStage(leaveRecords.getApprovalStage());
            int idx = current != null ? route.indexOf(current) : -1;

            if (idx >= 0 && idx < route.size() - 1) {
                // 還有下一關
                leaveRecords.setApprovalStage(route.get(idx + 1).getValue());
            } else {
                // 已到最後關
                leaveRecords.setApprovalStage(LeaveRecordApprovalStage.COMPLETED.getValue());
            }
            return;                // ← 完全走客製流程，後面的舊 switch 不再執行
        }

        /* === ② 原本小時數 / 部門邏輯保留 === */
        boolean isHRDepartment = isHRDepartment(department);
        //請假流程狀態（0:審核完成 1: 組長審核中、2:人資審核中、3:技術長審核中、4:總經理審核中）
        LeaveRecordApprovalStage initialStage = LeaveRecordApprovalStage.getApprovalStage(leaveRecords.getApprovalStage());
        if (initialStage == null) {
            throw new ServiceException(ErrorCode.INVALID_LEAVE_RECORD_APPROVAL_STAGE);
        }
        Float hoursRequested = leaveRecords.getCountVal();
        if (hoursRequested >= 24) {
            switch (initialStage) {
                case LEADER_REVIEW:
                    leaveRecords.setApprovalStage(isHRDepartment ?
                            LeaveRecordApprovalStage.GM_REVIEW.getValue() : LeaveRecordApprovalStage.TECH_LEAD_REVIEW.getValue());
                    break;
                case TECH_LEAD_REVIEW:
                    leaveRecords.setApprovalStage(LeaveRecordApprovalStage.GM_REVIEW.getValue());
                    break;
                case GM_REVIEW:
                    leaveRecords.setApprovalStage(isHRDepartment ?
                            LeaveRecordApprovalStage.COMPLETED.getValue() : LeaveRecordApprovalStage.HR_REVIEW.getValue());
                    break;
                case HR_REVIEW:
                    leaveRecords.setApprovalStage(LeaveRecordApprovalStage.COMPLETED.getValue());
                    break;
            }
        } else if (hoursRequested > 0) {
            switch (initialStage) {
                case LEADER_REVIEW:
                    leaveRecords.setApprovalStage(isHRDepartment ?
                            LeaveRecordApprovalStage.COMPLETED.getValue() : LeaveRecordApprovalStage.TECH_LEAD_REVIEW.getValue());
                    break;
                case TECH_LEAD_REVIEW:
                    leaveRecords.setApprovalStage(LeaveRecordApprovalStage.HR_REVIEW.getValue());
                    break;
                case HR_REVIEW:
                    leaveRecords.setApprovalStage(LeaveRecordApprovalStage.COMPLETED.getValue());
                    break;
                case GM_REVIEW:
                    leaveRecords.setApprovalStage(isHRDepartment(department) ?
                            LeaveRecordApprovalStage.COMPLETED.getValue() : LeaveRecordApprovalStage.HR_REVIEW.getValue());
                    break;
            }
        } else
            throw new ServiceException(ErrorCode.LEAVE_RECORD_COUNT_FAIL);
    }

    /**
     * 檢查是否有審核紀錄的權限
     */
    private boolean checkApprovalPermission(Department approverDepartment, Department
            applicantDepartment, LeaveRecords leaveRecords, Employee approver) {

        boolean sameDepartment = approverDepartment.getId().equals(applicantDepartment.getId());
        LeaveRecordApprovalStage approvalStage = LeaveRecordApprovalStage.getApprovalStage(leaveRecords.getApprovalStage());
        if (approvalStage == null) {
            throw new ServiceException(ErrorCode.INVALID_LEAVE_RECORD_APPROVAL_STAGE);
        }
        return switch (approvalStage) {
            case LEADER_REVIEW -> // 組長審核
                    sameDepartment && isDepartmentManager(approver.getId(), approverDepartment);
            case HR_REVIEW -> // 人資審核
                    isHRDepartment(approverDepartment);
            case TECH_LEAD_REVIEW -> // 技術長審核
                    isTechnicalLeadDepartment(approverDepartment);
            case GM_REVIEW -> // 總經理審核
                    isGMDepartment(approverDepartment);
            default -> false;
        };
    }
}
