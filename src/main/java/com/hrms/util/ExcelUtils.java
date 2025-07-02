package com.hrms.util;

import com.hrms.annotation.ExcelCell;
import com.hrms.enums.ErrorCode;
import com.hrms.enums.ExcelAggregationType;
import com.hrms.enums.ExcelType;
import com.hrms.exception.FileIOException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.*;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.hrms.util.DateUtils.DatePattern.YYYY_MM_DD_DASH;
import static com.hrms.util.DateUtils.DatePattern.YYYY_MM_DD_HH_MM_SS_DASH;

/**
 * Excel 工具類，用於創建和處理 Excel 工作簿，以及將 Excel 文件中的數據轉換為 Java Bean。
 *
 * <p>此工具類適用於處理 Excel 資料的導入與導出操作，並將 Excel 表格中的數據映射到 Java Bean。
 * <p>在使用此工具類時，Java Bean 類別中的欄位需要搭配 {@link ExcelCell} 註解一起使用，以配置 Excel
 * 中的欄位屬性，如欄位索引、標題、格式等。
 *
 * <p>此工具類僅支援 {@link ExcelType} 枚舉中定義的格式。
 *
 * @see ExcelCell
 * @see ExcelType
 * @see ExcelAggregationType
 */
@Slf4j
public class ExcelUtils {
    public static Workbook createWorkBook(ExcelType excelType) {
        return switch (excelType) {
            case XLS -> createXlsWorkBook();
            case XLSX -> createXlsxWorkBook();
        };
    }

    public static Workbook createXlsWorkBook() {
        return new HSSFWorkbook();
    }

    public static Workbook createXlsxWorkBook() {
        return new XSSFWorkbook();
    }

    public static Workbook getWorkBook(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        validateFileName(fileName);
        if (fileName.endsWith(ExcelType.XLSX.getFullExtension())) {
            return new XSSFWorkbook(file.getInputStream());
        }
        return new HSSFWorkbook(file.getInputStream());
    }

    public static Workbook getWorkBook(File file) throws IOException, InvalidFormatException {
        String fileName = file.getName();
        validateFileName(fileName);
        if (fileName.endsWith(ExcelType.XLSX.getFullExtension())) {
            return new XSSFWorkbook(file);
        }
        try (POIFSFileSystem fs = new POIFSFileSystem(file)) {
            return new HSSFWorkbook(fs.getRoot(), true);
        }
    }

    public static Workbook getWorkBook(String fileName) {
        validateFileName(fileName);
        if (fileName.endsWith(ExcelType.XLSX.getFullExtension())) {
            return new XSSFWorkbook();
        }
        return new HSSFWorkbook();
    }

    public static FormulaParsingWorkbook getFormulaParsingWorkbook(Workbook workbook) {
        if (workbook instanceof XSSFWorkbook) {
            return XSSFEvaluationWorkbook.create((XSSFWorkbook) workbook);
        } else if (workbook instanceof HSSFWorkbook) {
            return HSSFEvaluationWorkbook.create((HSSFWorkbook) workbook);
        }
        log.error("getFormulaParsingWorkbook 不支援的workbook類型: {}", workbook.getClass().getName());
        throw new FileIOException(ErrorCode.EXPORT_FAIL);
    }

    public static FormulaRenderingWorkbook getFormulaRenderingWorkbook(Workbook workbook) {
        if (workbook instanceof XSSFWorkbook) {
            return XSSFEvaluationWorkbook.create((XSSFWorkbook) workbook);
        } else if (workbook instanceof HSSFWorkbook) {
            return HSSFEvaluationWorkbook.create((HSSFWorkbook) workbook);
        }
        log.error("getFormulaRenderingWorkbook 不支援的workbook類型: {}", workbook.getClass().getName());
        throw new FileIOException(ErrorCode.EXPORT_FAIL);
    }

    private static void validateFileName(String fileName) {
        if (fileName == null
                || Arrays.stream(ExcelType.values())
                .noneMatch(excelType -> fileName.endsWith(excelType.getFullExtension()))) {
            log.error("錯誤的檔案格式 {}", fileName);
            throw new FileIOException(ErrorCode.EXPORT_FORMAT);
        }
    }

