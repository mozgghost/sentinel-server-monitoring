package com.sentinel.api.dto;

import jakarta.validation.constraints.*;

public record CreateAlertRuleRequest(
    @NotBlank
    @Size(max = 120)
    String name,

    @Size(max = 500)
    String description,

    @NotBlank
    @Pattern(regexp = "CPU|MEMORY")
    String metric,

    @NotBlank
    @Pattern(regexp = ">|>=")
    String operator,

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    double threshold,

    @NotBlank
    @Pattern(regexp = "WARNING|CRITICAL")
    String severity
) {
}