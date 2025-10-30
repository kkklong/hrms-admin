package com.hrms.common.alertRecipient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.common.alertRecipient.entity.AttendanceAlertRecipient;
import com.hrms.common.alertRecipient.repository.AttendanceAlertRecipientRepository;
import com.hrms.entity.Employee;
import com.hrms.enums.AlertEventType;
import com.hrms.repository.EmployeeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 事件通知之收件人清單 服務實現類
 * </p>
 *
 * @author System
 * @since 2025-08-19
 */
@Slf4j
@Service
@Transactional
public class AttendanceAlertRecipientService extends ServiceImpl<AttendanceAlertRecipientRepository, AttendanceAlertRecipient> {


    @Resource
    private AttendanceAlertRecipientRepository attendanceAlertRecipientRepository;
    @Resource
    private EmployeeRepository employeeRepository;

    /**
     * 取得對應事件的通知清單
     */
    public List<Employee> getCcEmployeesByEvent(AlertEventType eventType) {
        List<AttendanceAlertRecipient> subs = attendanceAlertRecipientRepository.selectList(
                new QueryWrapper<AttendanceAlertRecipient>()
                        .eq("event_type", eventType.getCode())
                        .eq("enabled", 1)
        );
        if (subs.isEmpty()) return Collections.emptyList();

        List<Integer> ids = subs.stream().map(AttendanceAlertRecipient::getEmployeeId).distinct().toList();
        if (ids.isEmpty()) return Collections.emptyList();

        List<Employee> emps = employeeRepository.selectBatchIds(ids);
        return emps.stream()
                .filter(e -> e.getStatus() != null && e.getStatus() == 1)
                .distinct()
                .toList();
    }

    /**
     * 更新通知清單
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCcEmployeesByEvent(AlertEventType eventType, List<Integer> employeeIds) {
        final int evt = eventType.getCode();
        final Set<Integer> target = employeeIds == null ? Collections.emptySet() : new HashSet<>(employeeIds);

        // 目通知人員清單
        List<AttendanceAlertRecipient> existing = attendanceAlertRecipientRepository.selectList(
                new QueryWrapper<AttendanceAlertRecipient>()
                        .eq("event_type", evt)
        );
        //通知啟用人員
        Set<Integer> enabledNow = existing.stream()
                .filter(r -> r.getEnabled() != null && r.getEnabled() == 1)
                .map(AttendanceAlertRecipient::getEmployeeId)
                .collect(Collectors.toSet());
        //通知停用人員
        Set<Integer> disabledNow = existing.stream()
                .filter(r -> r.getEnabled() == null || r.getEnabled() == 0)
                .map(AttendanceAlertRecipient::getEmployeeId)
                .collect(Collectors.toSet());

        // 需要停用的人員名單（目前啟用但不在目標清單）
        Set<Integer> toDisable = new HashSet<>(enabledNow);
        toDisable.removeAll(target);

        // 需要啟用的人員名單（目標清單裡但目前停用）
        Set<Integer> toEnable = new HashSet<>(target);
        toEnable.retainAll(disabledNow);

        // 需要新增的（通知人員清單裡不存在）
        Set<Integer> toInsert = new HashSet<>(target);
        toInsert.removeAll(enabledNow);
        toInsert.removeAll(disabledNow);

        // 停用
        if (!toDisable.isEmpty()) {
            attendanceAlertRecipientRepository.update(
                    new AttendanceAlertRecipient() {{
                        setEnabled((byte) 0);
                    }},
                    new UpdateWrapper<AttendanceAlertRecipient>()
                            .eq("event_type", evt)
                            .in("employee_id", toDisable)
                            .eq("enabled", 1)
            );
        }
        // 啟用
        if (!toEnable.isEmpty()) {
            attendanceAlertRecipientRepository.update(
                    new AttendanceAlertRecipient() {{
                        setEnabled((byte) 1);
                    }},
                    new UpdateWrapper<AttendanceAlertRecipient>()
                            .eq("event_type", evt)
                            .in("employee_id", toEnable)
                            .eq("enabled", 0)
            );
        }
        // 新增
        if (!toInsert.isEmpty()) {
            List<AttendanceAlertRecipient> inserts = toInsert.stream().map(empId -> {
                AttendanceAlertRecipient r = new AttendanceAlertRecipient();
                r.setEmployeeId(empId);
                r.setEventType((byte) evt);
                r.setEnabled((byte) 1);
                return r;
            }).toList();
            this.saveBatch(inserts);
        }
    }

    /**
     * 更新「上班未打卡」事件的通知名單
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateClockInCcEmployees(List<Integer> employeeIds) {
        updateCcEmployeesByEvent(AlertEventType.CLOCK_IN_MISS, employeeIds);
    }


}
