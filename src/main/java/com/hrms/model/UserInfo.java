package com.hrms.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class UserInfo implements Serializable {
    private Integer id;
    private String account;
    private String fullName;
    private String nickName;
    private String position;
    private String gender;
    private LocalDate birthday;
    private String phone;
    private String email;
    private String skype;
    private String telegram;
    private LocalDate entryDate;
    private Integer departmentParent;
    private Integer departmentId;
    private String departmentName;
    private String description;
    private String managerNickName;
    private Integer roleId;
    private String emergencyContact;
    private String address;
    private String relationship;
    private String emergencyContactPhone;
    private String idNumber;
    private String employeeNumber;
    private String highestEducationLevel;
    private String emergencyContactAddress;
    private String registeredAddress;
    private Integer overtimeType;
    private Byte company;
}

