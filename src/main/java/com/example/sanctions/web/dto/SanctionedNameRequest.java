package com.example.sanctions.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SanctionedNameRequest(
        @NotBlank( message = "fullName must not be blank" ) String fullName ) {
}
