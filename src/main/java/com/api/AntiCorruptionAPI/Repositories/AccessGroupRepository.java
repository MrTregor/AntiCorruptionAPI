package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
}