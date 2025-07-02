package com.hrms.enums;

import com.hrms.util.ExcelUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 目前 {@link ExcelUtils} 可支援的 Excel 文件格式。
 */
@Getter
@AllArgsConstructor
public enum ExcelType {
    XLS("xls"),
    XLSX("xlsx");
    private final String name;

    public String getFullExtension() {
        return "." + name;
    }
}
