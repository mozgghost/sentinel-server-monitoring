package com.sentinel.api.dto;
import jakarta.validation.constraints.NotBlank;
public record CreateServerRequest(@NotBlank String name,@NotBlank String host,@NotBlank String environment) {}
