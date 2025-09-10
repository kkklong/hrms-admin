package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Employee;
import com.hrms.entity.LeaveSpecialRecords;
import com.hrms.entity.LeaveSpecialRecordsTemplate;
import com.hrms.entity.mapstruct.LeaveSpecialRecordsTemplateMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.LeaveType;
import com.hrms.exception.ServiceException;
import com.hrms.model.vo.LeaveSpecialRecordsTemplateVO;
import com.hrms.repository.LeaveSpecialRecordsRepository;
import com.hrms.repository.LeaveSpecialRecordsTemplateRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-08-02
 */
@Slf4j
@Service
@Transactional
public class LeaveSpecialRecordsTemplateService extends ServiceImpl<LeaveSpecialRecordsTemplateRepository, LeaveSpecialRecordsTemplate> {

    @Resource
    LeaveSpecialRecordsTemplateRepository LeaveSpecialRecordsTemplateRepository;

    @Resource
    LeaveSpecialRecordsRepository leaveSpecialRecordsRepository;

    public List<LeaveSpecialRecordsTemplateVO> getLeaveSpecialRecordsTemplate() {

        List<LeaveSpecialRecordsTemplate> leaveSpecialRecordsTemplates = LeaveSpecialRecordsTemplateRepository.selectList(new QueryWrapper<>());
        List<LeaveSpecialRecordsTemplateVO> leaveSpecialRecordsTemplateVOs = leaveSpecialRecordsTemplates.stream()
                .map(template -> {
                    LeaveSpecialRecordsTemplateVO vo = LeaveSpecialRecordsTemplateMapper.INSTANCE.toLeaveSpecialRecordsTemplateVO(template);
                    String chineseName = LeaveType.getChineseNameByLeaveType(template.getLeaveTypes());
                    if (chineseName != null) {
                        vo.setChineseName(chineseName);
                    }
                    return vo;
                })
                .toList();
        return leaveSpecialRecordsTemplateVOs;
    }

    public void setLeaveSpecialRecords(Employee employee, int year) {
        List<LeaveSpecialRecordsTemplate> leaveSpecialRecordsTemplates = this.baseMapper.selectList(new QueryWrapper<>());
        List<LeaveSpecialRecords> list = new ArrayList<>();

        leaveSpecialRecordsTemplates.stream()
                .filter(e -> between(e.getLeaveTypes(), e.getYearData(), employee.getEntryDate(), year))
                .forEach(e -> {
                    LocalDateTime startDate = this.getStartDateTime(e.getLeaveTypes(), employee.getEntryDate(), year, e.getYearData());
                    LocalDateTime endDate = this.getEndDateTime(e.getLeaveTypes(), startDate, year);

                    LeaveSpecialRecords leaveSpecialRecords = new LeaveSpecialRecords();
                    leaveSpecialRecords.setEmployeeId(employee.getId());
                    leaveSpecialRecords.setLeaveTypes(e.getLeaveTypes());
                    leaveSpecialRecords.setStartDate(startDate);
                    leaveSpecialRecords.setEndDate(endDate);
                    leaveSpecialRecords.setSalaryStandard(e.getSalaryStandard());
                    leaveSpecialRecords.setFullAttendanceBonus(e.getFullAttendanceBonus());
                    leaveSpecialRecords.setMinLeaveUnit(e.getMinLeaveUnit());
                    leaveSpecialRecords.setMaxLeaveDays(e.getMaxLeaveDays());
                    leaveSpecialRecords.setContinuousLeave(e.getContinuousLeave());
                    leaveSpecialRecords.setContinuousLeave(e.getContinuousLeave());
                    leaveSpecialRecords.setAdvanceApplication(e.getAdvanceApplication());
                    Boolean attachmentRequired = LeaveType.PAID_SICK_LEAVE.getLeaveType().equals(e.getLeaveTypes());
                    leaveSpecialRecords.setAttachmentRequired(attachmentRequired);
                    list.add(leaveSpecialRecords);
                });

        if (ObjectUtils.isNotEmpty(list)) {
            list.stream().forEach(e -> {
                LambdaUpdateWrapper<LeaveSpecialRecords> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(LeaveSpecialRecords::getEmployeeId, employee.getId());
                updateWrapper.eq(LeaveSpecialRecords::getLeaveTypes, e.getLeaveTypes());
                updateWrapper.eq(LeaveSpecialRecords::getStartDate, e.getStartDate());
                int count = leaveSpecialRecordsRepository.update(e, updateWrapper);
                if (count == 0){
                    leaveSpecialRecordsRepository.insert(e);
                } else if (count > 1) {
                    throw new ServiceException(ErrorCode.UPDATE_LEAVE_RECORD_RECORDS_FAIL);
                }
            });
        }

    }

    private Boolean between(String leaveTypes, Float yearData, LocalDate entryDate, int year) {
        if ("0".equals(leaveTypes) || "1".equals(leaveTypes)) {

            if ("0".equals(leaveTypes) && yearData == 0.5) {
                if (entryDate.getYear() == year) {
                    return true;
                } else {
                    return false;
                }
            }

            LocalDate startDate  = LocalDate.of(year, entryDate.getMonthValue(), entryDate.getDayOfMonth());
            LocalDate addDate = entryDate.plusYears((long) Float.parseFloat(yearData + ""));
            if (Duration.between(addDate.atStartOfDay(), startDate.atStartOfDay()).toDays() == 0) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private LocalDateTime getStartDateTime(String leaveTypes, LocalDate date, int year, Float yearData) {
        LocalDateTime newDateTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0);

        if (date.getYear() == year) {
            if (!"0".equals(leaveTypes) && !"1".equals(leaveTypes)) {
                //入職月份滿3個月且不超過當年度，才給予有薪病假
                if (LeaveType.PAID_SICK_LEAVE.getLeaveType().equals(leaveTypes) && date.getMonthValue() + 3 < 12) {
                    return getDateTime(newDateTime, 0, 3, 0);
                } else {
                    return newDateTime;
                }
            }
        }

        if ("0".equals(leaveTypes) || "1".equals(leaveTypes)) {
            if (yearData == 0.5 && date.getYear() == year) {
                return getDateTime(newDateTime,0,6,0);
            }
            return LocalDateTime.of(year, date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0);
        }

        return  LocalDateTime.of(year, 1, 1, 0, 0, 0);
    }

    private LocalDateTime getEndDateTime(String leaveTypes, LocalDateTime date, int year) {
        LocalDateTime newDateTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23,59,59);
        if ("0".equals(leaveTypes)) {
            return  getDateTime(newDateTime, 1, 6, -1);
        }

        if ("1".equals(leaveTypes)) {
            return  getDateTime(newDateTime, 1, 0, -1);
        }

        return  LocalDateTime.of(year, 12, 31, 23,59,59);
    }

    private LocalDateTime getDateTime(LocalDateTime newDateTime, int year, int month, int day) {
        LocalDateTime dateTime = newDateTime.plusYears(year).plusMonths(month).plusDays(day);
        return dateTime;
    }
}

