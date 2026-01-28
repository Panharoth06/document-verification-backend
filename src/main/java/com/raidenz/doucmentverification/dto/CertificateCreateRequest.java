package com.raidenz.doucmentverification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CertificateCreateRequest(
        @JsonProperty("owner_name")
        @NotEmpty(message = "Owner name is required")
        String ownerName,

        @JsonProperty("course_name")
        @NotEmpty(message = "Course name is required")
        String courseName,

        @JsonProperty("offered_by")
        @NotEmpty(message = "Offered by is required")
        String offeredBy,

        @JsonProperty("covered_topics")
        @NotEmpty(message = "Cover topics are required")
        Set<String> coveredTopics
) {
}
