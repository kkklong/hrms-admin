package com.hrms.constant;

public class VerificationRegexp {

	/** ====================================帳號相關==============================================**/

    public static final String REGEXP_EMAIL = "^$|^[_a-zA-Z0-9-]+([.][_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+([.][a-zA-Z0-9-]+)*$";

    public static final String REGEXP_MOBILE_PHONE = "^$|0\\d{9}";

    public static final String REGEXP_SKYPE = "^$|^[a-zA-Z][a-zA-Z0-9._-]{5,31}$";

    public static final String REGEXP_TELEGRAM = "^$|^[a-zA-Z][a-zA-Z0-9_]{4,31}$";

    /** ====================================日期相關==============================================**/

    public static final String DATE_YYYY_MM = "^\\d{4}-(0[1-9]|1[0-2])$";
}
