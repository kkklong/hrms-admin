package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hrms.entity.ShiftSchedules;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.ExcelType;
import com.hrms.exception.FileIOException;
import com.hrms.model.excel.ShiftSchedulesConfig;
import com.hrms.model.excel.ShiftSchedulesExcel;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.ShiftSchedulesRepository;
import com.hrms.util.ExcelUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelReportService {

//    @Resource
//    AttendanceRecordsService attendanceRecordsService;

    @Resource
    ShiftSchedulesRepository shiftSchedulesRepository;

    @Resource
    EmployeeRepository employeeRepository;

//    @Resource
//    AttendanceSummaryReportService attendanceSummaryReportService;

//    @Resource
//    LeaveRecordsRepository leaveRecordsRepository;
//    @Autowired
//    private LeaveRecordsService leaveRecordsService;

//    public void attendanceRecordsExcel(HttpServletResponse response, LocalDate startDate, LocalDate endDate) {
//        final String FILE_NAME = String.format("(%s)~(%s)_打卡記錄", startDate, endDate);
//
//        List<Employee> employees = employeeRepository.selectList(new QueryWrapper<Employee>().ne("role_id", "1"));
//        try (Workbook workbook = ExcelUtils.createWorkBook(ExcelType.XLSX)) {
//            for (Employee employee : employees) {
//                List<AttendanceRecordsExcel> attendanceRecordsExcels = attendanceRecordsService.queryAttendanceRecordsExcel(employee.getId(), startDate, endDate);
//                if (ObjectUtils.isEmpty(attendanceRecordsExcels)) {
//                    continue;
//                }
//                String sheetName = employee.getFullName() + "(" + employee.getAccount() + ")";
//                ExcelUtils.beanToExcelWithTitleAndAggregation(workbook, workbook.createSheet(sheetName), AttendanceRecordsExcel.class, attendanceRecordsExcels);
//            }
//
//            if (workbook.getNumberOfSheets() == 0) {
//                ExcelUtils.createTitle(workbook, workbook.createSheet("查無資料"), AttendanceRecordsExcel.class);
//            }
//
//            ExcelUtils.writeToResponse(response, FILE_NAME, workbook);
//        } catch (Exception e) {
//            log.error("打卡記錄導出異常 startDate:{} endDate:{}"
//                    , startDate, endDate, e);
//            throw new FileIOException(ErrorCode.EXPORT_FAIL);
//        }
//    }

    /**
     * 排班表Excel
     * @param shiftDate 年月yyyy-mm
     * @param departmentId 部門id
     */
    public void shiftSchedulesExcel(HttpServletResponse response, String shiftDate, Integer departmentId) {
        final String FILE_NAME = String.format("%s排班表", shiftDate);

        try (Workbook workbook = ExcelUtils.createWorkBook(ExcelType.XLSX)) {
            QueryWrapper<ShiftSchedules> queryWrapper = new QueryWrapper<>();
            queryWrapper.likeRight("a.shift_date", shiftDate);
            if (departmentId != null) {
                queryWrapper.eq("a.department_id", departmentId);
            }

            //取得員工假別資料
            List<ShiftSchedulesExcel> shiftSchedulesExcelVOs = shiftSchedulesRepository.queryShiftSchedulesExcel(queryWrapper);

            //取得當月假別名稱對應
            List<ShiftSchedulesConfig> shiftSchedulesConfigVOs = shiftSchedulesRepository.querySchedulesConfig(queryWrapper);

            //當月日期表
            QueryWrapper<ShiftSchedules> dateWrapper = new QueryWrapper<>();
            dateWrapper.likeRight("shift_date", shiftDate);
            TreeSet<LocalDate> shiftDates=  shiftSchedulesRepository.selectList(dateWrapper)
                    .stream()
                    .map(ShiftSchedules::getShiftDate)
                    .collect(Collectors.toCollection(TreeSet::new));
            //當月部門
            Set<String> departmentNames = shiftSchedulesExcelVOs
                    .stream()
                    .map(ShiftSchedulesExcel::getDepartmentName)
                    .collect(Collectors.toSet());

            departmentNames.forEach(e -> {
                Sheet sheet = workbook.createSheet(e);
                createShiftSchedulesTitle(workbook, sheet, shiftDates);
                createShiftSchedulesBody(workbook, sheet, e, shiftSchedulesExcelVOs);
                createConfigBody(workbook, sheet,  e, shiftSchedulesConfigVOs);
            });

            ExcelUtils.writeToResponse(response, FILE_NAME, workbook);
        } catch (Exception e) {
            log.error("排班表導出異常 shiftDate:{} departmentId:{}", shiftDate, departmentId, e);
            throw new FileIOException(ErrorCode.EXPORT_FAIL);
        }
    }

//    public void attendanceSummaryReportExcel(HttpServletResponse response, AttendanceSummaryReportBO attendanceSummaryReportBO) {
//        final String FILE_NAME = String.format("%s月考勤月報表", attendanceSummaryReportBO.getReportDate());
//
//        try (Workbook workbook = ExcelUtils.createWorkBook(ExcelType.XLSX)) {
//            List<AttendanceSummaryReportExcel> attendanceSummaryReportExcels = attendanceSummaryReportService.queryAttendanceSummaryReportExcel(attendanceSummaryReportBO.getReportDate(), attendanceSummaryReportBO.getEmployeeNumber());
//            ExcelUtils.beanToExcelWithTitleAndAggregation(workbook, workbook.createSheet(attendanceSummaryReportBO.getReportDate()), AttendanceSummaryReportExcel.class, attendanceSummaryReportExcels);
//
//            ExcelUtils.writeToResponse(response, FILE_NAME, workbook);
//        } catch (Exception e) {
//            log.error("考勤月報表導出異常 reportDate:{}"
//                    , attendanceSummaryReportBO.getReportDate(), e);
//            throw new FileIOException(ErrorCode.EXPORT_FAIL);
//        }
//    }

    private void createShiftSchedulesBody(Workbook workbook, Sheet sheet, String departmentName, List<ShiftSchedulesExcel> shiftSchedulesExcelVOs) {
        AtomicInteger index = new AtomicInteger();
        index.set(1);

        for (ShiftSchedulesExcel shiftSchedulesExcelVO : shiftSchedulesExcelVOs) {
            if (!departmentName.equals(shiftSchedulesExcelVO.getDepartmentName())) {
                continue;
            }

            Row row = sheet.createRow(index.get());

            AtomicInteger dataIndex = new AtomicInteger();
            dataIndex.set(0);
            //配置員工名稱
            Cell cell = row.createCell(dataIndex.get());
            cell.setCellValue(shiftSchedulesExcelVO.getNickName());

            String[] shiftColorCodes = shiftSchedulesExcelVO.getShiftColorCodes().split(",");
            //配置員工放假表
            Arrays.stream(shiftColorCodes).forEach(colorHex -> {
                CellStyle cellStyle = getShiftScheduleColorStyle(workbook, colorHex);

                dataIndex.set(dataIndex.get() + 1);
                Cell cell2 = row.createCell(dataIndex.get());
                cell2.setCellStyle(cellStyle);
            });
            index.set(index.get() + 1);
        }
    }

    private void createShiftSchedulesTitle(Workbook workbook, Sheet sheet, TreeSet<LocalDate> shiftDates) {
        final int columnWidth = 5 * 256;
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        Cell cell;

        AtomicInteger index = new AtomicInteger();
        index.set(1);
        for (LocalDate date  : shiftDates) {
            cell = row.createCell(index.get());
            cell.setCellValue(date.toString().substring(8));//只取日
            cell.setCellStyle(style);
            sheet.setColumnWidth(index.get(), columnWidth);
            index.set(index.get() + 1);
        }
    }


    private void createConfigBody(Workbook workbook, Sheet sheet, String departmentName, List<ShiftSchedulesConfig> shiftSchedulesConfigVOs) {
        AtomicInteger index = new AtomicInteger();
        index.set(sheet.getLastRowNum() + 2);

        for (ShiftSchedulesConfig shiftSchedulesConfigVO  : shiftSchedulesConfigVOs) {
            if (!departmentName.equals(shiftSchedulesConfigVO.getDepartmentName())) {
                continue;
            }
            index.set(index.get() + 1);
            Row row = sheet.createRow(index.get());

            Cell cell = row.createCell(1);

            CellStyle cellStyle = getShiftScheduleColorStyle(workbook, shiftSchedulesConfigVO.getShiftColorCode());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(shiftSchedulesConfigVO.getConfigName());

        }
    }

    private CellStyle getShiftScheduleColorStyle(Workbook workbook, String colorHex) {
        CellStyle cellStyle = workbook.createCellStyle();
        //配置員工班別色碼
        cellStyle.setFillForegroundColor(ExcelUtils.getColorFromHex(workbook, colorHex));
        //配置色碼邊框失效，解決邊框問題
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        return cellStyle;
    }

//    public void employeeCardExcel(HttpServletResponse response, EmployeeCardExcelBO employeeCardExcelBO) {
//        final String FILE_NAME = String.format("(%s)~(%s)_員工名卡", employeeCardExcelBO.getStartDate(), employeeCardExcelBO.getEndDate());
//
//        //在職員工資料
//        QueryWrapper<Employee> onWrapper = new QueryWrapper<>();
//        onWrapper.in("a.status", 1, 2, 3);
//        List<EmployeeNameCardExcel> employeeNameCardExcels = new ArrayList<>(employeeRepository.getEmployeeNameCardExcel(onWrapper));
//
//        //離職員工資料
//        QueryWrapper<Employee> offWrapper = new QueryWrapper<>();
//        offWrapper.eq("a.status", 0);
//        offWrapper.ge("a.out_date", employeeCardExcelBO.getStartDate());
//        offWrapper.le("a.out_date", employeeCardExcelBO.getEndDate());
//        employeeNameCardExcels.addAll(employeeRepository.getEmployeeNameCardExcel(offWrapper));
//
//        //取得template模板
//        File oldFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("static/sample/employeeCardExcel.xlsx")).getPath());
//
//        try (Workbook wb = WorkbookFactory.create(oldFile)) {
//            //將template寫入到新的excel
//            FileOutputStream fileOut = new FileOutputStream("/newEmployeeCardExcel.xlsx");
//            wb.write(fileOut);
//            wb.close();
//            fileOut.close();
//
//
//            try (Workbook workbook = new XSSFWorkbook(new FileInputStream("/newEmployeeCardExcel.xlsx"))) {
//
//                if (employeeNameCardExcels.isEmpty()) {
//                    Sheet sheet = workbook.getSheetAt(0);
//                    ExcelUtils.cellReplaces(sheet, new EmployeeNameCardExcel());
//                } else {
//                    for (int i = 0; i < employeeNameCardExcels.size(); i ++) {
//                        if (i != 0) {
//                            workbook.cloneSheet(0);
//                        }
//                        workbook.setSheetName(i, employeeNameCardExcels.get(i).getFullName() + "(" + i + ")");
//                    }
//
//                    for (int i = 0; i < employeeNameCardExcels.size(); i ++) {
//                        Sheet sheet = workbook.getSheetAt(i);
//                        ExcelUtils.cellReplaces(sheet, employeeNameCardExcels.get(i));
//                    }
//                }
//                ExcelUtils.writeToResponse(response, FILE_NAME, workbook);
//            } catch (Exception e) {
//                log.error("員工名卡導出異常", e);
//                throw new FileIOException(ErrorCode.EXPORT_FAIL);
//            }
//
//        } catch (Exception e) {
//            log.error("員工名卡Template模板導出異常", e);
//            throw new FileIOException(ErrorCode.EXPORT_FAIL);
//        }
//    }
//
//    public void employeeRosterExcel(HttpServletResponse response, EmployeeRosterExcelBO employeeRosterExcelBO) {
//        final String FILE_NAME = String.format("(%s)~(%s)_員工名冊", employeeRosterExcelBO.getStartDate(), employeeRosterExcelBO.getEndDate());
//
//        //在職員工資料
//        QueryWrapper<Employee> onWrapper = new QueryWrapper<>();
//        onWrapper.in("a.status", 1, 2, 3);
//        List<EmployeeRosterExcel> employeeRosterExcels = new ArrayList<>(employeeRepository.getEmployeeRosterExcel(onWrapper));
//
//        //離職員工資料
//        QueryWrapper<Employee> offWrapper = new QueryWrapper<>();
//        offWrapper.eq("a.status", 0);
//        offWrapper.ge("a.out_date", employeeRosterExcelBO.getStartDate());
//        offWrapper.le("a.out_date", employeeRosterExcelBO.getEndDate());
//        employeeRosterExcels.addAll(employeeRepository.getEmployeeRosterExcel(offWrapper));
//
//        try (Workbook workbook = ExcelUtils.createWorkBook(ExcelType.XLSX)) {
//            ExcelUtils.beanToExcelWithTitleAndAggregation(workbook, workbook.createSheet(FILE_NAME), EmployeeRosterExcel.class, employeeRosterExcels);
//            ExcelUtils.writeToResponse(response, FILE_NAME, workbook);
//        } catch (Exception e) {
//            log.error("員工名冊導出異常", e);
//            throw new FileIOException(ErrorCode.EXPORT_FAIL);
//        }
//    }
//
//    public void employeeAttendanceExcel(HttpServletResponse response, EmployeeAttendanceExcelBO employeeAttendanceExcelBO) {
//        final String FILE_NAME = String.format("(%s)~(%s)_員工出勤紀錄", employeeAttendanceExcelBO.getStartDate(), employeeAttendanceExcelBO.getEndDate());
//
//        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
//        queryWrapper.ne("role_id", "1");
//        queryWrapper.ne("role_id", "0");
//        queryWrapper.in(ObjectUtils.isNotEmpty(employeeAttendanceExcelBO.getIds()), "id", employeeAttendanceExcelBO.getIds());
//        List<Employee> employees = employeeRepository.selectList(queryWrapper);
//
//        try (Workbook workbook = ExcelUtils.createWorkBook(ExcelType.XLSX)) {
//            for (Employee employee : employees) {
//                List<AttendanceDetailsSheet> attendanceDetailsSheets = attendanceRecordsService.queryAttendanceDetailsSheet(employee.getId(), employeeAttendanceExcelBO.getStartDate(), employeeAttendanceExcelBO.getEndDate());
//                //加班
//                List<OverTimeRecordsSheet> overTimeRecordsSheets = leaveRecordsService.queryOverTimeRecordsSheet(employee.getId(), employeeAttendanceExcelBO.getStartDate(), employeeAttendanceExcelBO.getEndDate());
//                //請假
//                List<LeaveRecordsSheet> leaveRecordsSheets = leaveRecordsService.queryLeaveRecordsSheet(employee.getId(), employeeAttendanceExcelBO.getStartDate(), employeeAttendanceExcelBO.getEndDate());
//                if(ObjectUtils.isEmpty(attendanceDetailsSheets) && ObjectUtils.isEmpty(overTimeRecordsSheets) && ObjectUtils.isEmpty(leaveRecordsSheets)){
//                    continue;
//                }
//
//                String attendanceDetailsSheetName =employee.getEmployeeNumber() + employee.getFullName() + "考勤明細表";
//                Sheet sheet = workbook.createSheet(attendanceDetailsSheetName);
//
//                if (!ObjectUtils.isEmpty(attendanceDetailsSheets)) {
//                    ExcelUtils.beanToExcelWithTitleAndAggregation(workbook, sheet, AttendanceDetailsSheet.class, attendanceDetailsSheets);
//                }
//
//                if (!ObjectUtils.isEmpty(overTimeRecordsSheets)) {
//                    sheet.createRow(sheet.getLastRowNum()+2);
//                    ExcelUtils.beanToExcelWithTitleAndAggregation(workbook, sheet, OverTimeRecordsSheet.class, overTimeRecordsSheets);
//                }
//
//                if (!ObjectUtils.isEmpty(leaveRecordsSheets)) {
//                    leaveRecordsSheets.forEach(e -> {
//                        Arrays.stream(LeaveType.values()).forEach(l -> {
//                            if (l.getLeaveType().equals(e.getLeaveTypes())) {
//                                e.setLeaveTypeName(l.getChineseName());
//                            }
//                        });
//                    });
//                    sheet.createRow(sheet.getLastRowNum()+2);
//                    ExcelUtils.beanToExcelWithTitleAndAggregation(workbook, sheet, LeaveRecordsSheet.class, leaveRecordsSheets);
//                }
//            }
//
//            if (workbook.getNumberOfSheets() == 0) {
//                ExcelUtils.createTitle(workbook, workbook.createSheet("查無資料"), OverTimeRecordsSheet.class);
//            }
//
//            ExcelUtils.writeToResponse(response, FILE_NAME, workbook);
//        } catch (Exception e) {
//            log.error("打卡記錄導出異常 startDate:{} endDate:{}"
//                    , employeeAttendanceExcelBO.getStartDate(), employeeAttendanceExcelBO.getEndDate(), e);
//            throw new FileIOException(ErrorCode.EXPORT_FAIL);
//        }
//    }
}
