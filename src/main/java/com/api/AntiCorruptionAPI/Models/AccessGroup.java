package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель представляет группу доступа в системе.
 *
 * Используется для группировки пользователей и определения их прав.
 */
@Getter
@Setter
@Entity
@Table(name = "access_groups")
public class AccessGroup {

    /**
     * Уникальный идентификатор группы доступа.
     * Автоматически генерируется базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное название группы доступа.
     * Не может быть пустым.
     */
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    // Геттеры и сеттеры
}