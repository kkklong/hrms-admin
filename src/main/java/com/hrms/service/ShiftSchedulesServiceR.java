package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Config;
import com.hrms.entity.ShiftSchedules;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.bo.ShiftTypeBO;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.ShiftSchedulesRepository;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class ShiftSchedulesServiceR extends ServiceImpl<ShiftSchedulesRepository, ShiftSchedules> {

    @Resource
    private ConfigService configService;
    @Resource
    private ShiftSchedulesRepository shiftSchedulesRepository;
    @Resource
    private EmployeeRepository employeeRepository;
    @Resource
    private DepartmentRepository departmentRepository;
    @Resource
    private EmailService emailService;

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



}
