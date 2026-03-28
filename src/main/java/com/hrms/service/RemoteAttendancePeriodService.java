package com.hrms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Config;
import com.hrms.entity.RawAttendanceRecords;
import com.hrms.entity.RemoteAttendancePeriod;
import com.hrms.entity.ShiftSchedules;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.UserInfo;
import com.hrms.model.bo.RawAttendanceRecordsBO;
import com.hrms.model.bo.RemoteAttendancePeriodBO;
import com.hrms.model.bo.RemoteAttendancePeriodDataBO;
import com.hrms.model.vo.RawAttendanceRecordsVO;
import com.hrms.model.vo.RemoteAttendancePeriodVo;
import com.hrms.model.vo.RemoteClockStatusVO;
import com.hrms.repository.RawAttendanceRecordsRepository;
import com.hrms.repository.RemoteAttendancePeriodRepository;
import com.hrms.repository.ShiftSchedulesRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2025-04-28
 */
@Slf4j
@Service
@Transactional
public class RemoteAttendancePeriodService extends ServiceImpl<RemoteAttendancePeriodRepository, RemoteAttendancePeriod> {

    @Resource
    private RemoteAttendancePeriodRepository remoteAttendancePeriodRepository;
    @Resource
    private ConfigService configService;
    @Resource
    private RawAttendanceRecordsService rawAttendanceRecordsService;
    @Resource
    private ShiftSchedulesRepository shiftSchedulesRepository;
    @Resource
    private RawAttendanceRecordsRepository rawAttendanceRecordsRepository;


    public List<RawAttendanceRecordsVO> queryRemote(RawAttendanceRecordsBO bo) {
        if (bo.getShowDetail()) {
            return rawAttendanceRecordsRepository.queryAllRemote(bo);
        } else {
            return rawAttendanceRecordsRepository.queryRemote(bo);
        }
    }

    /**
     * 新增或修改遠端打卡設定
     */
    public void saveRemoteAttendancePeriod(RemoteAttendancePeriodBO bo) {
        for (RemoteAttendancePeriodDataBO item : bo.getRemoteAttendancePeriodDataList()) {
            if (item.getId() != null)
                update(item);
            else
                create(item);
        }
    }

    /**
     * 新增遠端打卡設定
     */
    private void create(RemoteAttendancePeriodDataBO item) {

        checkShiftTypes(item.getAvailableShiftType());

        long count = remoteAttendancePeriodRepository.selectCount(
                new QueryWrapper<RemoteAttendancePeriod>()
                        .eq("remote_date", item.getRemoteDate())
        );
        if (count > 0) {
            throw new ServiceException(
                    ErrorCode.REMOTE_ATTENDANCE_DATE_DUPLICATE,
                    "日期(" + item.getRemoteDate() + ")");
        }
        RemoteAttendancePeriod entity = new RemoteAttendancePeriod();
        entity.setRemoteDate(item.getRemoteDate());
        entity.setAvailableShiftType(String.join(",", item.getAvailableShiftType()));
        remoteAttendancePeriodRepository.insert(entity);
    }

    /**
     * 修改遠端打卡設定
     */
    private void update(RemoteAttendancePeriodDataBO item) {
        RemoteAttendancePeriod existing = remoteAttendancePeriodRepository.selectById(item.getId());
        if (existing == null) {
            throw new ServiceException(ErrorCode.REMOTE_ATTENDANCE_ID_NOT_FOUND);
        }
        //檢查是否修改日期
        if (!existing.getRemoteDate().equals(item.getRemoteDate())) {
            throw new ServiceException(ErrorCode.REMOTE_ATTENDANCE_DATE_CANNOT_MODIFY);
        }
        checkShiftTypes(item.getAvailableShiftType());
        existing.setAvailableShiftType(
                String.join(",", item.getAvailableShiftType())
        );
        remoteAttendancePeriodRepository.updateById(existing);
    }


    /**
     * 檢查傳入的班別是否存在
     */
    private void checkShiftTypes(List<String> shiftTypes) {
        Set<String> validKeys = configService.getShiftType().stream()
                .map(Config::getConfigKey)
                .collect(Collectors.toSet());
        for (String type : shiftTypes) {
            if (!validKeys.contains(type)) {
                throw new ServiceException(
                        ErrorCode.INVALID_SHIFT_TYPE,
                        "班別(" + type + ")不存在"
                );
            }
        }
    }

