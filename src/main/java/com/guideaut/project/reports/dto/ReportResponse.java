package com.guideaut.project.report.dto;

import com.guideaut.project.report.ReportReason;
import com.guideaut.project.report.ReportStatus;
import com.guideaut.project.report.ReportType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ReportResponse(
    UUID id,
    UUID targetId,
    String targetName,
    ReportType type,       
    String reporterName,
    ReportReason reason,
    String description,
    ReportStatus status,
    OffsetDateTime createdAt
) {}