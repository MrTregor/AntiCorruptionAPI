package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель пользователя системы с расширенной информацией о сотруднике.
 * <p>
 * Содержит comprehensive информацию о личных и профессиональных данных.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username", "email", "employee_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(unique = true, nullable = false, length = 50)
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
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<AccessGroup> groups = new HashSet<>();

    /**
     * Табельный номер сотрудника.
     */
    @Column(name = "employee_id", unique = true, length = 50)
    private String employeeId;

    /**
     * Персональные данные.
     */
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    /**
     * Полное имя сотрудника.
     *
     * @return Строка с полным именем
     */
    public String getFullName() {
        return String.format("%s %s %s",
                lastName != null ? lastName : "",
                firstName != null ? firstName : "",
                middleName != null ? middleName : ""
        ).trim();
    }

    /**
     * Дата рождения.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Пол сотрудника.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    /**
     * Перечисление полов.
     */
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    /**
     * Фотография сотрудника.
     */
    @Lob
    @Column(name = "photo")
    private byte[] photo;

    /**
     * Паспортные данные.
     */
    @Column(name = "passport_series", length = 10)
    private String passportSeries;

    @Column(name = "passport_number", length = 20)
    private String passportNumber;

    /**
     * Контактная информация.
     */
    @Column(length = 500)
    private String address;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(unique = true, length = 100)
    private String email;

    /**
     * Информация о работе.
     */
    @Column(length = 100)
    private String position;

    @Column(length = 100)
    private String department;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "contract_type", length = 50)
    private String contractType;

    @Column(precision = 10, scale = 2)
    private Double salary;

    /**
     * Образование и квалификация.
     */
    @Column(length = 500)
    private String education;

    @Column(name = "work_experience", length = 1000)
    private String workExperience;

    @Column(length = 500)
    private String skills;

    /**
     * Личная информация.
     */
    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @Column(name = "number_of_children")
    private Integer numberOfChildren;

    @Column(name = "military_service_info", length = 500)
    private String militaryServiceInfo;

    /**
     * Служебные документы.
     */
    @Column(length = 20)
    private String inn;

    @Column(length = 20)
    private String snils;

    /**
     * Профессиональное развитие.
     */
    @Column(name = "qualification_upgrade", length = 1000)
    private String qualificationUpgrade;

    @Column(length = 500)
    private String awards;

    @Column(name = "disciplinary_actions", length = 1000)
    private String disciplinaryActions;

    @Column(name = "attestation_results", length = 1000)
    private String attestationResults;

    /**
     * Дополнительная информация.
     */
    @Column(name = "medical_exam_results", length = 500)
    private String medicalExamResults;

    @Column(name = "bank_details", length = 500)
    private String bankDetails;

    @Column(name = "emergency_contact", length = 200)
    private String emergencyContact;

    @Column(length = 1000)
    private String notes;

    /**
     * Статус сотрудника.
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isFired = false;

    /**
     * Дата создания записи.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления записи.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Переопределение метода toString для удобного вывода информации.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", position='" + position + '\'' +
                '}';
    }

    /**
     * Переопределение метода equals для корректного сравнения.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    /**
     * Переопределение метода hashCode.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}