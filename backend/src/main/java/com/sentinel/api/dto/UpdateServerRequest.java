package com.sentinel.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateServerRequest(
        @NotBlank String name,
        @NotBlank String host,
        @NotBlank String environment
) {}