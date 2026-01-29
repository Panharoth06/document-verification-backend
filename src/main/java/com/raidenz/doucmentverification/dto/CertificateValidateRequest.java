package com.raidenz.doucmentverification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CertificateValidateRequest(
        @JsonProperty("hash_value")
        String hashValue
) {
}
