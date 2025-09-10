package com.hrms.util;

import com.hrms.entity.Department;

public class DepartmentUtils {

    public static boolean isDepartmentManager(Integer userId, Department department) {
        return department.getManagerId().equals(userId);
    }

    public static boolean isTechnicalLeadDepartment(Department department) {
        return department.getDepartmentName().contains("技術長");
    }

    public static boolean isGMDepartment(Department department) {
        return department.getDepartmentName().contains("總經理");
    }

    public static boolean isHRDepartment(Department department) {
        return department.getDepartmentName().contains("人資");
    }

    public static boolean isAdminDepartment(Department department) {
        return department.getDepartmentName().contains("管理員");
    }
}
