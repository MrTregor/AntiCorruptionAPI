package com.api.AntiCorruptionAPI.Requests;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserUpdateRequest {
    private String username;
    private String password;
    private String employeeId;
    private String lastName;
    private String firstName;
    private String middleName;
    private Date dateOfBirth;
    private String gender;
    private byte[] photo;
    private String passportSeries;
    private String passportNumber;
    private String address;
    private String phoneNumber;
    private String email;
    private String position;
    private String department;
    private Date hireDate;
    private String contractType;
    private Double salary;
    private String education;
    private String workExperience;
    private String skills;
    private String maritalStatus;
    private Integer numberOfChildren;
    private String militaryServiceInfo;
    private String inn;
    private String snils;
    private String qualificationUpgrade;
    private String awards;
    private String disciplinaryActions;
    private String attestationResults;
    private String medicalExamResults;
    private String bankDetails;
    private String emergencyContact;
    private String notes;
    private Boolean isFired;
}