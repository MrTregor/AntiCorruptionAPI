package com.api.AntiCorruptionAPI.Repositories;

import com.api.AntiCorruptionAPI.Models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}