package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.mapstruct.EmployeeMapper;
import com.hrms.enums.EmployeeStatus;
import com.hrms.enums.ErrorCode;
import com.hrms.model.vo.QueryEmployeeVO;
import com.hrms.util.EncryptionUtils;

import com.hrms.model.UserInfo;
import com.hrms.model.bo.CreateEmployeeBO;
import com.hrms.model.bo.EmployeeBO;
import com.hrms.repository.EmployeeRepository;
import com.hrms.exception.ServiceException;
import com.hrms.model.RolePermission;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-05-22
 */
@Slf4j
@Service
@Transactional
public class EmployeeService extends ServiceImpl<EmployeeRepository, Employee> {
    @Resource
    EmployeeRepository employeeRepository;
//
//    @Resource
//    LeaveSpecialRecordsRepository leaveSpecialRecordsRepository;
//
//    @Resource
//    LeaveRecordsRepository leaveRecordsRepository;
//
//    @Resource
//    LeaveSpecialRecordsTemplateService leaveSpecialRecordsTemplateService;
//
//    @Resource
//    private ShiftSchedulesRepository shiftSchedulesRepository;
//
//    @Resource
//    private ShiftSchedulesService shiftSchedulesService;
//
//    @Resource
//    private DepartmentRepository departmentRepository;
//
    // --------Account Info--------
    public Employee getByAccount(String account) {
        return employeeRepository.getByAccount(account);
    }

    public RolePermission getRolePermission(String account) {
        return employeeRepository.getRolePermission(account);
    }

    public UserInfo getUserInfo(String account) {
        return employeeRepository.getUserInfo(account);
    }

    // ---- employee CRUD ----

    public void createEmployee(CreateEmployeeBO createEmployeeBO) {
        Employee employee = EmployeeMapper.INSTANCE.employeeCreateBOToEmployee(createEmployeeBO);
        employee.setStatus(EmployeeStatus.ACTIVE.getValue());

        try {
            employee.setPassword(EncryptionUtils.encryptWithMD5(createEmployeeBO.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            log.error("md5加密失敗", e);
            throw new ServiceException(ErrorCode.GENERAL_ERROR);
        }

//        try {
//            int count = employeeRepository.insert(employee);
////            if (count == 1) {
////                leaveSpecialRecordsTemplateService.setLeaveSpecialRecords(employee, LocalDate.now().getYear());
////                shiftSchedulesService.loadShiftSchedulesToNewEmployee(employee);
////            }
//        } catch (DataIntegrityViolationException ex) {
//            ex.printStackTrace();
//            throw new ServiceException(ErrorCode.ACCOUNT_ERROR);
//        }
    }

    public void updateEmployee(EmployeeBO employeeBO) {
        Employee oldEmployee = employeeRepository.selectById(employeeBO.getId());
        Employee employee = EmployeeMapper.INSTANCE.employeeBOToEmployee(employeeBO);
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", employee.getId());
        employeeRepository.update(employee, updateWrapper);
        //只在有更換部門時才執行
//        if (!Objects.equals(oldEmployee.getDepartmentId(), employeeBO.getDepartmentId())) {
//            Department newDepartment = departmentRepository.selectById(employeeBO.getDepartmentId());
//            if (newDepartment == null) {
//                throw new ServiceException(ErrorCode.DEPARTMENT_ID_NOT_FOUND);
//            }
//            updateShiftSchedulesForNewDepartment(employeeBO.getId(), newDepartment, employeeBO.getUpdateShiftToDefault());
//        }
    }

    @Transactional
    public void delete(Integer id) {
        //刪除employee資料
        this.removeById(id);
//        //刪除請假紀錄
//        LambdaQueryWrapper<LeaveRecords> leaveRecordsQueryWrapper = new LambdaQueryWrapper<>();
//        leaveRecordsQueryWrapper.eq(LeaveRecords::getEmployeeId, id);
//        leaveRecordsRepository.delete(leaveRecordsQueryWrapper);
//        //刪除員工假別
//        LambdaQueryWrapper<LeaveSpecialRecords> leaveSpecialRecordsQueryWrapper = new LambdaQueryWrapper<>();
//        leaveSpecialRecordsQueryWrapper.eq(LeaveSpecialRecords::getEmployeeId, id);
//        leaveSpecialRecordsRepository.delete(leaveSpecialRecordsQueryWrapper);
//        // 刪除員工班表資料
//        LambdaQueryWrapper<ShiftSchedules> shiftSchedulesQueryWrapper = new LambdaQueryWrapper<>();
//        shiftSchedulesQueryWrapper.eq(ShiftSchedules::getEmployeeId, id);
//        shiftSchedulesRepository.delete(shiftSchedulesQueryWrapper);
    }

    // --------Query--------
    public QueryEmployeeVO query(Integer employeeId) {
        Optional<Employee> employee = this.getOptById(employeeId);
        if (employee.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPLOYEE_ERROR);
        }
        QueryEmployeeVO queryEmployeeVO = EmployeeMapper.INSTANCE.employeeToQueryEmployeeVO(employee.get());
        return queryEmployeeVO;
    }

    public List<QueryEmployeeVO> queryByDepartmentId(Integer departmentId) {
        List<Employee> employees = employeeRepository.getByDepartmentId(departmentId);
        List<QueryEmployeeVO> queryEmployeeVOs = employees.stream()
                .map(EmployeeMapper.INSTANCE::employeeToQueryEmployeeVO)
                .toList();
        return queryEmployeeVOs;
    }

    public List<QueryEmployeeVO> getAllEmployee() {
        List<Employee> employees = employeeRepository.selectList(new QueryWrapper<>());
        List<QueryEmployeeVO> queryEmployeeVOs = employees.stream()
                .map(EmployeeMapper.INSTANCE::employeeToQueryEmployeeVO)
                .toList();
        return queryEmployeeVOs;
    }

//
//    private void updateShiftSchedulesForNewDepartment(Integer employeeId,
//                                                      Department newDepartment,
//                                                      Boolean updateShiftToDefault) {
//        LocalDate nextMonthFirstDay = LocalDate.now().plusMonths(1).withDayOfMonth(1);
//        List<ShiftSchedules> futureSchedules = shiftSchedulesRepository.selectList(
//                new LambdaQueryWrapper<ShiftSchedules>()
//                        .eq(ShiftSchedules::getEmployeeId, employeeId)
//                        .ge(ShiftSchedules::getShiftDate, nextMonthFirstDay)
//        );
//        if (futureSchedules == null || futureSchedules.isEmpty()) {
//            return;
//        }
//        for (ShiftSchedules schedule : futureSchedules) {
//            schedule.setDepartmentId(newDepartment.getId());
//            if (Boolean.TRUE.equals(updateShiftToDefault)) {
//                String oldShiftType = schedule.getShiftTypes();
//                //只要舊的 shiftType 含有 "SHIFT_TYPE"，就更新成新部門的預設班別
//                if (oldShiftType != null && oldShiftType.contains("SHIFT_TYPE")) {
//                    schedule.setShiftTypes(newDepartment.getWorkType());
//                }
//            }
//        }
//        shiftSchedulesService.updateBatchById(futureSchedules);
//    }




}
