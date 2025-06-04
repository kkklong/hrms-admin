package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hrms.entity.Config;
import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.ShiftSchedules;
import com.hrms.entity.mapstruct.ShiftSchedulesMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.ShiftScheduleStatus;
import com.hrms.enums.TimeSlot;
import com.hrms.exception.ServiceException;
import com.hrms.model.CsvShiftSchedules;
import com.hrms.model.ShiftScheduleCounts;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.ShiftSchedulesBO;
import com.hrms.model.bo.ShiftTypeBO;
import com.hrms.model.vo.ShiftSchedulePeriodHolidayVo;
import com.hrms.model.vo.ShiftSchedulePeriodVo;
import com.hrms.model.vo.ShiftSchedulesVO;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.ShiftSchedulesRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服務實現類
 * </p>
 *
 * @author System
 * @since 2025-04-13
 */
@Slf4j
@Service
@Transactional
public class ShiftSchedulesService extends ServiceImpl<ShiftSchedulesRepository, ShiftSchedules> {
    @Resource
    private ConfigService configService;
    @Resource
    private ShiftSchedulesRepository shiftSchedulesRepository;
    @Resource
    private EmployeeRepository employeeRepository;
    @Resource
    private DepartmentRepository departmentRepository;

    /**
     * 儲存班別配置
     */
    public void saveShiftType(ShiftTypeBO shiftTypeBO) {
        // 排班配置格式檢查
        String shiftKey = shiftTypeBO.getShiftKey();

        if (!(shiftKey.startsWith("SHIFT_TYPE_") || shiftKey.endsWith("HOLIDAY"))) {
            throw new ServiceException(ErrorCode.INVALID_SHIFT_TYPE_CONFIG_KEY);
        }
        // 驗證班別色碼格式
        String shiftColorCode = shiftTypeBO.getShiftColorCode();
        if (StringUtils.isNotBlank(shiftColorCode)) {
            try {
                Color.decode(shiftColorCode);
            } catch (NumberFormatException e) {
                throw new ServiceException(ErrorCode.INVALID_SHIFT_COLOR_CODE);
            }
        }
        // 將shiftTypeBO轉為Config
        Config config = new Config();
        config.setId(shiftTypeBO.getId());
        config.setConfigKey(shiftTypeBO.getShiftKey());
        config.setName(shiftTypeBO.getShiftName());
        config.setRemark(shiftTypeBO.getDescription());
        config.setSort(shiftTypeBO.getSort() != null ? shiftTypeBO.getSort() : 0);
        config.setConfigValue(shiftTypeBO.getFlexibleWork() != null ? shiftTypeBO.getFlexibleWork().toString() : null);
        config.setConfigValue1(shiftTypeBO.getStartTime() != null ? shiftTypeBO.getStartTime().toString() : null);
        config.setConfigValue2(shiftTypeBO.getEndTime() != null ? shiftTypeBO.getEndTime().toString() : null);
        config.setConfigValue3(shiftTypeBO.getLunchStartTime() != null ? shiftTypeBO.getLunchStartTime().toString() : null);
        config.setConfigValue4(shiftTypeBO.getLunchEndTime() != null ? shiftTypeBO.getLunchEndTime().toString() : null);
        config.setConfigValue5(shiftTypeBO.getShiftColorCode() != null ? shiftTypeBO.getShiftColorCode() : null);
        config.setConfigValue6(shiftTypeBO.getTimeSlot() != null ? shiftTypeBO.getTimeSlot() : null);
        configService.saveConfig(config);
    }

    /**
     * 依選定的區間日期給予區間範圍的資料
     */
    public java.util.List<ShiftSchedulePeriodVo> getShiftSchedulePeriods(LocalDate startDate, LocalDate endDate) {
        java.util.List<ShiftSchedulePeriodVo> periods = new ArrayList<>();

        if (startDate.isAfter(endDate)) {
            throw new ServiceException(ErrorCode.END_TIME_EARLIER_THAN_START);
        }

        // 查詢特定日期範圍的預設班表，按日期排序
        ShiftSchedules shiftSchedules = shiftSchedulesRepository.selectOne(new LambdaQueryWrapper<ShiftSchedules>()
                .eq(ShiftSchedules::getEmployeeId, 1)
                .orderByAsc(ShiftSchedules::getShiftDate)
                .last("limit 1")
        );

        if (shiftSchedules.getShiftDate().isAfter(startDate)) {
            throw new ServiceException(ErrorCode.INVALID_PERIOD_START_DATE);
        }

        LocalDate periodStartDate = startDate.with(DayOfWeek.MONDAY);
        LocalDate periodEndDate;

        LocalDate firstDate = shiftSchedules.getShiftDate().with(DayOfWeek.MONDAY);
        long days = ChronoUnit.DAYS.between(firstDate, periodStartDate);
//        log.info("periodStartDate: {}, firstDate: {}", periodStartDate, firstDate);
//        log.info("days: {}", days);
//        log.info("intValue: {}", shiftSchedules.getWeekType().intValue());
        if ((shiftSchedules.getWeekType().intValue() + days / 7) % 2 == 0) {
            periodStartDate = periodStartDate.minusWeeks(1);
        }

        do {
            periodEndDate = periodStartDate.plusWeeks(1).with(DayOfWeek.SUNDAY);
            java.util.List<ShiftSchedulePeriodHolidayVo> shiftSchedulePeriodHolidayVos = shiftSchedulesRepository.queryDefaultHolidays(periodStartDate, periodEndDate);

            ShiftSchedulePeriodVo period = new ShiftSchedulePeriodVo(periodStartDate, periodEndDate, shiftSchedulePeriodHolidayVos);
            periods.add(period);

            periodStartDate = periodEndDate.plusDays(1);
        } while (periodStartDate.isBefore(endDate) || periodStartDate.isEqual(endDate));
        return periods;
    }

    /**
     * 儲存個人排班班表
     */
    public void savePersonalShiftSchedules(java.util.List<ShiftSchedulesBO> shiftSchedulesBOList) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Integer currentEmployeeId = userInfo.getId();

