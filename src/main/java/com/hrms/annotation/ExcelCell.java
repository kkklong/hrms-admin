package com.hrms.annotation;

import com.hrms.enums.ExcelAggregationType;
import com.hrms.util.DateUtils.DatePattern;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 用於標記 Excel 欄位的註解，配置欄位在 Excel 表格中的映射屬性，適用於 {@link ExcelUtils} 導入導出操作。
 *
 * <p>此註解應用於 Java 類別的欄位，用來指定欄位在 Excel 表格中的相關屬性，包括列索引、標題、
 * 日期格式、列寬、儲存格格式以及彙總類型等。
 *
 * <p>在導入資料時，此註解僅會使用 {@code columnIndex} 來識別 Excel 中的資料欄位，
 * 以便將其正確映射到對應的 Java 欄位上。其他屬性（例如標題、格式）僅在導出資料時生效，
 * 用於控制 Excel 的格式化和顯示效果。
 *
 * <p>使用此註解時，必須明確設定 {@code columnIndex}，其餘屬性可根據需求選擇性配置。
 *
 * @see ExcelUtils
 * @see DatePattern
 * @see ExcelAggregationType
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelCell {

    /**
     * 指定欄位在 Excel 中的列索引位置。
     * 必須設定以確保欄位能正確映射到 Excel 表格中的相應列。
     *
     * @return 欄位的列索引
     */
    int columnIndex();

    /**
     * 欄位在 Excel 中的標題名稱。
     * 如果未指定標題，將使用預設的空字串。
     *
     * @return 欄位的標題
     */
    String title() default "";

    /**
     * 欄位的列寬（單位為字元數）。
     * 如果值為 0，則表示使用 Excel 的預設列寬。
     *
     * @return 欄位的列寬
     */
    int columnWidth() default 0;

    /**
     * 日期格式模式，用於將日期型別的欄位轉換為字串時所使用的格式。
     *
     * <p>當設置為 {@link DatePattern#NONE} 時，在導出 Excel 時會使用原始的時間格式顯示日期。
     * 當設置為其他格式時，則會將日期轉換為字串並以指定的格式顯示。
     *
     * <p><b>注意：</b>此設定僅適用於欄位類型為 {@link  LocalDate} 和 {@link LocalDateTime}。
     * 若應用於其他日期類型（例如 {@link Date} 或 {@link Calendar}），將不會生效。
     *
     * @return 日期格式模式
     */
    DatePattern datePattern() default DatePattern.NONE;

    /**
     * Excel 儲存格格式，用於指定儲存格的自定義格式。
     *
     * <p>常見的 Excel 格式範例：
     * <ul>
     *     <li>數字格式：{@code "0"} - 顯示整數</li>
     *     <li>小數格式：{@code "0.00"} - 顯示兩位小數</li>
     *     <li>百分比格式：{@code "0.00%"} - 顯示百分比，保留兩位小數</li>
     *     <li>貨幣格式：{@code "$#,##0.00"} - 顯示帶有千分位的貨幣格式，保留兩位小數</li>
     *     <li>日期格式：{@code "yyyy/MM/dd"} - 顯示為年/月/日</li>
     *     <li>時間格式：{@code "hh:mm:ss"} - 顯示為時:分:秒</li>
     *     <li>文字格式：{@code "@"} - 將內容顯示為文字</li>
     * </ul>
     *
     * 預設值為空字串，表示不使用特別的格式。
     */
    String cellFormat() default "";

    /**
     * 彙總類型，用於指定如何在 Excel 中對資料進行彙總操作，例如總和、計數等。
     * 預設值為 {@link ExcelAggregationType#NONE}，表示不進行任何彙總操作。
     *
     * @return 彙總操作的類型
     */
    ExcelAggregationType aggregationType() default ExcelAggregationType.NONE;

    /**
     * Excel 自訂義公式。
     * 可搭配 {@link #cellFormulaRowBase()}
     * 來設定公式配置的基準位置，目前不支援column偏移處理。
     * <p>
     * 範例:<br>
     * 1. 一般使用(預設基準為第一列):<br>
     *    cellFormula = "A3+$A$4+A5"<br>
     *    <br>
     *    第一列: A3+$A$4+A5 (原始公式)<br>
     *    第二列: A4+$A$4+A6 (A3,A5向下位移，$A$4固定不變)<br>
     *    第三列: A5+$A$4+A7 (A3,A5向下位移，$A$4固定不變)<br>
     * <p>
     * 2. 指定基準列(第三列為基準):<br>
     *    cellFormula = "A3+$A$4+A5"<br>
     *    cellFormulaRowBasic = 3<br>
     *    <br>
     *    第一列: A1+$A$4+A3 (A3,A5向上位移2列，$A$4固定不變)<br>
     *    第二列: A2+$A$4+A4 (A3,A5向上位移1列，$A$4固定不變)<br>
     *    第三列: A3+$A$4+A5 (基準列)<br>
     *    第四列: A4+$A$4+A6 (A3,A5向下位移1列，$A$4固定不變)<br>
     *    第五列: A5+$A$4+A7 (A3,A5向下位移2列，$A$4固定不變)<br>
     *
     * @return 套用的公式
     */
    String cellFormula() default "";

    /**
     * 公式計算的基準列位置。
     * 僅在設定 {@link #cellFormula()} 時生效。
     * 設為 -1 時，將使用第一列作為基準列進行公式套用。
     *
     * @return 基準列索引
     */
    int cellFormulaRowBase() default -1;
}