    /**
     * 查詢指定日期區間內的遠端打卡設定
     */
    public List<RemoteAttendancePeriodVo> queryByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ServiceException(ErrorCode.END_TIME_EARLIER_THAN_START);
        }
        List<RemoteAttendancePeriod> list = remoteAttendancePeriodRepository.selectList(
                Wrappers.<RemoteAttendancePeriod>lambdaQuery()
                        .ge(RemoteAttendancePeriod::getRemoteDate, startDate)
                        .le(RemoteAttendancePeriod::getRemoteDate, endDate)
        );
        return list.stream()
                .map(e -> new RemoteAttendancePeriodVo()
                        .setId(e.getId())
                        .setRemoteDate(e.getRemoteDate())
                        .setAvailableShiftType(
                                e.getAvailableShiftType() == null
                                        ? Collections.emptyList()
                                        : Arrays.asList(e.getAvailableShiftType().split(","))
                        )
                )
                .toList();
    }

    /**
     * 查詢指定日期區間內需要遠端打卡的班別
     */
    public List<RemoteAttendancePeriodVo> queryRemoteShift(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ServiceException(ErrorCode.END_TIME_EARLIER_THAN_START);
        }
        List<RemoteAttendancePeriod> list = remoteAttendancePeriodRepository.selectList(
                Wrappers.<RemoteAttendancePeriod>lambdaQuery()
                        .ge(RemoteAttendancePeriod::getRemoteDate, startDate)
                        .le(RemoteAttendancePeriod::getRemoteDate, endDate)
        );
        return list.stream()
                .map(e -> new RemoteAttendancePeriodVo()
                        .setAvailableShiftType(
                                e.getAvailableShiftType() == null
                                        ? Collections.emptyList()
                                        : Arrays.asList(e.getAvailableShiftType().split(","))
                        )
                )
                .toList();
    }

    /**
     * 遠端上班打卡
     */
    public void remoteClockIn() {
        UserInfo user = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        ShiftSchedules shift = resolveShift(user.getId(), now);
        validateClock(user, shift, now, true);

        RawAttendanceRecords rawAttendanceRecords = new RawAttendanceRecords();
        rawAttendanceRecords.setAccount(user.getAccount());
        rawAttendanceRecords.setDateTime(now);
        rawAttendanceRecords.setRawData("remote_clockIn");
        rawAttendanceRecordsService.save(rawAttendanceRecords);
    }

    /**
     * 員工遠端下班打卡
     */
    public void remoteClockOut() {
        UserInfo user = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        ShiftSchedules shift = resolveShift(user.getId(), now);
        validateClock(user, shift, now, false);  // 驗證

        RawAttendanceRecords rawAttendanceRecords = new RawAttendanceRecords();
        rawAttendanceRecords.setAccount(user.getAccount());
        rawAttendanceRecords.setDateTime(now);
        rawAttendanceRecords.setRawData("remote_clockOut");
        rawAttendanceRecordsService.save(rawAttendanceRecords);
    }

    /**
     * 回傳使用者當前遠端打卡狀態
     */
    public RemoteClockStatusVO getClockStatus() {

        UserInfo user = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        RemoteClockStatusVO vo = new RemoteClockStatusVO();

        ShiftSchedules shift = resolveShift(user.getId(), now);
        //若沒排班，則不允許遠端打卡
        if (shift == null) {
            vo.setCanClockIn(false);
            vo.setCanClockOut(false);
            return vo;
        }
        today = shift.getShiftDate();
        //今天是否已打過遠端上班卡
        boolean hadClockIn = rawAttendanceRecordsRepository.exists(
                Wrappers.<RawAttendanceRecords>lambdaQuery()
                        .eq(RawAttendanceRecords::getAccount, user.getAccount())
                        .eq(RawAttendanceRecords::getRawData, "remote_clockIn")
                        .between(RawAttendanceRecords::getDateTime,
                                today.atStartOfDay(),
                                today.plusDays(1).atStartOfDay()));
        //判斷「上班打卡」按鈕
        boolean canClockIn;
        try {
            validateClock(user, shift, now, true);
            canClockIn = true;
        } catch (ServiceException e) {
            canClockIn = false;
        }
        vo.setCanClockIn(canClockIn);

        //判斷「下班打卡」按鈕
        boolean canClockOut = false;
        if (hadClockIn) {   //只有上班打卡過才可能開啟
            try {
                validateClock(user, shift, now, false);
                canClockOut = true;
            } catch (ServiceException e) {
                //若拋出例外代表不允許打下班卡
            }
        }
        vo.setCanClockOut(canClockOut);
        return vo;
    }


    /**
     * 遠端打卡驗證
     */
    private void validateClock(UserInfo user,
                               ShiftSchedules shift,
                               LocalDateTime now,
                               boolean isClockIn) {

        Map<String, Config> shiftTypeMap = configService.getShiftType().stream()
                .collect(Collectors.toMap(Config::getConfigKey, e -> e));

        LocalDate shiftDate = shift.getShiftDate();

        // 判斷是否開放遠端打卡，且班別符合
        RemoteAttendancePeriod period = remoteAttendancePeriodRepository.selectOne(
                Wrappers.<RemoteAttendancePeriod>lambdaQuery()
                        .eq(RemoteAttendancePeriod::getRemoteDate, shiftDate)
        );

        if (period == null) {
            throw new ServiceException(ErrorCode.REMOTE_CLOCK_DENIED, "日期:" + shiftDate);
        }

        List<String> allowedTypes = period.getAvailableShiftType() == null
                ? Collections.emptyList()
                : Arrays.stream(period.getAvailableShiftType().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (!allowedTypes.contains(shift.getShiftTypes())) {
            String shiftTypeName = shiftTypeMap.containsKey(shift.getShiftTypes())
                    ? shiftTypeMap.get(shift.getShiftTypes()).getName()
                    : shift.getShiftTypes();
            throw new ServiceException(ErrorCode.REMOTE_CLOCK_DENIED, "班別:" + shiftTypeName);
        }

        // 同日不得重複打卡
        Config cfg = shiftTypeMap.get(shift.getShiftTypes());
        if (cfg == null) {
            throw new ServiceException(ErrorCode.CONFIG_ID_NOT_FOUND);
        }
        LocalTime start = LocalTime.parse(cfg.getConfigValue1());  // 上班時間
        LocalTime end = LocalTime.parse(cfg.getConfigValue2()); //下班時間
        boolean crosses = start.isAfter(end);

        LocalDateTime dupStart = shiftDate.atStartOfDay();
        LocalDateTime dupEnd = crosses
                ? shiftDate.plusDays(1).atTime(end)
                : shiftDate.atTime(end);

        String rawFlag = isClockIn ? "remote_clockIn" : "remote_clockOut";
        long dup = rawAttendanceRecordsService.count(
                Wrappers.<RawAttendanceRecords>lambdaQuery()
                        .eq(RawAttendanceRecords::getAccount, user.getAccount())
                        .eq(RawAttendanceRecords::getRawData, rawFlag)
                        .between(RawAttendanceRecords::getDateTime, dupStart, dupEnd));

        if (dup > 0)
            throw new ServiceException(ErrorCode.REMOTE_CLOCK_DUPLICATE);

        LocalDateTime shiftStartDT = shiftDate.atTime(start);

        if (isClockIn && now.isBefore(shiftStartDT.minusMinutes(30))) {
            throw new ServiceException(ErrorCode.REMOTE_CLOCK_TIME_ILLEGAL);
        }
        if (!isClockIn && now.isBefore(shiftStartDT.plusMinutes(30))) {
            throw new ServiceException(ErrorCode.REMOTE_CLOCK_TIME_ILLEGAL);
        }
    }

    /**
     * 依現在時間取得正確班別（處理跨日夜班）
     */
    private ShiftSchedules resolveShift(Integer empId, LocalDateTime now) {

        LocalDate today = now.toLocalDate();

        // 今天班別
        ShiftSchedules todayShift = shiftSchedulesRepository.getByDateAndEmployeeId(today, empId);

        if (todayShift != null && inShiftRange(todayShift, now)) {
            return todayShift;
        }

        //  昨天班別（可能是跨日班延續到今天）
        ShiftSchedules yestShift =
                shiftSchedulesRepository.getByDateAndEmployeeId(today.minusDays(1), empId);
        if (yestShift != null && inShiftRange(yestShift, now)) {
            return yestShift;
        }
        //兩筆都不涵蓋 now → 代表此刻沒有班別
        return null;
    }

    /**
     * now 是否落在排班的 [startDT, endDT] 區段內
     */
    private boolean inShiftRange(ShiftSchedules s, LocalDateTime now) {
        Config cfg = configService.getShiftType().stream()
                .filter(c -> c.getConfigKey().equals(s.getShiftTypes()))
                .findFirst().orElse(null);
        if (cfg == null) return false;

        LocalDate date = s.getShiftDate();
        LocalTime start = LocalTime.parse(cfg.getConfigValue1());
        LocalTime end = LocalTime.parse(cfg.getConfigValue2());

        // 跨日班：end ≤ start → 結束時間算到翌日
        LocalDateTime startDT = date.atTime(start).minusMinutes(30);
        LocalDateTime endDT = start.isAfter(end)
                ? date.plusDays(1).atTime(end).plusHours(1)
                : date.atTime(end).plusHours(1);
        return !now.isBefore(startDT) && !now.isAfter(endDT);
    }
}
