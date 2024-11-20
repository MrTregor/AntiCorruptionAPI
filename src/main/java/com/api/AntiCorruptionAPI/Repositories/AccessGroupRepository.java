package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
    Optional<AccessGroup> findByName(String name);
    Set<AccessGroup> findByIdIn(Set<Long> ids);
}