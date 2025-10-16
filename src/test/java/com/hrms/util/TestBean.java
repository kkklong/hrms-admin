package com.hrms.util;

import com.hrms.annotation.ExcelCell;
import com.hrms.enums.ExcelAggregationType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Data
class TestBean {
    @ExcelCell(columnIndex = 0, title = "name")
    String name;
    @ExcelCell(columnIndex = 1, title = "sum", aggregationType = ExcelAggregationType.SUM)
    Integer sum;
    @ExcelCell(columnIndex = 2, title = "count", aggregationType = ExcelAggregationType.COUNT)
    AtomicLong count;
    @ExcelCell(columnIndex = 3, title = "average", aggregationType = ExcelAggregationType.AVERAGE)
    Float average;
    @ExcelCell(columnIndex = 4, title = "min", aggregationType = ExcelAggregationType.MIN)
    BigDecimal min;
    @ExcelCell(columnIndex = 5, title = "max", aggregationType = ExcelAggregationType.MAX)
    Double max;
    @ExcelCell(columnIndex = 6, title = "localDate", datePattern = DateUtils.DatePattern.YYYY_MM_DD_DASH)
    LocalDate localDate;
    @ExcelCell(columnIndex = 7, title = "localDateTime")
    LocalDateTime localDateTime;
    @ExcelCell(columnIndex = 8, title = "date", cellFormat = "mm/dd HH:MM")
    Date date;
    @ExcelCell(columnIndex = 9, title = "formula", aggregationType = ExcelAggregationType.SUM, cellFormula = "B1 + C1 + C$3 + 100", cellFormat = "#,##0.00")
    Integer formula;
}
