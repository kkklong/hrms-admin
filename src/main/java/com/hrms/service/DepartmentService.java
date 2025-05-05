package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.mapstruct.DepartmentMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.DepartmentBO;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2025-04-13
 */
@Slf4j
@Service
@Transactional
public class DepartmentService extends ServiceImpl<DepartmentRepository, Department> {
    @Resource
    DepartmentRepository departmentRepository;

    @Resource
    EmployeeRepository employeeRepository;

    @Resource
    private LoginService loginService;

    public Department create(DepartmentBO departmentBO) {
        Department department = DepartmentMapper.INSTANCE.CreateDepartmentBOToDepartment(departmentBO);
        Employee employee = employeeRepository.selectById(department.getManagerId());
        UserInfo loginUser = loginService.getCurrentUserInfo();
        Set<Integer> departmentIds = list().stream()
                .map(Department::getId)
                .collect(Collectors.toSet());

        if (department.getId() != null)
            throw new ServiceException(ErrorCode.ID_AUTO_INCREMENT);

        if (department.getManagerId() != null && employee == null)
            throw new ServiceException(ErrorCode.SUPERVISOR_ID_NOT_FOUND);

        if (department.getDepartmentParent() != null && !departmentIds.contains(department.getDepartmentParent()))
            throw new ServiceException(ErrorCode.PARENT_DEPARTMENT_NOT_FOUND);

        department.setCreatedDate(LocalDateTime.now());
        department.setUpdatedDate(LocalDateTime.now());
        department.setCreatedId(loginUser.getAccount());
        department.setUpdatedId(loginUser.getAccount());
        int rows = departmentRepository.insert(department);

        if (rows != 1)
            throw new ServiceException(ErrorCode.FAILURE);

        return department;
    }

    public Department update(DepartmentBO departmentBO) {
        Department department = DepartmentMapper.INSTANCE.CreateDepartmentBOToDepartment(departmentBO);
        Employee employee = employeeRepository.selectById(department.getManagerId());
        UserInfo loginUser = loginService.getCurrentUserInfo();

        if (!isDepartmentExists(department.getId()))
            throw new ServiceException(ErrorCode.DEPARTMENT_ID_NOT_FOUND);

        if (department.getManagerId() != null && employee == null)
            throw new ServiceException(ErrorCode.SUPERVISOR_ID_NOT_FOUND);

        department.setUpdatedDate(LocalDateTime.now());
        department.setUpdatedId(loginUser.getAccount());
        int rows = departmentRepository.updateById(department);
        if (rows != 1)
            throw new ServiceException(ErrorCode.FAILURE);

        return department;
    }

    public void deleteById(Integer departmentId) {

        //取得所有員工的部門ID
        Set<Integer> checkDepartmentIds = employeeRepository.selectList(null).stream()
                .map(Employee::getDepartmentId)
                .collect(Collectors.toSet());

        if (!isDepartmentExists(departmentId)) {
            throw new ServiceException(ErrorCode.SUPERVISOR_ID_NOT_FOUND);
        }

        //需要該部門底下無員工才可以刪除
        if (checkDepartmentIds.contains(departmentId)) {
            throw new ServiceException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
        }

        int rows = departmentRepository.deleteById(departmentId);
        if (rows != 1) {
            throw new ServiceException(ErrorCode.FAILURE);
        }
    }


    private boolean isDepartmentExists(Integer departmentId) {
        return departmentRepository.selectById(departmentId) != null;
    }
}