    private static <T> List<T> getBean(Class<T> clazz, Workbook book) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        Sheet sheet = book.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            T t = convertRowToBean(sheet.getRow(i), clazz);
            list.add(t);
        }
        return list;
    }

    public static <T> List<T> getBeanFromFile(String path, Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        try (Workbook book = getWorkBook(path)) {
            return getBean(clazz, book);
        }
    }

    public static <T> List<T> getBeanFromFile(MultipartFile file, Class<T> clazz) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try (Workbook book = getWorkBook(file)) {
            return getBean(clazz, book);
        }
    }

    public static <T> List<T> getBeanFromFile(File file, Class<T> clazz) throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try (Workbook book = getWorkBook(file)) {
            return getBean(clazz, book);
        }
    }

    public static <T> void beanToExcelWithTitleAndAggregation(Workbook workbook, Sheet sheet, Class<T> clazz, List<T> beans) {
        createTitle(workbook, sheet, clazz);
        int firstRow = sheet.getLastRowNum() + 1;
        createRowFromBean(sheet, clazz, beans);
        createAggregationRow(sheet, firstRow, clazz);
        adjustColumnWidths(sheet, clazz);
    }

    public static <T> void createTitle(Workbook workbook, Sheet sheet, Class<T> clazz) {
        Row row = sheet.createRow(Math.max(sheet.getLastRowNum(), 0));

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        Cell cell;
        Field[] fields = clazz.getDeclaredFields();
        Set<Integer> columnIndexSet = new HashSet<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelCell.class)) {
                field.setAccessible(true);
                ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
                int columnIndex = excelCell.columnIndex();
                if (!columnIndexSet.add(columnIndex)) {
                    log.error("createTitle 重複的欄位: {}", columnIndex);
                    throw new FileIOException(ErrorCode.EXPORT_FAIL);
                }

                cell = row.createCell(columnIndex);
                cell.setCellValue(excelCell.title());
                cell.setCellStyle(style);
            }
        }
    }

    public static <T> void createRowFromBean(Sheet sheet, Class<T> clazz, List<T> beans) {
        if (beans.isEmpty()) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (T bean : beans) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelCell.class)) {
                    try {
                        createCellFromBean(sheet.getWorkbook(), bean, field, row);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        log.error("解析bean失敗 bean:{} field:{}", bean, field.getName(), e);
                        throw new FileIOException(ErrorCode.EXPORT_FAIL);
                    }
                }
            }
        }
    }

    private static <T> Cell createCellFromBean(Workbook workbook, T bean, Field field, Row row) throws IllegalAccessException {
        field.setAccessible(true);
        ExcelCell anno = field.getAnnotation(ExcelCell.class);
        int columnIndex = anno.columnIndex();
        Cell cell = row.createCell(columnIndex);

        Class<?> fieldType = field.getType();
        String cellFormat = anno.cellFormat();

        if (StringUtils.isNotEmpty(anno.cellFormula())) {
            setCellFormula(cell, anno);
        } else if (fieldType.equals(String.class)) {
            cell.setCellValue((String) field.get(bean));
        } else if (Number.class.isAssignableFrom(fieldType)) {
            Number value = (Number) field.get(bean);
            if (value != null) {
                cell.setCellValue(value.doubleValue());
            } else {
                cell.setBlank();
            }
        } else if (fieldType.equals(LocalDateTime.class)) {
            LocalDateTime value = (LocalDateTime) field.get(bean);
            if (anno.datePattern() != DateUtils.DatePattern.NONE && !ObjectUtils.isEmpty(value)) {
                cell.setCellValue(anno.datePattern().getFormatter().format(value));
            } else {
                cell.setCellValue(value);
            }
            if (StringUtils.isEmpty(cellFormat)) {
                cellFormat = "yyyy-MM-dd HH:mm:ss";
            }
        } else if (fieldType.equals(LocalDate.class)) {
            LocalDate value = (LocalDate) field.get(bean);
            if (anno.datePattern() != DateUtils.DatePattern.NONE && !ObjectUtils.isEmpty(value)) {
                cell.setCellValue(anno.datePattern().getFormatter().format(value));
            } else {
                cell.setCellValue(value);
            }
            if (StringUtils.isEmpty(cellFormat)) {
                cellFormat = "yyyy-MM-dd";
            }
        } else if (fieldType.equals(Date.class)) {
            if (anno.datePattern() != DateUtils.DatePattern.NONE) {
                log.warn("目前datePattern不支援Date欄位，不執行格式化為字串處理，如須設定日期格式請用cellFormat");
            }
            cell.setCellValue((Date) field.get(bean));
            if (StringUtils.isEmpty(cellFormat)) {
                cellFormat = "yyyy-MM-dd HH:mm:ss";
            }
        } else {
            log.error("不支援的欄位形態 fieldType:{}", fieldType.getTypeName());
            throw new FileIOException(ErrorCode.EXPORT_FAIL);
        }

        if (StringUtils.isNotEmpty(cellFormat)) {
            CellStyle cellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat(cellFormat));
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }

    private static void setCellFormula(Cell cell, ExcelCell anno) {
        Sheet sheet = cell.getSheet();
        Workbook workbook = sheet.getWorkbook();
        int startRow = Math.max(anno.cellFormulaRowBase(), 0);
        FormulaShifter rowCopyShifter = FormulaShifter.createForRowCopy(workbook.getSheetIndex(sheet), sheet.getSheetName()
                , startRow, startRow, cell.getRowIndex() - startRow, workbook.getSpreadsheetVersion());
        Ptg[] ptgList = FormulaParser.parse(anno.cellFormula(), getFormulaParsingWorkbook(workbook), FormulaType.CELL, workbook.getSheetIndex(sheet));
        rowCopyShifter.adjustFormula(ptgList, workbook.getSheetIndex(sheet));
        FormulaRenderingWorkbook formulaRenderingWorkbook = ExcelUtils.getFormulaRenderingWorkbook(workbook);
        String formula = FormulaRenderer.toFormulaString(formulaRenderingWorkbook, ptgList);
        cell.setCellFormula(formula);
    }

    public static <T> Row createAggregationRow(Sheet sheet, int firstRow, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        CellStyle cellStyle = getAggregationCellStyle(sheet);

        int lastRowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRowNum);
        for (Field field : fields) {
            field.setAccessible(true);
            ExcelCell anno = field.getAnnotation(ExcelCell.class);
            if (anno == null) {
                continue;
            }
            if (anno.aggregationType() == ExcelAggregationType.NONE) {
                continue;
            }
            int columnIndex = anno.columnIndex();

            Cell cell = row.createCell(columnIndex);
            String columnLetter = CellReference.convertNumToColString(columnIndex);

            String formula;
            switch (anno.aggregationType()) {
                case SUM -> formula = String.format("SUM(%s%d:%s%d)", columnLetter, firstRow + 1, columnLetter, lastRowNum);
                case COUNT -> formula = String.format("COUNT(%s%d:%s%d)", columnLetter, firstRow + 1, columnLetter, lastRowNum);
                case AVERAGE -> formula = String.format("AVERAGE(%s%d:%s%d)", columnLetter, firstRow + 1, columnLetter, lastRowNum);
                case MAX -> formula = String.format("MAX(%s%d:%s%d)", columnLetter, firstRow + 1, columnLetter, lastRowNum);
                case MIN -> formula = String.format("MIN(%s%d:%s%d)", columnLetter, firstRow + 1, columnLetter, lastRowNum);
                default -> {
                    log.error("不支援的彙總類型: {}", anno.aggregationType());
                    throw new FileIOException(ErrorCode.EXPORT_FAIL);
                }
            }
            cell.setCellFormula(formula);
            cell.setCellStyle(cellStyle);
        }
        return row;
    }

    public static <T> void adjustColumnWidths(Sheet sheet, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelCell.class)) {
                ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
                int columnIndex = excelCell.columnIndex();
                //配置欄位寬度
                if (excelCell.columnWidth() > 0) {
                    sheet.setColumnWidth(columnIndex, excelCell.columnWidth());
                } else {
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }

    public static <T> void cellReplaces(Sheet sheet, T bean) throws IllegalAccessException {
        for (Row row : sheet) {
            for (Cell cell : row) {
                String value = cell.getStringCellValue();
                Field[] fields = bean.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    ExcelCell anno = field.getAnnotation(ExcelCell.class);
                    Object object =  field.get(bean);
                    if (value.contains(field.getName())) {
                        if (ObjectUtils.isEmpty(object)) {
                            cell.setCellValue(value.replace(field.getName(), ""));
                            continue;
                        }

                        if (field.getType().equals(LocalDate.class)) {
                            LocalDate localDate = (LocalDate) object;
                            String feildValue;
                            if (ObjectUtils.isNotEmpty(anno) && anno.datePattern() != DateUtils.DatePattern.NONE && !ObjectUtils.isEmpty(value)) {
                                feildValue = anno.datePattern().getFormatter().format(localDate);
                            } else {
                                //預設
                                feildValue = localDate.format(YYYY_MM_DD_DASH.getFormatter());
                            }
                            cell.setCellValue(value.replace(field.getName(), feildValue));
                            continue;
                        }

                        if (field.getType().equals(LocalDateTime.class)) {
                            LocalDateTime localDateTime = (LocalDateTime) object;
                            String feildValue;
                            if (ObjectUtils.isNotEmpty(anno) && anno.datePattern() != DateUtils.DatePattern.NONE && !ObjectUtils.isEmpty(value)) {
                                feildValue = anno.datePattern().getFormatter().format(localDateTime);
                            } else {
                                //預設
                                feildValue = localDateTime.format(YYYY_MM_DD_HH_MM_SS_DASH.getFormatter());
                            }
                            cell.setCellValue(value.replace(field.getName(), feildValue));
                            continue;
                        }

                        String feildValue = ObjectUtils.isEmpty(object) ? "" : String.valueOf(object) ;
                        cell.setCellValue(value.replace(field.getName(), feildValue));
                    }
                }
            }
        }

    }

    private static CellStyle getAggregationCellStyle(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle aggregationCellStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true); // 設定字型為粗體
        aggregationCellStyle.setFont(boldFont);
        aggregationCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex()); // 設定背景色
        aggregationCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        aggregationCellStyle.setBorderTop(BorderStyle.THIN); // 設定邊框
        aggregationCellStyle.setBorderBottom(BorderStyle.THIN);
        aggregationCellStyle.setBorderLeft(BorderStyle.THIN);
        aggregationCellStyle.setBorderRight(BorderStyle.THIN);
        return aggregationCellStyle;
    }

    public static <T> T convertRowToBean(Row row, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T entity = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        Set<Integer> columnIndexSet = new HashSet<>();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(ExcelCell.class)) {
                continue;
            }

            field.setAccessible(true);
            ExcelCell anno = field.getAnnotation(ExcelCell.class);
            int columnIndex = anno.columnIndex();
            if (!columnIndexSet.add(columnIndex)) {
                log.error("convertRowToBean 重複的欄位: {}", columnIndex);
                throw new FileIOException(ErrorCode.EXPORT_FAIL);
            }

            Cell cell = row.getCell(columnIndex);
            try {
                field.set(entity, getCellValue(field.getType(), cell));
            } catch (IllegalArgumentException | IllegalAccessException | UnsupportedOperationException e) {
                log.error("解析資料失敗 field：{} cell：{}", field.getName(), new CellReference(cell).formatAsString(), e);
                throw new FileIOException(ErrorCode.EXPORT_FAIL);
            }
        }

        return entity;
    }

    public static <T> T getCellValue(Class<T> fieldType, Cell cell) {
        if (fieldType.equals(String.class)) {
            return fieldType.cast(getString(cell));
        }
        if (Number.class.isAssignableFrom(fieldType)) {
            return getNumeric(fieldType, cell);
        }
        if (fieldType.equals(LocalDateTime.class)) {
            return fieldType.cast(getLocalDateTime(cell));
        }
        if (fieldType.equals(LocalDate.class)) {
            return fieldType.cast(getLocalDate(cell));
        }
        if (fieldType.equals(Date.class)) {
            return fieldType.cast(getDate(cell));
        }
        return null;
    }

    public static String getString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                Workbook workbook = cell.getRow().getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                yield switch (cellValue.getCellType()) {
                    case NUMERIC -> String.valueOf(cellValue.getNumberValue());
                    case STRING -> cellValue.getStringValue();
                    case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());
                    case FORMULA, ERROR ->
                            throw new UnsupportedOperationException("不支援" +  cellValue.getCellType() + "轉數值型別");
                    default -> null;
                };
            }
            case ERROR -> throw new UnsupportedOperationException("不支援" +  cell.getCellType() + "轉數值型別");
            default -> null;
        };
    }

    private static <T> T getNumeric(Class<T> fieldType, Cell cell) {
        if (cell == null) {
            return null;
        }

        double value = 0;
        switch (cell.getCellType()) {
            case BLANK, _NONE -> {
                return null;
            }
            case NUMERIC -> value = cell.getNumericCellValue();
            case STRING -> value = Double.parseDouble(cell.getStringCellValue());
            case BOOLEAN -> value = cell.getBooleanCellValue() ? 1d : 0d;
            case FORMULA -> {
                Workbook workbook = cell.getRow().getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case NUMERIC -> value = cellValue.getNumberValue();
                    case STRING -> value = Double.parseDouble(cellValue.getStringValue());
                    case BOOLEAN -> value = cellValue.getBooleanValue() ? 1d : 0d;
                    case FORMULA, ERROR ->
                            throw new UnsupportedOperationException("不支援" +  cellValue.getCellType() + "轉數值型別");
                }
            }
        }

        if (fieldType.equals(AtomicInteger.class)) {
            return fieldType.cast(new AtomicInteger((int) value));
        } else if (fieldType.equals(AtomicLong.class)) {
            return fieldType.cast(new AtomicLong((long) value));
        } else if (fieldType.equals(Byte.class)) {
            return fieldType.cast((byte) value);
        } else if (fieldType.equals(Integer.class)) {
            return fieldType.cast((int) value);
        } else if (fieldType.equals(Long.class)) {
            return fieldType.cast((long) value);
        } else if (fieldType.equals(Float.class)) {
            return fieldType.cast((float) value);
        } else if (fieldType.equals(Double.class)) {
            return fieldType.cast(value);
        } else if (fieldType.equals(BigDecimal.class)) {
            return fieldType.cast(BigDecimal.valueOf(value));
        } else if (fieldType.equals(BigInteger.class)) {
            return fieldType.cast(BigInteger.valueOf((long) value));
        }

        throw new UnsupportedOperationException("不支援的數值型別：" + fieldType.getTypeName());
    }

    public static Date getDate(Cell cell) {
        LocalDateTime localDateTime = getLocalDateTime(cell);
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public static LocalDate getLocalDate(Cell cell) {
        LocalDateTime localDateTime = getLocalDateTime(cell);
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime getLocalDateTime(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case BLANK, _NONE -> null;
            case STRING -> DateUtils.parseToLocalDateTime(cell.getStringCellValue());
            case NUMERIC -> cell.getLocalDateTimeCellValue();
            default -> throw new UnsupportedOperationException("cell形態錯誤，無法轉成LocalDateTime：" + cell);
        };
    }

    public static Color getColorFromHex(Workbook workbook, String colorHex) {
        java.awt.Color color = java.awt.Color.decode(colorHex);

        if (workbook instanceof XSSFWorkbook) {
            return new XSSFColor(color, null);
        } else if (workbook instanceof HSSFWorkbook hssfWorkbook) {
            return hssfWorkbook.getCustomPalette()
                    .findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
        } else {
            throw new FileIOException(ErrorCode.EXPORT_FORMAT);
        }
    }

    public static void writeToResponse(HttpServletResponse response, String fileName, Workbook workbook) throws IOException {
        String extension;
        if (workbook instanceof XSSFWorkbook) {
            extension = ExcelType.XLSX.getFullExtension();
        } else if (workbook instanceof HSSFWorkbook) {
            extension = ExcelType.XLS.getFullExtension();
        } else {
            throw new FileIOException(ErrorCode.EXPORT_FORMAT);
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName + extension, StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
