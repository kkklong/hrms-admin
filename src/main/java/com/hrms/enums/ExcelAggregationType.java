package com.hrms.enums;

import com.hrms.annotation.ExcelCell;

/**
 * 定義 Excel 中可用的彙總類型。
 * <p>
 * 此列舉類別用於描述在生成 Excel 報表時，
 * 可以應用於單元格範圍的彙總操作類型。
 * 包含以下幾種彙總操作：
 * <ul>
 *     <li>NONE: 不進行任何彙總操作。</li>
 *     <li>SUM: 對指定範圍的數值求和。</li>
 *     <li>COUNT: 計算指定範圍內的非空單元格數量。</li>
 *     <li>AVERAGE: 計算指定範圍內的數值平均值。</li>
 *     <li>MAX: 找出指定範圍內的最大值。</li>
 *     <li>MIN: 找出指定範圍內的最小值。</li>
 * </ul>
 * <p>
 * 使用{@link ExcelUtils#createAggregationRow}會建立對應的彙總計算
 *
 * @see ExcelUtils
 * @see ExcelCell
 */
public enum ExcelAggregationType {
    NONE,
    SUM,
    COUNT,
    AVERAGE,
    MAX,
    MIN;
}
