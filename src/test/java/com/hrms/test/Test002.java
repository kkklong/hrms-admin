package com.hrms.test;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Test002 {

    @Test
    public void test001(){
        System.out.println("lastDayHours: ");
    }


    @Test
    public void RemotePeriodTest(){
        Date today = new Date();
//"09:00"18:00 23:00  08:00 14:00 23:00
        String dateS = "2025-09-16";

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDate.parse(dateS);
        LocalTime start = LocalTime.parse("09:00");
        LocalTime end = LocalTime.parse("18:00");


        LocalDateTime startDT = date.atTime(start).minusMinutes(30);
        LocalDateTime endDT = start.isAfter(end)
                ? date.plusDays(1).atTime(end).plusHours(1)
                : date.atTime(end).plusHours(1);
        Boolean inShiftPd = !now.isBefore(startDT) && !now.isAfter(endDT);

        System.out.println("startDT: " + startDT);
        System.out.println("endDT: " + endDT);

        System.out.println("inShiftPd: " + inShiftPd);


        LocalDateTime startDT2 = date.atTime(start).minusMinutes(30);
        LocalDateTime endDT2 = start.isAfter(end)
                ? date.plusDays(1).atTime(end.plusHours(1))
                : date.atTime(end.plusHours(1));

        System.out.println("startDT2: " + startDT2);
        System.out.println("endDT2: " + endDT2);
    }

    @Test
    public void test003(){
        String reportDate = "2025-05";
        System.out.println(LocalDate.parse(reportDate + "-01"
                , DateTimeFormatter.ISO_DATE));
        LocalDate startDate = LocalDate.parse("2025-09-18");
        LocalDate periodStartDate = startDate.with(DayOfWeek.MONDAY);
        System.out.println(periodStartDate);
    }
}
