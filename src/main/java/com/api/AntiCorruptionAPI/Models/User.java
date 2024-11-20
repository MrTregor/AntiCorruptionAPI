package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

/**
 * Модель пользователя системы с расширенной информацией о сотруднике.
 * <p>
 * Содержит comprehensive информацию о личных и профессиональных данных.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Логин пользователя для входа в систему.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Хэшированный пароль пользователя.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Группы доступа, к которым принадлежит пользователь.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<AccessGroup> groups = new HashSet<>();

    /**
     * Табельный номер сотрудника.
     */
    private String employeeId;

    /**
     * Персональные данные.
     */
    private String lastName;

    private String firstName;

    private String middleName;



    /**
     * Дата рождения.
     */
    private LocalDate dateOfBirth;

    /**
     * Пол сотрудника.
     */
    private String gender;

    /**
     * Фотография сотрудника.
     */
    @Lob
    private byte[] photo;

    /**
     * Паспортные данные.
     */
    private String passportSeries;
    private String passportNumber;

    /**
     * Контактная информация.
     */
    private String address;
    private String phoneNumber;
    private String email;

    /**
     * Информация о работе.
     */
    private String position;
    private String department;
    private LocalDate hireDate;
    private String contractType;
    private Double salary;

    /**
     * Образование и квалификация.
     */
    private String education;
    private String workExperience;
    private String skills;

    /**
     * Личная информация.
     */
    private String maritalStatus;
    private Integer numberOfChildren;
    private String militaryServiceInfo;

    /**
     * Служебные документы.
     */
    private String inn;
    private String snils;

    /**
     * Профессиональное развитие.
     */
    private String qualificationUpgrade;
    private String awards;
    private String disciplinaryActions;
    private String attestationResults;

    /**
     * Дополнительная информация.
     */
    private String medicalExamResults;
    private String bankDetails;
    private String emergencyContact;
    private String notes;

    /**
     * Статус сотрудника.
     */
    @Column(nullable = false, columnDefinition = "boolean default false" )
    private Boolean isFired;          // Признак увольнения
}