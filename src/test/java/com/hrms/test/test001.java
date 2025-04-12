package com.hrms.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class test001 {
    @Test
    public void test017(){
        LocalDateTime startDateTime = LocalDateTime.parse("2024-08-24T23:30:00");
        LocalDateTime endDateTime = LocalDateTime.parse("2024-08-25T08:00:00");
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        System.out.println(startDate.atStartOfDay());
        System.out.println(endDate.atStartOfDay());
        long dayCount = Duration.between(startDateTime, endDateTime).toDays();
        System.out.println(dayCount);

        Duration duration = Duration.between(startDateTime, endDateTime);
        double hours = duration.toHours() + (duration.toMinutesPart() / 60.0);
        System.out.println(hours);  // 输出：8.5
        long lastDayHours = Duration.between(startDateTime, endDateTime).toHours();
        System.out.println(lastDayHours);
    }

    @Test
    public void test01() throws IOException {
        String filePath = "src/calendar2025.json";
        ; // 請替換成實際的檔案路徑
        long timediff;
        String updatedJsonString;
        String filePath2 = "src/calendar.json";
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> calendarEntries = objectMapper.readValue(jsonString, new TypeReference<List<Map<String, String>>>() {
            });
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            // Insert "周次" for each entry
            for (Map<String, String> entry : calendarEntries) {
                LocalDate date = LocalDate.parse(entry.get("西元日期"), formatter);
                int weekOfYear = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                entry.put("周次", String.valueOf(weekOfYear));
            }
            updatedJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(calendarEntries);
            System.out.println(updatedJsonString);

            JsonElement jsonElement = JsonParser.parseString(updatedJsonString);
            FileWriter fileWriter = new FileWriter(filePath2);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonElement, fileWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void test02(){
//        ScheduledTasks st = new ScheduledTasks();
//        st.updateAttendanceRecords();
//    }
//
//    @Autowired
//    private ScheduledTasks scheduledTasks;
//    @Autowired
//    private AttendanceRecordsService attendanceRecordsService;
//
//
//    @Test
//    public void testInitLeaveSpecialRecordsScheduled() {
//        scheduledTasks.initLeaveSpecialRecordsScheduled();
//        // 验证逻辑，例如检查相关服务是否被正确调用
//    }
//
//    @Test
//    public void createAttendanceRecords() {
//        attendanceRecordsService.createAttendanceRecords(LocalDate.parse("2024-11-09"));
//    }
//
//    @Test
//    public void updateAttendanceRecords() {
//        attendanceRecordsService.calculateAttendanceRecords(LocalDate.parse("2024-11-09"), true);
//    }

}
