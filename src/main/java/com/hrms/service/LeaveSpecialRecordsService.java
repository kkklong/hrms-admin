package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Department;
import com.hrms.entity.Employee;
import com.hrms.entity.LeaveSpecialRecords;
import com.hrms.entity.mapstruct.LeaveSpecialRecordsMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.LeaveType;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.LeaveSpecialRecordsBO;
import com.hrms.model.vo.LeaveSpecialRecordsVO;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveSpecialRecordsRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LeaveSpecialRecordsService extends ServiceImpl<LeaveSpecialRecordsRepository, LeaveSpecialRecords> {

    @Resource
    LeaveSpecialRecordsRepository leaveSpecialRecordsRepository;
    @Resource
    EmployeeRepository employeeRepository;
    @Resource
    DepartmentRepository departmentRepository;
    @Resource
    LeaveRecordsService leaveRecordsService;

    public void saveLeaveSpecialRecords(LeaveSpecialRecordsBO leaveSpecialRecordsBO) {
        if (leaveSpecialRecordsBO.getId() != null)
            update(leaveSpecialRecordsBO);
        else
            create(leaveSpecialRecordsBO);
    }

    private void create(LeaveSpecialRecordsBO leaveSpecialRecordsBO) {
        LeaveSpecialRecords leaveSpecialRecords = LeaveSpecialRecordsMapper.INSTANCE.leaveSpecialRecordsBOToLeaveSpecialRecords(leaveSpecialRecordsBO);

        if (leaveSpecialRecords.getId() != null) {
            throw new ServiceException(ErrorCode.ID_AUTO_INCREMENT);
        }
        if (leaveSpecialRecords.getEndDate().isBefore(leaveSpecialRecords.getStartDate())) {
            throw new ServiceException(ErrorCode.LEAVE_SPECIAL_RECORD_END_TIME_EARLIER_THAN_START);
        }
        leaveSpecialRecordsRepository.insert(leaveSpecialRecords);
    }

    private void update(LeaveSpecialRecordsBO leaveSpecialRecordsBO) {
        LeaveSpecialRecords leaveSpecialRecords = LeaveSpecialRecordsMapper.INSTANCE.leaveSpecialRecordsBOToLeaveSpecialRecords(leaveSpecialRecordsBO);

        if (leaveSpecialRecordsRepository.selectById(leaveSpecialRecords.getId()) == null) {
            throw new ServiceException(ErrorCode.LEAVE_SPECIAL_RECORD_ID_NOT_FOUND);
        }

        if (leaveSpecialRecords.getEndDate().isBefore(leaveSpecialRecords.getStartDate())) {
            throw new ServiceException(ErrorCode.LEAVE_SPECIAL_RECORD_END_TIME_EARLIER_THAN_START);
        }

        leaveSpecialRecordsRepository.updateById(leaveSpecialRecords);
    }

    public void deleteById(Integer leaveSpecialRecordsId) {
        int rows = leaveSpecialRecordsRepository.deleteById(leaveSpecialRecordsId);
        if (rows != 1) {
            throw new ServiceException(ErrorCode.FAILURE);
        }
    }

    /**
     * 申請休假選單用
     * @param employeeId
     * @return
     */
    public List<LeaveSpecialRecordsVO> queryByEmployeeId(Integer employeeId) {
        List<LeaveSpecialRecords> leaveSpecialRecordsList = leaveSpecialRecordsRepository.getByEmployeeId(employeeId);
        Employee employee = employeeRepository.selectById(employeeId);
        if (employee == null)
            throw new ServiceException(ErrorCode.EMPLOYEE_ERROR);
        Department department = departmentRepository.selectById(employee.getDepartmentId());

        // 過濾掉剩餘時數為0或小於0的假別紀錄
        List<LeaveSpecialRecords> filteredLeaveSpecialRecordsList = leaveSpecialRecordsList.stream()
                .filter(record -> leaveRecordsService.calculateAvailableLeaveHours(record) > 0)
                .collect(Collectors.toList());

        // 若存在「生理假(6)」的有效紀錄，則隱藏「生理病假(5)」（6 用完才會顯示 5）
        List<LeaveSpecialRecords> effectiveList = new ArrayList<>(filteredLeaveSpecialRecordsList);
        boolean hasMenstrualSick = filteredLeaveSpecialRecordsList.stream()
                .anyMatch(record -> LeaveType.MENSTRUAL_SICK_LEAVE.getLeaveType().equals(record.getLeaveTypes()));
        if (hasMenstrualSick) {
            effectiveList = filteredLeaveSpecialRecordsList.stream()
                    .filter(record -> !LeaveType.MENSTRUAL_LEAVE.getLeaveType().equals(record.getLeaveTypes()))
                    .collect(Collectors.toList());
        }

        // 保留特休和選休假中開始時間最早的紀錄
        List<LeaveSpecialRecords> finalLeaveSpecialRecordsList = filterEarliestLeaveByType(
                effectiveList, LeaveType.ANNUAL_LEAVE, LeaveType.OPTIONAL_LEAVE);

        return finalLeaveSpecialRecordsList.stream()
                .map(record -> {
                    LeaveSpecialRecordsVO recordVO = LeaveSpecialRecordsMapper.INSTANCE.leaveSpecialRecordsToLeaveSpecialRecordsVO(record, employee, department);
                    recordVO.setChineseName(LeaveType.getChineseNameByLeaveType(record.getLeaveTypes()));
                    return recordVO;
                })
                .sorted(Comparator.comparing(LeaveSpecialRecordsVO::getLeaveTypes))
                .toList();
    }

    //過濾選取假別的最早未過期的假別紀錄
    private List<LeaveSpecialRecords> filterEarliestLeaveByType(List<LeaveSpecialRecords> records, LeaveType... leaveTypes) {
        List<LeaveSpecialRecords> result = new ArrayList<>(records);

        for (LeaveType leaveType : leaveTypes) {
            Optional<LeaveSpecialRecords> matchingRecord =records.stream()
                    .filter(record -> leaveType.getLeaveType().equals(record.getLeaveTypes())
                            && record.getStartDate().isBefore(LocalDateTime.now())//檢查假別是否生效
                            && record.getEndDate().isAfter(LocalDateTime.now())) // 排除過期的紀錄
                    .sorted(Comparator.comparing(LeaveSpecialRecords::getStartDate)) // 根據 startDate 排序
                    .findFirst();

            if (matchingRecord.isPresent()) {
                // 若找到符合的紀錄，就移除所有該類型但不是這筆的紀錄
                result.removeIf(r -> leaveType.getLeaveType().equals(r.getLeaveTypes()) && !r.equals(matchingRecord.get()));
            } else {
                // 若找不到符合條件的紀錄，則移除所有該 leaveType 的紀錄
                result.removeIf(r -> leaveType.getLeaveType().equals(r.getLeaveTypes()));
            }
        }
        return result;
    }

    public List<LeaveSpecialRecordsVO> getAllLeaveSpecialRecords() {
        // 查詢所有 LeaveSpecialRecords
        List<LeaveSpecialRecords> leaveSpecialRecordsList = leaveSpecialRecordsRepository.selectList(new QueryWrapper<>());

        if (leaveSpecialRecordsList == null || leaveSpecialRecordsList.isEmpty())
            return new ArrayList<>();

        // 查詢有假別設定的員工資料，並以 Map<employeeId, Employee> 儲存
        List<Integer> employeeIds = leaveSpecialRecordsList.stream()
                .map(LeaveSpecialRecords::getEmployeeId)
                .distinct()
                .toList();
        Map<Integer, Employee> employeeMap = employeeRepository.selectBatchIds(employeeIds)
                .stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));

        // 查詢有假別設定的部門資料，並以 Map<departmentId, Department> 儲存
        List<Integer> departmentIds = employeeMap.values().stream()
                .map(Employee::getDepartmentId)
                .distinct()
                .toList();
        Map<Integer, Department> departmentMap = departmentRepository.selectBatchIds(departmentIds)
                .stream()
                .collect(Collectors.toMap(Department::getId, department -> department));

        return leaveSpecialRecordsList.stream()
                .map(record -> {
                    // 從 map 中取得對應的 employee 和 department
                    Employee employee = employeeMap.get(record.getEmployeeId());
                    if (employee == null)
                        throw new ServiceException(ErrorCode.EMPLOYEE_ERROR);
                    Department department = departmentMap.get(employee.getDepartmentId());
                    LeaveSpecialRecordsVO recordVO = LeaveSpecialRecordsMapper.INSTANCE.leaveSpecialRecordsToLeaveSpecialRecordsVO(record, employee, department);
                    recordVO.setChineseName(LeaveType.getChineseNameByLeaveType(record.getLeaveTypes()));
                    return recordVO;
                })
                .toList();
    }
}
