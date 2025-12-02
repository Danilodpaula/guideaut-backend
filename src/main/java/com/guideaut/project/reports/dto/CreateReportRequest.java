package com.guideaut.project.report.dto;

import com.guideaut.project.report.ReportReason;
import com.guideaut.project.report.ReportType;
import java.util.UUID;

public record CreateReportRequest(
    UUID targetId,
    ReportType type,
    ReportReason reason,
    String description
) {}