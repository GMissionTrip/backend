package com.gangchu.gangchutrip.journey.dto;

import com.gangchu.gangchutrip.journey.entity.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {
    private String postId;
    private String reason;
    private String description;

    public Report.ReportReason getReasonEnum() {
        return Report.ReportReason.valueOf(reason.toUpperCase());
    }
}

