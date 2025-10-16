package com.hrms.util;

import com.hrms.enums.ExcelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class ExcelUtilsTest {

    /**
     * 如須確認生成的檔案可改成false
     */
    static boolean deleteTestFile = false;

    @Test
    public void xlsxTest() throws Exception {
        excelTest(ExcelType.XLSX);
    }

    @Test
    public void xlsTest() throws Exception {
        excelTest(ExcelType.XLS);
    }

    private void excelTest(ExcelType excelType) throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Path tempDirectoryPath = Paths.get("tmp");
        if (Files.notExists(tempDirectoryPath)) {
            try {
                Files.createDirectories(tempDirectoryPath);
            } catch (IOException e) {
                log.error("無法建立 tmp 路徑：{}", tempDirectoryPath.toAbsolutePath(), e);
                return;
            }
        }
        try (Workbook workbook = ExcelUtils.createWorkBook(excelType)) {

            // 移除微秒避免excel轉換精準度問題
            LocalDateTime date = LocalDateTime.now();
            int millis = date.getNano() / 1_000_000;
            date = date.withNano(millis * 1_000_000);
            date = date.withDayOfMonth(8);
            date = date.withMonth(7);

            excelWriteTest(workbook, date);
            Path path = tempDirectoryPath.resolve("temp" + excelType.getFullExtension());
            File file = path.toFile();
            if (file.exists()) {
                if (!file.delete()) {
                    log.warn("略過測試！無法刪除測試檔案:{}", file.getAbsolutePath());
                    return;
                }
            }
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
                log.info("Excel 檔案已成功寫入至：{}", path.toAbsolutePath());
            } catch (IOException e) {
                log.error("寫入 Excel 檔案失敗", e);
            }

            excelReadTest(file, date);
            if (deleteTestFile && !file.delete()) {
                log.warn("無法刪除測試檔案:{}", file.getAbsolutePath());
            }
        }
    }

    void excelWriteTest(Workbook workbook, LocalDateTime date) {
        Sheet sheet = workbook.createSheet("sheet");
        Class<TestBean> clazz = TestBean.class;
        List<TestBean> beans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TestBean bean = new TestBean();
            bean.setName("test" + i);
            bean.setSum(i);
            bean.setCount(new AtomicLong(i));
            bean.setAverage((float) i);
            bean.setMin(BigDecimal.valueOf(i));
            bean.setMax((double) i);
            bean.setLocalDate(date.toLocalDate().plusDays(i));
            bean.setLocalDateTime(date.plusMonths(i));
            bean.setDate(Date.from(date.minusDays(i).atZone(ZoneId.systemDefault()).toInstant()));
            beans.add(bean);
        }
        ExcelUtils.createTitle(workbook, sheet, clazz);
        ExcelUtils.createRowFromBean(sheet, clazz, beans);
        ExcelUtils.createAggregationRow(sheet, 1, clazz);
        ExcelUtils.adjustColumnWidths(sheet, clazz);
    }

    void excelReadTest(File file, LocalDateTime date) throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<TestBean> beanFromFile = ExcelUtils.getBeanFromFile(file, TestBean.class);
        TestBean first = beanFromFile.getFirst();
        assertEquals("test0", first.getName());
        assertEquals(0, first.getSum());
        assertEquals(0f, first.getAverage());
        assertEquals(BigDecimal.valueOf(0d), first.getMin());
        assertEquals(0d, first.getMax());

        TestBean last = beanFromFile.getLast();

        int sum = 0;
        long count = 0;
        float average = 0f;
        BigDecimal min = BigDecimal.valueOf(0d);
        double max = 0;
        int formula = 0;

        try (Workbook workbook = ExcelUtils.getWorkBook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals(6, sheet.getLastRowNum());

            for (int i = 0; i < beanFromFile.size() - 1; i++) {
                TestBean bean = beanFromFile.get(i);
                sum += bean.getSum();
                count++;

                min = min.min(bean.getMin());
                max = Math.max(max, bean.getMax());
                formula = formula + (100 + (i * 2) + 1);

                assertEquals(bean.getName(), "test" + i);
                assertEquals(bean.getLocalDate(), date.toLocalDate().plusDays(i));
                assertEquals(bean.getLocalDateTime(), date.plusMonths(i));
                assertEquals(bean.getDate(), Date.from(date.minusDays(i).atZone(ZoneId.systemDefault()).toInstant()));
                assertEquals(bean.getFormula(), 100 + (i * 2) + 1);

                checkFormula(sheet.getRow(i + 1), String.format("B%d+C%d+C$3+100", i + 2, i + 2));
            }
        }

        if (count > 0) {
            average = (float) sum / count;
        }

        assertEquals(sum, last.getSum());
        assertEquals(average, last.getAverage());
        assertEquals(min, last.getMin());
        assertEquals(max, last.getMax());
        assertEquals(formula, last.getFormula());
    }

    private void checkFormula(Row row, String formula) {
        Cell cell = row.getCell(9);
        assertEquals(cell.getCellFormula(), formula);
    }

    @Test
    public void dateTimeTest() {
        File file = new File(Objects.requireNonNull(getClass().getResource("/loadAttendanceRecordSample.xlsx")).getPath());
        log.info("測試檔案：{}", file.getAbsoluteFile());
        try (Workbook workBook = ExcelUtils.getWorkBook(file)) {
            Sheet sheet = workBook.getSheetAt(0);
            LocalDate date = LocalDate.of(2024, 10, 1);
            LocalDateTime startTime = date.atTime(9, 0, 0);
            LocalDateTime endTime = date.atTime(18, 0, 0);
            for (int i=1; i<=sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String account = row.getCell(0).getStringCellValue();
                LocalDate attendanceDate = ExcelUtils.getLocalDate(row.getCell(1));
                LocalDateTime clockInTime = ExcelUtils.getLocalDateTime(row.getCell(2));
                LocalDateTime clockOutTime = ExcelUtils.getLocalDateTime(row.getCell(3));

                assertEquals("test" + i, account);
                assertEquals(attendanceDate, date);
                assertEquals(clockInTime, startTime);
                assertEquals(clockOutTime, endTime);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}