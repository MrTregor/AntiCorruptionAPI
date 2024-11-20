package com.api.AntiCorruptionAPI.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель представляет группу доступа в системе.
 *
 * Используется для группировки пользователей и определения их прав.
 */
@Entity
@Table(name = "access_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    /**
     * Описание группы доступа.
     * Опционально, может быть пустым.
     */
    @Column(length = 500)
    private String description;

    /**
     * Список разрешений, связанных с данной группой.
     * Используется для определения прав доступа.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "group_permissions",
            joinColumns = @JoinColumn(name = "group_id")
    )
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    /**
     * Список пользователей, принадлежащих к этой группе.
     * Отношение многие-ко-многим с пользователями.
     */
    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    /**
     * Добавление разрешения в группу.
     *
     * @param permission разрешение для добавления
     */
    public void addPermission(String permission) {
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
        }
        this.permissions.add(permission);
    }

    /**
     * Удаление разрешения из группы.
     *
     * @param permission разрешение для удаления
     */
    public void removePermission(String permission) {
        if (this.permissions != null) {
            this.permissions.remove(permission);
        }
    }

    /**
     * Проверка наличия разрешения в группе.
     *
     * @param permission разрешение для проверки
     * @return true, если разрешение есть в группе
     */
    public boolean hasPermission(String permission) {
        return this.permissions != null && this.permissions.contains(permission);
    }

    /**
     * Переопределение метода toString для удобного вывода информации о группе.
     *
     * @return строковое представление группы
     */
    @Override
    public String toString() {
        return "AccessGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }

    /**
     * Переопределение метода equals для корректного сравнения групп.
     *
     * @param o объект для сравнения
     * @return true, если группы эквивалентны
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessGroup that = (AccessGroup) o;
        return id != null && id.equals(that.id);
    }

    /**
     * Переопределение метода hashCode для корректной работы в коллекциях.
     *
     * @return хеш-код группы
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}