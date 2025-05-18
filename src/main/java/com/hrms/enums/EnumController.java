package com.hrms.enums;

import com.hrms.common.ApiResponse;
import com.hrms.entity.Config;
import com.hrms.model.vo.DropDownVo;
import com.hrms.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/enum")
@Tag(name = "列舉資料", description = "列舉資料API")
@Slf4j
public class EnumController {
    @Resource
    ConfigService configService;

    @Operation(summary = "取得公司列表", description = "取得公司列表 API")
    @GetMapping(path = "/getCompanyType")
    public ApiResponse<List<DropDownVo<Byte>>> getCompanyType() {
        List<DropDownVo<Byte>> data = Stream.of(CompanyType.values())
                .map(company -> new DropDownVo<>(company.getName(), company.getValue()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

    @Operation(summary = "取得員工狀態列表", description = "取得員工狀態列表API")
    @GetMapping(path = "/getEmployeeStatus")
    public ApiResponse<List<DropDownVo<Byte>>> getEmployeeStatus() {
        List<DropDownVo<Byte>> data = Stream.of(EmployeeStatus.values())
                .map(status -> new DropDownVo<>(status.getName(), status.getValue()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }

    @Operation(summary = "取得排班類型列表", description = "取得排班類型列表API")
    @GetMapping(path = "/getShiftType")
    public ApiResponse<List<DropDownVo<String>>> getShiftType() {
        List<Config> shiftTypes = configService.getShiftType();
        List<DropDownVo<String>> data = shiftTypes.stream()
                .map(shiftType -> new DropDownVo<>(shiftType.getName(), shiftType.getConfigKey()))
                .collect(Collectors.toList());
        return new ApiResponse<>(data);
    }
}
