package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    // Уникальный идентификатор пользователя в системе
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Логин пользователя для входа в систему
    @Column(unique = true, nullable = false)
    private String username;

    // Хэшированный пароль пользователя
    @Column(nullable = false)
    private String password;

    // Группы доступа, к которым принадлежит пользователь
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<AccessGroup> groups = new HashSet<>();

    // Табельный номер сотрудника
    private String employeeId;

    // ФИО сотрудника
    private String lastName;
    private String firstName;
    private String middleName;

    // Персональные данные
    private Date dateOfBirth;     // Дата рождения
    private String gender;        // Пол

    // Фотография сотрудника
    @Lob
    private byte[] photo;

    // Паспортные данные
    private String passportSeries;    // Серия паспорта
    private String passportNumber;    // Номер паспорта

    // Контактная информация
    private String address;           // Адрес проживания
    private String phoneNumber;       // Контактный телефон
    private String email;             // Электронная почта

    // Информация о работе
    private String position;          // Должность
    private String department;        // Отдел/подразделение
    private Date hireDate;           // Дата приема на работу
    private String contractType;      // Тип трудового договора
    private Double salary;            // Размер заработной платы

    // Образование и квалификация
    private String education;         // Образование (уровень, учебное заведение, специальность)
    private String workExperience;    // Опыт работы
    private String skills;            // Профессиональные навыки и квалификации

    // Личная информация
    private String maritalStatus;     // Семейное положение
    private Integer numberOfChildren; // Количество детей
    private String militaryServiceInfo; // Данные о воинском учете

    // Документы и идентификаторы
    private String inn;               // ИНН
    private String snils;             // СНИЛС

    // Профессиональное развитие
    private String qualificationUpgrade;   // Сведения о повышении квалификации
    private String awards;                 // Награды и поощрения
    private String disciplinaryActions;    // Дисциплинарные взыскания
    private String attestationResults;     // Результаты аттестаций

    // Дополнительная информация
    private String medicalExamResults;     // Результаты медосмотров
    private String bankDetails;            // Банковские реквизиты
    private String emergencyContact;       // Контакты для экстренной связи
    private String notes;                  // Примечания

    // Статус сотрудника
    private Boolean isFired;          // Признак увольнения
}