        if (shiftSchedulesBOList == null || shiftSchedulesBOList.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_SHIFT_SCHEDULES);
        }
        //只能儲存自己的排班班表
        Map<Integer, java.util.List<ShiftSchedulesBO>> sortAndGroupByEmployee = sortAndGroupByEmployee(shiftSchedulesBOList);
        Set<Integer> integers = sortAndGroupByEmployee.keySet();
        if (sortAndGroupByEmployee.size() != 1 || !integers.contains(currentEmployeeId)) {
            throw new ServiceException(ErrorCode.NO_PERMISSION);
        }

        validateShiftSchedulesData(sortAndGroupByEmployee, null, true);

        validateShiftSchedules(sortAndGroupByEmployee);

        java.util.List<ShiftSchedules> shiftSchedulesList = convert(shiftSchedulesBOList);

        updateBatchById(shiftSchedulesList);
    }


    /**
     * 手動調整排班班表
     */
    public void manuallyAdjustShiftSchedules(java.util.List<ShiftSchedulesBO> shiftSchedulesBOList) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee currentEmployee = employeeRepository.selectById(userInfo.getId());
        // 組長只能編輯自己部門的排班
        Integer currentDepartmentId = 4 == currentEmployee.getRoleId()
                ? currentEmployee.getDepartmentId() : null;

        if (shiftSchedulesBOList == null || shiftSchedulesBOList.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_SHIFT_SCHEDULES);
        }

        Map<Integer, java.util.List<ShiftSchedulesBO>> sortAndGroupByEmployee = sortAndGroupByEmployee(shiftSchedulesBOList);

        validateShiftSchedulesData(sortAndGroupByEmployee, currentDepartmentId, false);

        // 進行排班檢核
        validateShiftSchedules(sortAndGroupByEmployee);

        java.util.List<ShiftSchedules> shiftSchedulesList = convert(shiftSchedulesBOList);

        // 執行批次更新
        updateBatchById(shiftSchedulesList);
    }

    /**
     * 將 ShiftSchedulesBO 列表轉換為 ShiftSchedules 列表
     */
    public java.util.List<ShiftSchedules> convert(java.util.List<ShiftSchedulesBO> shiftSchedulesBOList) {

        // 獲取所有 config配置的班別及假日類型
        java.util.List<Config> configs = new ArrayList<>();
        configs.addAll(configService.getShiftType());
        configs.addAll(configService.getHoliday());
        Set<String> configTypes = configs.stream()
                .map(Config::getConfigKey)
                .collect(Collectors.toSet());
        Map<String, String> configKeyNameMap = configs.stream()
                .collect(Collectors.toMap(Config::getConfigKey, Config::getName));

        // 轉換 BO 為 ShiftSchedules，並根據 shiftTypes 設置 status
        return shiftSchedulesBOList.stream()
                .map(bo -> {
                    if (!configTypes.contains(bo.getShiftTypes())) {
                        throw new ServiceException(ErrorCode.SHIFT_TYPE_NOT_SCHEDULED, configKeyNameMap.getOrDefault(bo.getShiftTypes(), bo.getShiftTypes()));
                    }
                    return ShiftSchedulesMapper.INSTANCE.shiftSchedulesBOToShiftSchedules(bo);
                })
                .collect(Collectors.toList());
    }


    /**
     * 排班檢核處理(連續工作天數和最低休息時間)
     */
    private void validateShiftSchedules(Map<Integer, java.util.List<ShiftSchedulesBO>> shiftSchedulesMap) {

        // 每個員工分別進行檢核
        for (Map.Entry<Integer, java.util.List<ShiftSchedulesBO>> entry : shiftSchedulesMap.entrySet()) {
            Integer employeeId = entry.getKey();
            java.util.List<ShiftSchedulesBO> employeeShifts = entry.getValue();

            // 取得班表的日期範圍
            LocalDate startDate = employeeShifts.getFirst().getShiftDate();
            LocalDate endDate = employeeShifts.getLast().getShiftDate();

            // 查詢員工在整個時間區間內的班表，包括前一週的班表(後一週班表不列入，因為可能還沒排班)
            java.util.List<ShiftSchedules> allShifts = shiftSchedulesRepository.selectList(new LambdaQueryWrapper<ShiftSchedules>()
                    .eq(ShiftSchedules::getEmployeeId, employeeId)
                    .between(ShiftSchedules::getShiftDate, startDate.minusDays(7), endDate));

            // 將資料庫中的班表轉換成 Map，key 是 shiftDate，value 是 ShiftSchedules
            Map<LocalDate, ShiftSchedules> allShiftsMap = allShifts.stream()
                    .collect(Collectors.toMap(ShiftSchedules::getShiftDate, shiftSchedules -> shiftSchedules));

            for (ShiftSchedulesBO bo : employeeShifts) {
                ShiftSchedules shiftSchedules = allShiftsMap.get(bo.getShiftDate());
                if (shiftSchedules != null) {
                    shiftSchedules.setShiftTypes(bo.getShiftTypes());
                }
            }
            // 檢查連續工作天數和最低休息時間
            validateConsecutiveWorkDaysAndRestHours(allShifts);
        }
    }

    /**
     * 載入年預設班表
     */
    public void loadShiftScheduleFromCSV(MultipartFile file) throws IOException {

        validateFileType(file);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            java.util.List<ShiftSchedules> shiftSchedulesList = new ArrayList<>();

            // 解析 CSV 標頭
            reader.readLine();
            // 解析第一筆
            line = reader.readLine();
            CsvShiftSchedules csvShiftSchedules = processCsvData(line);
            if (csvShiftSchedules == null) {
                throw new ServiceException(ErrorCode.EMPTY_CSV_DATA);
            }

            Byte weekType = shiftSchedulesRepository.getWeekType(csvShiftSchedules.getShiftDate().minusDays(1));
            if (weekType == null) {
                throw new ServiceException(ErrorCode.INVALID_SHIFT_SCHEDULE_CSV_YEAR);
            }

            // 一次性查詢所有在職員工
            java.util.List<Employee> employees = employeeRepository.findAllActiveEmployees();

            // 一次性查詢所有部門資料
            java.util.List<Department> departments = departmentRepository.selectList(null);

            // 將部門 ID 和部門對應起來，方便後續快速查找
            Map<Integer, Department> departmentMap = departments.stream()
                    .collect(Collectors.toMap(Department::getId, department -> department));

            // 解析每行 CSV 資料
            do {

                if (csvShiftSchedules.getShiftDate().getDayOfWeek() == DayOfWeek.MONDAY) {
                    weekType = weekType == 1 ? (byte) 2 : (byte) 1;
                }

                //檢查相同日期班表是否已存在於資料庫
                if (shiftSchedulesRepository.existsByShiftDate(csvShiftSchedules.getShiftDate())) {
                    throw new ServiceException(ErrorCode.DUPLICATE_SHIFT_SCHEDULE);
                }

                for (Employee employee : employees) {
                    // 獲取部門ID，如果為null則使用admin的department
                    Department department = departmentMap.getOrDefault(employee.getDepartmentId(), departmentMap.get(1));
                    String shiftType = determineShiftType(csvShiftSchedules.getShiftDate(), csvShiftSchedules.getStatus(), department);

                    ShiftSchedules shiftSchedule = new ShiftSchedules();
                    shiftSchedule.setEmployeeId(employee.getId());
                    shiftSchedule.setDepartmentId(employee.getDepartmentId());
                    shiftSchedule.setShiftDate(csvShiftSchedules.getShiftDate());
                    shiftSchedule.setWeekType(weekType);
                    shiftSchedule.setShiftTypes(shiftType);
                    shiftSchedule.setStatus(ShiftScheduleStatus.WORKING_AND_HOLIDAY.getValue());
                    shiftSchedule.setRemark(csvShiftSchedules.getRemark());
                    shiftSchedulesList.add(shiftSchedule);
                }
            } while ((csvShiftSchedules = processCsvData(reader.readLine())) != null);
            saveBatch(shiftSchedulesList);
            int targetYear = shiftSchedulesList.getLast().getShiftDate().getYear();
            //檢查明年度班表是否存在，不存在先鎖定年底班表(跨年度)
            updateOverlappingSchedules(targetYear, (byte) 1, true);
            //載入新年度班表後，將上年度年底班表解鎖
            updateOverlappingSchedules(targetYear - 1, (byte) 0, false);
        }
    }

    private CsvShiftSchedules processCsvData(String line) {
        if (line == null) {
            return null;
        }
        String[] values = line.split(",");

        // 檢查欄位數量
        if (values.length < 3) {
            throw new ServiceException(ErrorCode.INVALID_CSV_FORMAT);
        }

        LocalDate shiftDate;

        // 檢查日期格式
        try {
            shiftDate = LocalDate.parse(values[0], DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            throw new ServiceException(ErrorCode.INVALID_DATE_FORMAT);
        }

        ShiftScheduleStatus status = ShiftScheduleStatus.getByStringValue(values[2]);
        // 檢查班表狀態
        if (status == null) {
            throw new ServiceException(ErrorCode.INVALID_SHIFT_SCHEDULE_STATUS);
        }
        String remarks = values.length > 3 ? values[3] : null;

        return new CsvShiftSchedules(shiftDate, remarks, status);
    }

    /**
     * 鎖定或解鎖跨年度的班表
     *
     * @param targetYear    目標年份
     * @param actionType    要設置的 action_type (1: 鎖定, 0: 解鎖)
     * @param checkNextYear 是否需要檢查下一年度的班表 (true: 鎖定時檢查, false: 解鎖時不檢查)
     */
    private void updateOverlappingSchedules(int targetYear, byte actionType, boolean checkNextYear) {
        // 獲取最後一個星期的開始日期（星期一）
        LocalDate lastMonday = LocalDate.of(targetYear, 12, 31).with(DayOfWeek.MONDAY);

        // 獲取該週的 weekType
        Byte weekType = shiftSchedulesRepository.getWeekType(lastMonday);

        // 計算該週的結束日期（星期日）
        LocalDate weekEndDate = lastMonday.plusDays(6);

        // 檢查是否跨年度
        boolean isCrossingYear = weekEndDate.getYear() > targetYear;

        if (isCrossingYear) {
            // 如果需要檢查下一年度，並且下一年度的班表已存在，則跳過
            if (checkNextYear && shiftSchedulesRepository.existsByShiftDate(weekEndDate)) {
                return; // 下一年度的班表已經存在，不需要鎖定
            }
            // 根據 weekType 決定是否鎖定當週或兩週
            LocalDate startDate = weekType == 1 ? lastMonday : lastMonday.minusWeeks(1);
            updateSchedules(startDate, weekEndDate, actionType);
        }
    }

    /**
     * 更新指定範圍內的班表的 `action_type`
     */
    private void updateSchedules(LocalDate startDate, LocalDate endDate, byte actionType) {
        java.util.List<ShiftSchedules> overlappingSchedules = shiftSchedulesRepository.selectList(
                new QueryWrapper<ShiftSchedules>().between("shift_date", startDate, endDate)
        );
        if (!overlappingSchedules.isEmpty()) {
            overlappingSchedules.forEach(schedule -> schedule.setActionType(actionType));
            updateBatchById(overlappingSchedules);
        }
    }

    /**
     * 創建新員工時載入預設班表
     */
    public void loadShiftSchedulesToNewEmployee(Employee newEmployee) {

        LocalDate employeeEntryDate = newEmployee.getEntryDate();

        if (employeeEntryDate == null) {
            throw new ServiceException(ErrorCode.INVALID_EMPLOYEE_ENTRY_DATE);
        }

        // 以admin班表為新員工的預設班表
        java.util.List<ShiftSchedules> adminShiftSchedules = shiftSchedulesRepository.selectList(
                new QueryWrapper<ShiftSchedules>()
                        .eq("employee_id", 1)
                        .ge("shift_date", employeeEntryDate)
        );

        if (adminShiftSchedules == null || adminShiftSchedules.isEmpty()) {
            throw new ServiceException(ErrorCode.ADMIN_SHIFT_SCHEDULE_NOT_FOUND);
        }

        // 創建新員工的班表
        java.util.List<ShiftSchedules> newEmployeeShiftSchedules = new ArrayList<>();

        for (ShiftSchedules adminShiftSchedule : adminShiftSchedules) {
            ShiftSchedules newShiftSchedule = new ShiftSchedules();

            newShiftSchedule.setEmployeeId(newEmployee.getId());
            newShiftSchedule.setDepartmentId(newEmployee.getDepartmentId());
            newShiftSchedule.setShiftDate(adminShiftSchedule.getShiftDate());
            newShiftSchedule.setWeekType(adminShiftSchedule.getWeekType());
            newShiftSchedule.setShiftTypes(adminShiftSchedule.getShiftTypes());
            newShiftSchedule.setStatus(adminShiftSchedule.getStatus());
            newShiftSchedule.setRemark(adminShiftSchedule.getRemark());
            newShiftSchedule.setActionType(adminShiftSchedule.getActionType());
            newEmployeeShiftSchedules.add(newShiftSchedule);
        }
        saveBatch(newEmployeeShiftSchedules);
    }

    /**
     * 判斷班別
     */
    private String determineShiftType(LocalDate shiftDate, ShiftScheduleStatus status, Department department) {
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        if (status == ShiftScheduleStatus.DEFAULT_HOLIDAY) {
            // 假日邏輯處理，status 為 2 代表假日，且不是周六或周日則為國定假日
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                return (dayOfWeek == DayOfWeek.SATURDAY) ? "REST_HOLIDAY" : "REGULAR_HOLIDAY";
            } else {
                return "NATIONAL_HOLIDAY"; // 國定假日
            }
        } else {
            // 非假日或補班日，返回部門的預設班別
            return department.getWorkType();
        }
    }

    /**
     * 檢查檔案格式
     */
    public void validateFileType(MultipartFile file) throws IOException {

        java.util.List<String> allowedMimeTypes = Arrays.asList("text/csv", "text/plain");
        // 使用 Tika 檢測文件的 MIME 類型
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        // 檢查是否為 CSV 格式
        if (!allowedMimeTypes.contains(mimeType)) {
            throw new ServiceException(ErrorCode.INVALID_FILE_TYPE_OR_SIZE);
        }
    }

    /**
     * 根據起始和結束日期和部門ID查詢對應的排班資料
     */
    public java.util.List<ShiftSchedulesVO> queryByPeriodAndDepartment(LocalDate startDate, LocalDate endDate, Integer departmentId) {

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Integer employeeId = userInfo.getId();
        Employee currentEmployee = employeeRepository.selectById(employeeId);
        Integer roleId = userInfo.getRoleId();

        // 如果沒有指定部門，則使用當前使用者的部門
        int actualDepartmentId = (departmentId != null) ? departmentId : currentEmployee.getDepartmentId();

        // 檢查使用者權限，判斷是否可以查看所有部門
        if (!hasPermissionToViewAllDepartments(roleId) && !currentEmployee.getDepartmentId().equals(actualDepartmentId)) {
            throw new ServiceException(ErrorCode.NO_PERMISSION);
        }
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            java.util.List<ShiftSchedulePeriodVo> periods = getShiftSchedulePeriods(today, today);
            if (!periods.isEmpty()) {
                ShiftSchedulePeriodVo period = periods.getFirst();
                startDate = period.getStartDate();
                endDate = period.getEndDate();
            }
        }
        Department department = departmentRepository.selectById(actualDepartmentId);
        Map<Integer, Employee> employeeMap = employeeRepository.selectList(null).stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        // 預先查詢所有班別的色碼並存入 Map
        java.util.List<Config> configs = new ArrayList<>();
        configs.addAll(configService.getShiftType());
        configs.addAll(configService.getHoliday());
        Map<String, String> shiftColorMap = configs.stream()
                .filter(config -> config.getConfigKey() != null && config.getConfigValue5() != null)
                .collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue5));

        // 調用repository查詢符合條件的排班資料
        java.util.List<ShiftSchedules> shiftSchedulesList = shiftSchedulesRepository.queryByDepartmentAndDateRange(actualDepartmentId, startDate, endDate);

        return shiftSchedulesList.stream()
                .map(shiftSchedule -> {
                    Employee employee = employeeMap.get(shiftSchedule.getEmployeeId()); // 取得對應的員工資料
                    ShiftSchedulesVO shiftSchedulesVO = ShiftSchedulesMapper.INSTANCE.shiftSchedulesToShiftSchedulesVO(shiftSchedule, employee, department);
                    shiftSchedulesVO.setShiftColorCode(shiftColorMap.get(shiftSchedule.getShiftTypes()));
                    return shiftSchedulesVO;
                })
                .toList();
    }

    /**
     * 排班衝突檢測
     */
    public void checkShiftScheduleConflicts(int departmentId) {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee currentEmployee = employeeRepository.selectById(userInfo.getId());
        // 組長只能檢核自己部門的排班
        if (currentEmployee.getRoleId().equals(4) && !currentEmployee.getDepartmentId().equals(departmentId)) {
            throw new ServiceException(ErrorCode.DEPARTMENT_ACCESS_DENIED);
        }
        // 獲取下個月的起始和結束日期
        LocalDate firstDayOfNextMonth = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        LocalDate lastDayOfNextMonth = firstDayOfNextMonth.withDayOfMonth(firstDayOfNextMonth.lengthOfMonth());

        // 查詢部門資料
        Department department = departmentRepository.selectById(departmentId);
        if (department == null) {
            throw new ServiceException(ErrorCode.DEPARTMENT_ID_NOT_FOUND);
        }

        // 查詢月份班表
        java.util.List<ShiftSchedules> schedulesByMonth = shiftSchedulesRepository.queryByDepartmentAndDateRange(departmentId, firstDayOfNextMonth, lastDayOfNextMonth);

        //將月份班表轉成雙週制班表
        java.util.List<ShiftSchedulePeriodVo> periods = getShiftSchedulePeriods(firstDayOfNextMonth, lastDayOfNextMonth);
        LocalDate fullMonthStartDate = periods.getFirst().getStartDate(); // 第一筆雙週的開始日期
        LocalDate fullMonthEndDate = periods.getLast().getEndDate(); // 最後一筆雙週的結束日期
        java.util.List<ShiftSchedules> schedulesByTwoWeek = shiftSchedulesRepository.queryByDepartmentAndDateRange(departmentId, fullMonthStartDate, fullMonthEndDate);
        java.util.List<ShiftSchedulesBO> scheduleBOList = schedulesByTwoWeek.stream()
                .map(ShiftSchedulesMapper.INSTANCE::shiftSchedulesToShiftSchedulesBO)
                .toList();
        Map<Integer, java.util.List<ShiftSchedulesBO>> sortAndGroupByEmployee = sortAndGroupByEmployee(scheduleBOList);

        // 檢查雙週制排班
        validateShiftSchedulesData(sortAndGroupByEmployee, null, false);

        //檢查連續工作時數和最低工時
        validateShiftSchedules(sortAndGroupByEmployee);

        checkNationalHolidaySchedules(schedulesByMonth, firstDayOfNextMonth, lastDayOfNextMonth);

        checkShiftCounts(schedulesByMonth, department);
    }

    /**
     * 檢查部門員工是否在國定假日都有排班
     */
    private void checkNationalHolidaySchedules(java.util.List<ShiftSchedules> schedules, LocalDate startDate, LocalDate endDate) {

        java.util.List<ShiftSchedulePeriodHolidayVo> managerHolidays = shiftSchedulesRepository.queryDefaultHolidays(startDate, endDate);

        // 計算當月國定假日的總天數
        int nationalHolidayCount = (int) managerHolidays.stream()
                .filter(holiday -> "NATIONAL_HOLIDAY".equals(holiday.getShiftTypes()))
                .count();

        if (nationalHolidayCount == 0) {
            return;
        }

        // 取得部門內所有員工的id
        Set<Integer> employeeIds = schedules.stream()
                .map(ShiftSchedules::getEmployeeId)
                .collect(Collectors.toSet());

        // 統計每個員工安排的國定假日數量
        Map<Integer, Integer> employeeHolidayCounts = schedules.stream()
                .filter(schedule -> "NATIONAL_HOLIDAY".equals(schedule.getShiftTypes()))
                .collect(Collectors.groupingBy(
                        ShiftSchedules::getEmployeeId,
                        Collectors.summingInt(e -> 1)
                ));

        // 檢查每個員工是否安排了正確的國定假日數量
        for (Integer employeeId : employeeIds) {
            int count = employeeHolidayCounts.getOrDefault(employeeId, 0);
            if (count != nationalHolidayCount) {
                // 員工未安排足夠的國定假日班次
                String employeeName = employeeRepository.selectById(employeeId).getNickName();
                String errorMsg = "員工(" + employeeName + ")" +
                        "在日期範圍(" + startDate + "~" + endDate + ")" +
                        " 的國定假日應安排：" + nationalHolidayCount +
                        " 天，但實際安排：" + count + " 天";
                throw new ServiceException(ErrorCode.NATIONAL_HOLIDAY_SCHEDULE_MISSING, errorMsg);
            }
        }
    }

    /**
     * 檢查班次是否符合最小排班人數要求
     *
     * @param schedules  所有班表
     * @param department 部門資料
     */
    private void checkShiftCounts(java.util.List<ShiftSchedules> schedules, Department department) {
        java.util.List<Config> configs = configService.getShiftType();

        // 配置的班別時段(早、午、晚)
        Map<String, String> shiftTypeCategoryMap = configs.stream()
                .filter(config -> StringUtils.isNotBlank(config.getConfigValue6()))
                .collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue6));

        // 只處理需要排班的班別
        Map<String, String> scheduledShiftTypes = configs.stream()
                .filter(config -> config.getConfigKey().contains("SCHEDULED"))
                .collect(Collectors.toMap(Config::getConfigKey, Config::getName));

        Map<String, String> allShiftTypeNames = configs.stream()
                .collect(Collectors.toMap(Config::getConfigKey, Config::getName));

        // 構建一個存放每日班次計數的map
        Map<LocalDate, ShiftScheduleCounts> dailyShiftCounts = new HashMap<>();
        Set<LocalDate> insufficientShifts = new HashSet<>();

        //設置最少上班人數
        int every_day_morning_count = department.getEveryDayMorningCount() != null ? department.getEveryDayMorningCount() : 0;
        int every_day_afternoon_count = department.getEveryDayAfternoonCount() != null ? department.getEveryDayAfternoonCount() : 0;
        int every_day_night_count = department.getEveryDayNightCount() != null ? department.getEveryDayNightCount() : 0;

        // 統計每天各班次的排班人數
        for (ShiftSchedules schedule : schedules) {

            String shiftType = schedule.getShiftTypes();

            // 跳過為假日的shiftType以及請假的排班，不列入統計
            if (shiftType.contains("HOLIDAY") || schedule.getStatus().equals(ShiftScheduleStatus.LEAVE.getValue())) {
                continue;
            }
            if (!scheduledShiftTypes.containsKey(shiftType)) {
                StringBuilder errorMsg = new StringBuilder();
                String employeeName = employeeRepository.selectById(schedule.getEmployeeId()).getNickName();
                String shiftTypeName = allShiftTypeNames.get(shiftType);
                if (shiftTypeName == null) {
                    shiftTypeName = shiftType;
                }
                errorMsg.append("員工(").append(employeeName).append(")")
                        .append("在日期(").append(schedule.getShiftDate()).append(")")
                        .append("班別(").append(shiftTypeName).append(")");
                throw new ServiceException(ErrorCode.SHIFT_TYPE_NOT_SCHEDULED, errorMsg.toString());
            }

            LocalDate shiftDate = schedule.getShiftDate();
            ShiftScheduleCounts counts = dailyShiftCounts.computeIfAbsent(shiftDate, k -> new ShiftScheduleCounts());

            // 根據班別時段來統計排班人數
            String shiftCategory = shiftTypeCategoryMap.get(shiftType);
            if (shiftCategory == null) {
                throw new ServiceException(ErrorCode.INVALID_SHIFT_CATEGORY, "班別：" + allShiftTypeNames.get(shiftType));
            }

            // 根據 shiftCategory 和 TimeSlot 進行對應
            TimeSlot timeSlot = Arrays.stream(TimeSlot.values())
                    .filter(slot -> slot.getValue().equalsIgnoreCase(shiftCategory))
                    .findFirst()
                    .orElseThrow(() -> {
                        StringBuilder errorMsg = new StringBuilder();
                        String employeeName = employeeRepository.selectById(schedule.getEmployeeId()).getNickName();
                        errorMsg.append("員工(").append(employeeName).append(")")
                                .append("日期(").append(schedule.getShiftDate()).append(")")
                                .append(" 班別時段(").append(shiftCategory).append(")");
                        return new ServiceException(ErrorCode.SHIFT_TYPE_NOT_SCHEDULED, errorMsg.toString());
                    });

            // 根據排班類型統計人數
            switch (timeSlot) {
                case MORNING:
                    counts.setMorningCount(counts.getMorningCount() + 1);
                    break;
                case AFTERNOON:
                    counts.setAfternoonCount(counts.getAfternoonCount() + 1);
                    break;
                case NIGHT:
                    counts.setNightCount(counts.getNightCount() + 1);
                    break;
                default:
                    throw new ServiceException(ErrorCode.SHIFT_TYPE_NOT_SCHEDULED);
            }
        }

        // 檢查是否符合每日最小排班人數
        for (Map.Entry<LocalDate, ShiftScheduleCounts> entry : dailyShiftCounts.entrySet()) {
            LocalDate date = entry.getKey();
            ShiftScheduleCounts counts = entry.getValue();
            if (counts.getMorningCount() < every_day_morning_count ||
                    counts.getAfternoonCount() < every_day_afternoon_count ||
                    counts.getNightCount() < every_day_night_count) {
                insufficientShifts.add(date);
            }
        }
        // 如果有不符合的日期，拋出異常並發送郵件給組長
        if (!insufficientShifts.isEmpty()) {
            Employee teamLeader = employeeRepository.selectById(department.getManagerId());

            //組長不存在或無email
            if (teamLeader == null || StringUtils.isEmpty(teamLeader.getEmail()))
                throw new ServiceException(ErrorCode.NO_GROUP_LEADER_OR_EMAIL_NOT_SET,
                        "部門：" + department.getDepartmentName() + " - " +
                                (teamLeader == null ? "未設定組長" : "組長(" + teamLeader.getNickName() + ")未設定信箱"));
            String emailContent = buildEmailContent(insufficientShifts, department.getDepartmentName(), teamLeader.getNickName(), dailyShiftCounts, every_day_morning_count, every_day_afternoon_count, every_day_night_count);
            try {
//                emailService.sendEmail(teamLeader.getEmail(), "排班人數不足通知", emailContent);
            } catch (Exception e) {
                log.error("排班異常通知email發送失敗 account：{} email：{} content：{}"
                        , teamLeader.getAccount(), teamLeader.getEmail(), emailContent, e);
            }
            throw new ServiceException(ErrorCode.INSUFFICIENT_SHIFT_COUNTS, "請至信箱查看詳細資訊");
        }
    }

    /**
     * 排班不符最低人數郵件內容
     */
    private String buildEmailContent(Set<LocalDate> insufficientShifts, String departmentName, String managerName, Map<LocalDate, ShiftScheduleCounts> dailyShiftCounts, int requiredMorningCount, int requiredAfternoonCount, int requiredNightCount) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append(String.format("%s 您好:<br><br>", managerName));
        emailContent.append(String.format("以下日期的 %s 部門排班未滿足最低人數要求：<br><br>", departmentName));

        for (LocalDate date : insufficientShifts) {
            emailContent.append(String.format("日期：%s<br>", date.toString()));
            ShiftScheduleCounts counts = dailyShiftCounts.get(date);
            // 只通知有不足的班別
            if (counts.getMorningCount() < requiredMorningCount) {
                emailContent.append(String.format("早班需要：%d 人，實際人數：%d<br>", requiredMorningCount, counts.getMorningCount()));
            }
            if (counts.getAfternoonCount() < requiredAfternoonCount) {
                emailContent.append(String.format("午班需要：%d 人，實際人數：%d<br>", requiredAfternoonCount, counts.getAfternoonCount()));
            }
            if (counts.getNightCount() < requiredNightCount) {
                emailContent.append(String.format("晚班需要：%d 人，實際人數：%d<br>", requiredNightCount, counts.getNightCount()));
            }
            emailContent.append("<br>");
        }
        emailContent.append("請即時調整排班，避免影響運作。<br><br>");
        return emailContent.toString();
    }

    /**
     * 關閉下個月班表
     */
    public void closeNextMonthShiftSchedules(int departmentId) {

        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        Employee currentEmployee = employeeRepository.selectById(userInfo.getId());

        // 組長只能檢核自己部門的排班
        if (currentEmployee.getRoleId().equals(4) && !currentEmployee.getDepartmentId().equals(departmentId)) {
            throw new ServiceException(ErrorCode.DEPARTMENT_ACCESS_DENIED);
        }
        // 獲取下個月的起始和結束日期
        LocalDate firstDayOfNextMonth = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        LocalDate lastDayOfNextMonth = firstDayOfNextMonth.withDayOfMonth(firstDayOfNextMonth.lengthOfMonth());

        // 查詢部門資料
        Department department = departmentRepository.selectById(departmentId);
        if (department == null) {
            throw new ServiceException(ErrorCode.DEPARTMENT_ID_NOT_FOUND);
        }

        // 查詢班表
        java.util.List<ShiftSchedules> schedules = shiftSchedulesRepository.queryByDepartmentAndDateRange(departmentId, firstDayOfNextMonth, lastDayOfNextMonth);

        // 將所有班表的 actionType 設為 1
        if (!schedules.isEmpty()) {
            schedules.forEach(schedule -> schedule.setActionType((byte) 1));
            updateBatchById(schedules);
        }
    }

    /**
     * 檢查用戶是否有權限查看部門的排班資料
     */
    private boolean hasPermissionToViewAllDepartments(Integer roleId) {
        return switch (roleId) {
            case 1 ->   //ADMIN，可以查看所有部門的排班資料
                    true;
            case 2 ->   //MANAGER，可以查看所有部門的排班資料
                    true;
            case 3 ->   //HR，可以查看所有部門的排班資料
                    true;
            case 4 ->   //TEAM LEADER，僅限查看自己部門的排班資料
                    false;
            case 5 ->   //EMPLOYEE，僅限查看自己部門的排班資料
                    false;
            default -> false;
        };
    }

    private Map<Integer, java.util.List<ShiftSchedulesBO>> sortAndGroupByEmployee(java.util.List<ShiftSchedulesBO> shiftSchedulesBOList) {
        return shiftSchedulesBOList.stream()
                .sorted(Comparator.comparing(ShiftSchedulesBO::getShiftDate))
                .collect(Collectors.groupingBy(ShiftSchedulesBO::getEmployeeId));
    }

    /**
     * 檢查班表資料(一例一休、是否以雙周為單位、排班連續性)
     *
     * @param employeeShiftSchedules 使用sortAndGroupByEmployee整理過的BO資料
     * @param departmentId           限定部門，null就不檢查
     * @param checkLocked            是否檢查鎖定
     */
    private void validateShiftSchedulesData(Map<Integer, java.util.List<ShiftSchedulesBO>> employeeShiftSchedules
            , Integer departmentId, boolean checkLocked) {

        // 取得所有班別和假日
        java.util.List<Config> holidayConfigs = configService.getHoliday();

        Set<String> restDayShiftTypes = holidayConfigs.stream()
                .map(Config::getConfigKey)
                .collect(Collectors.toSet());

        for (Map.Entry<Integer, java.util.List<ShiftSchedulesBO>> entry : employeeShiftSchedules.entrySet()) {
            Integer employeeId = entry.getKey();
            java.util.List<ShiftSchedulesBO> shifts = entry.getValue();

            if (shifts.isEmpty() || shifts.size() % 14 != 0) {
                throw new ServiceException(ErrorCode.SHIFT_SCHEDULES_WRONG_SIZE);
            }

            int[] regularHolidayDaysPerWeek = new int[2]; // [0] 第1週例假日數量，[1] 第2週例假日數量
            int restHolidayDays = 0; // 休假日數量
            java.util.List<String> violationDetails = new ArrayList<>(); // 收集錯誤資訊

            for (int i = 0; i < shifts.size(); i += 14) {
                java.util.List<ShiftSchedulesBO> currentGroup = shifts.subList(i, i + 14);
                LocalDate previousDate = null;
                Integer targetMonth = getTargetMonthForCurrentGroup(currentGroup, employeeId);
                if (checkLocked && targetMonth == null) {
                    throw new ServiceException(ErrorCode.ACTION_TYPE_NOT_ALLOWED);
                }

                for (int j = 0; j < currentGroup.size(); j++) {
                    ShiftSchedulesBO shiftSchedulesBO = currentGroup.get(j);

                    ShiftSchedules shiftSchedules = shiftSchedulesRepository.selectOne(new LambdaQueryWrapper<ShiftSchedules>()
                            .eq(ShiftSchedules::getId, shiftSchedulesBO.getId())
                            .eq(ShiftSchedules::getEmployeeId, employeeId));

                    // 修改排班需一次給予兩週排班資訊
                    validateDateRange(previousDate, shiftSchedulesBO.getShiftDate(), shiftSchedules, j);

                    if (checkLocked && shiftSchedulesBO.getShiftDate().getMonthValue() == targetMonth && shiftSchedules.getActionType() == 1) {
                        throw new ServiceException(ErrorCode.ACTION_TYPE_NOT_ALLOWED);
                    }

                    if (departmentId != null && !shiftSchedules.getDepartmentId().equals(departmentId)) {
                        throw new ServiceException(ErrorCode.DEPARTMENT_ACCESS_DENIED);
                    }

                    String shiftType = shiftSchedulesBO.getShiftTypes();

                    // 是否為休息日
                    boolean isRestDay = restDayShiftTypes.contains(shiftType);
                    // 統計休息日數量
                    if (isRestDay) {
                        Byte weekType = shiftSchedules.getWeekType();
                        int weekIndex = weekType - 1; // weekType 為 1 或 2
                        if ("REGULAR_HOLIDAY".equals(shiftType)) {
                            regularHolidayDaysPerWeek[weekIndex]++;
                        } else if ("REST_HOLIDAY".equals(shiftType)) {
                            restHolidayDays++;
                        }
                    }
                    previousDate = shiftSchedulesBO.getShiftDate();
                }
                // 雙週內的例假日總數
                int totalRegularHolidayDays = Arrays.stream(regularHolidayDaysPerWeek).sum();
                // 比對每週例假日數量是否符合要求
                for (int week = 0; week < 2; week++) {
                    if (regularHolidayDaysPerWeek[week] != 1) {
                        // 取出該週開始與結束日期
                        LocalDate weekStart = currentGroup.get(week * 7).getShiftDate();
                        LocalDate weekEnd = currentGroup.get(week * 7 + 6).getShiftDate();
                        // 驗證訊息
                        String employeeName = employeeRepository.selectById(employeeId).getNickName();
                        StringBuilder errorMsg = new StringBuilder();
                        errorMsg.append("員工:").append(employeeName).append("\r");
                        errorMsg.append("日期範圍：").append(weekStart).append(" 至 ").append(weekEnd).append("\r");
                        errorMsg.append("每週應有例假日：1 天，實際設定例假日：")
                                .append(regularHolidayDaysPerWeek[week]).append(" 天");

                        throw new ServiceException(ErrorCode.REST_DAY_RULE_VIOLATION, errorMsg.toString());
                    }
                }
                // 檢查雙週是否有符合一例一休的規則
                if (totalRegularHolidayDays != 2 || restHolidayDays != 2) {
                    LocalDate groupStart = currentGroup.getFirst().getShiftDate();
                    LocalDate groupEnd = currentGroup.getLast().getShiftDate();
                    String employeeName = employeeRepository.selectById(employeeId).getNickName();
                    // 驗證訊息
                    StringBuilder errorMsg = new StringBuilder();
                    errorMsg.append("員工：").append(employeeName).append("\r");
                    errorMsg.append("雙週日期範圍：").append(groupStart).append(" 至 ").append(groupEnd).append("\r");
                    errorMsg.append("規定雙週應有例假日：2 天，休假日：2 天，實際設定例假日：")
                            .append(totalRegularHolidayDays).append(" 天，休假日：")
                            .append(restHolidayDays).append(" 天");

                    throw new ServiceException(ErrorCode.REST_DAY_RULE_VIOLATION, errorMsg.toString());
                }
                Arrays.fill(regularHolidayDaysPerWeek, 0);
                restHolidayDays = 0;
            }
        }
    }

    /**
     * 從 currentGroup 中找出第一筆未被鎖定的班表，回傳其日期月份，
     * 若全部班表皆鎖定則回傳 null。
     * 此 function 主要用於處理跨月的雙週排班情況(部分班表已鎖定)
     */
    private Integer getTargetMonthForCurrentGroup(java.util.List<ShiftSchedulesBO> currentGroup, Integer employeeId) {
        for (ShiftSchedulesBO bo : currentGroup) {
            ShiftSchedules ss = shiftSchedulesRepository.selectOne(
                    new LambdaQueryWrapper<ShiftSchedules>()
                            .eq(ShiftSchedules::getId, bo.getId())
                            .eq(ShiftSchedules::getEmployeeId, employeeId)
            );
            if (ss.getActionType() != 1) {
                return bo.getShiftDate().getMonthValue();
            }
        }
        return null;
    }

    /**
     * 計算官方雙週例假日數量
     */
    private int[] getOfficialRegularHolidayDaysPerWeek(java.util.List<ShiftSchedulePeriodVo> officialPeriods) {
        int[] officialRegularHolidayDaysPerWeek = new int[2];

        ShiftSchedulePeriodVo officialPeriod = officialPeriods.getFirst();
        java.util.List<ShiftSchedulePeriodHolidayVo> holidays = officialPeriod.getHolidays();

        if (holidays != null && !holidays.isEmpty()) {
            // 定義第一週結束日（開始日後6天）
            LocalDate firstWeekEnd = officialPeriod.getStartDate().plusDays(6);
            for (ShiftSchedulePeriodHolidayVo holiday : holidays) {
                String shiftType = holiday.getShiftTypes();
                if ("REGULAR_HOLIDAY".equals(shiftType)) {
                    // 判斷該假日屬於第幾週
                    int w = (holiday.getShiftDate().isAfter(firstWeekEnd)) ? 1 : 0;
                    officialRegularHolidayDaysPerWeek[w]++;
                }
            }
        }
        return officialRegularHolidayDaysPerWeek;
    }

    /**
     * 計算官方雙週休假日數量
     */
    private int getOfficialTotalRestHolidayDays(java.util.List<ShiftSchedulePeriodVo> officialPeriods) {
        int total = 0;
        ShiftSchedulePeriodVo period = officialPeriods.getFirst();
        java.util.List<ShiftSchedulePeriodHolidayVo> holidays = period.getHolidays();
        if (holidays != null && !holidays.isEmpty()) {
            for (ShiftSchedulePeriodHolidayVo holiday : holidays) {
                String shiftType = holiday.getShiftTypes();
                if ("REST_HOLIDAY".equals(shiftType)) {
                    total++;
                }
            }
        }
        return total;
    }


    /**
     * 檢查連續工作天數和最低休息時間
     */
    private void validateConsecutiveWorkDaysAndRestHours(java.util.List<ShiftSchedules> shifts) {

        java.util.List<Config> shiftTypeConfigs = configService.getShiftType();
        List<Config> holidayConfigs = configService.getHoliday();

        Map<String, Config> shiftTypeMap = shiftTypeConfigs.stream()
                .collect(Collectors.toMap(Config::getConfigKey, e -> e));

        Set<String> restDayShiftTypes = holidayConfigs.stream()
                .map(Config::getConfigKey)
                .collect(Collectors.toSet());

        int consecutiveWorkDays = 0; // 連續工作天數
        LocalDateTime previousShiftEndDateTime = null; // 前次下班時間

        for (ShiftSchedules shiftSchedules : shifts) {
            String shiftType = shiftSchedules.getShiftTypes();

            // 是否為休息日
            boolean isRestDay = restDayShiftTypes.contains(shiftType);

            // 檢查連續工作天數
            if (isRestDay) {
                consecutiveWorkDays = 0;
            } else {
                consecutiveWorkDays++;
                if (consecutiveWorkDays > 6) {
                    String employeeName = employeeRepository.selectById(shiftSchedules.getEmployeeId()).getNickName();
                    String errorMsg = "員工(" + employeeName + ")" +
                            "在日期(" + shiftSchedules.getShiftDate() + ")" +
                            " 連續工作天數超過 6 天";
                    throw new ServiceException(ErrorCode.EXCEEDS_SIX_DAY_WORK_RULE, errorMsg);
                }
            }
            // 檢查最低休息時間
            if (!isRestDay) {
                LocalDateTime currentShiftStartDateTime = getShiftStartDateTime(shiftSchedules, shiftTypeMap);
                if (previousShiftEndDateTime != null) {
                    long hoursBetween = ChronoUnit.HOURS.between(previousShiftEndDateTime, currentShiftStartDateTime);
                    if (hoursBetween < 11) {
                        String employeeName = employeeRepository.selectById(shiftSchedules.getEmployeeId()).getNickName();
                        String errorMsg = "員工(" + employeeName + ")" +
                                "在日期(" + previousShiftEndDateTime + "~" + currentShiftStartDateTime + ")" +
                                " 休息不足 (" + hoursBetween + " 小時)，最低需 11 小時";
                        throw new ServiceException(ErrorCode.NOT_ENOUGH_REST_HOURS, errorMsg);
                    }
                }
                previousShiftEndDateTime = getShiftEndDateTime(shiftSchedules, shiftTypeMap);
            } else {
                previousShiftEndDateTime = null;
            }
        }
    }

    /**
     * 取得班次上班時間
     */
    private LocalDateTime getShiftStartDateTime(ShiftSchedules shiftSchedules, Map<String, Config> shiftTypeMap) {
        LocalDate date = shiftSchedules.getShiftDate();
        String shiftType = shiftSchedules.getShiftTypes();

        Config config = shiftTypeMap.get(shiftType);
        if (config == null) {
            throw new ServiceException(ErrorCode.CONFIG_ID_NOT_FOUND);
        }
        // 上班時間
        String startTimeStr = config.getConfigValue1();
        LocalTime shiftStartTime = LocalTime.parse(startTimeStr);

        return date.atTime(shiftStartTime);
    }

    /**
     * 取得班次下班時間
     */
    private LocalDateTime getShiftEndDateTime(ShiftSchedules shiftSchedules, Map<String, Config> shiftTypeMap) {
        LocalDate date = shiftSchedules.getShiftDate();
        String shiftType = shiftSchedules.getShiftTypes();

        Config config = shiftTypeMap.get(shiftType);
        if (config == null) {
            throw new ServiceException(ErrorCode.CONFIG_ID_NOT_FOUND);
        }
        //下班時間
        String endTimeStr = config.getConfigValue2();
        LocalTime shiftEndTime = LocalTime.parse(endTimeStr);

        // 如果為大夜班，需要加一天
        if (shiftEndTime.isBefore(LocalTime.parse(config.getConfigValue1()))) {
            return date.plusDays(1).atTime(shiftEndTime);
        } else {
            return date.atTime(shiftEndTime);
        }
    }

    /**
     * 確認日期連續，且從週一至隔週週日，第一週weekType為1，第二週weekType為2
     */
    private void validateDateRange(LocalDate previousDate, LocalDate currentDate, ShiftSchedules shiftSchedules, int index) {
        if (previousDate != null) {
            if (!previousDate.plusDays(1).equals(currentDate)) {
                throw new ServiceException(ErrorCode.SHIFT_DATE_NOT_CONTINUOUS);
            }
        }
        if (shiftSchedules == null) {
            throw new ServiceException(ErrorCode.SHIFT_SCHEDULES_NOT_EXISTS);
        }

        if (!shiftSchedules.getShiftDate().equals(currentDate)) {
            throw new ServiceException(ErrorCode.SHIFT_SCHEDULES_WRONG_DATE);
        }

        if (index == 0) {
            if (currentDate.getDayOfWeek() != DayOfWeek.MONDAY || shiftSchedules.getWeekType() != 1) {
                throw new ServiceException(ErrorCode.SHIFT_SCHEDULES_WRONG_RANGE);
            }
        }
        if (index == 13) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SUNDAY || shiftSchedules.getWeekType() != 2) {
                throw new ServiceException(ErrorCode.SHIFT_SCHEDULES_WRONG_RANGE);
            }
        }
    }
}
