package com.raidenz.doucmentverification.exception.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;
}
