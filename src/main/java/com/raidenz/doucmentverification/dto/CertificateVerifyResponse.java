package com.raidenz.doucmentverification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CertificateVerifyResponse(
        @JsonProperty("is_valid")
        boolean isValid
) {
}
