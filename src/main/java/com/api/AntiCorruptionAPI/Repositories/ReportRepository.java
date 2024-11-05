package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByAssignedTo(Long assignedTo);
}