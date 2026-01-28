package com.raidenz.doucmentverification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record CertificateResponse(
        @JsonProperty("owner_name")
        String ownerName,

        @JsonProperty("course_name")
        String courseName,

        @JsonProperty("offered_by")
        String offeredBy,

        @JsonProperty("covered_topics")
        Set<String> coveredTopics,

        @JsonProperty("issue_date")
        LocalDateTime issueDate,

        @JsonProperty("pdf_path")
        String pdfPath,

        String code
) {
}
