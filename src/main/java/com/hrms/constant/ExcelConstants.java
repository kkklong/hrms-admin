package com.hrms.constant;

public class ExcelConstants {

    public static final String ATTENDANCE_SUMMARY_SALARY_TOTAL = "ROUND(AK1-((AK1/30/8/60)*F1)-((AK1/30/8/60)*G1)-((AK1/30/8/60)*H1)+K1-(((AK1/30/8)*M1)/2)-(((AK1/30/8)*N1))+V1+W1+(((AK1/30/8)*AA1))-AB1-AC1-AF1,0)-AH1";

    public static final String ATTENDANCE_SUMMARY_OVERTIME_CONVERTED_TO_CASH = "ROUND((((AK1/30/8)*(X1*1.34)))+(((AK1/30/8)*(Y1*1.67))),0)";

    public static final String ATTENDANCE_SUMMARY_VOLUNTARY_PENSION = "AE1*AK1/100";

    public static final String BONUS_TOTAL = "(P1*Q1)+(R1*S1)+(T1*U1)";
}
