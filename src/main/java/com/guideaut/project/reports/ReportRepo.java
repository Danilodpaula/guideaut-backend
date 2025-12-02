package com.guideaut.project.repo;

import com.guideaut.project.report.Report;
import com.guideaut.project.report.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReportRepo extends JpaRepository<Report, UUID> {
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);
    List<Report> findAllByOrderByCreatedAtDesc();
}