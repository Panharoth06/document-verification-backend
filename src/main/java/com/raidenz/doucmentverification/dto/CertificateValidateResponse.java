package com.raidenz.doucmentverification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CertificateValidateResponse(
        @JsonProperty("is_match")
        boolean isMatch,

        @JsonProperty("certificate")
        CertificateResponse certificate
) {
}
