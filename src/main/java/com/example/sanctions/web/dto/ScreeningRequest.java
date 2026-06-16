package com.example.sanctions.web.dto;

import jakarta.validation.constraints.NotBlank;


public record ScreeningRequest(
        @NotBlank( message = "name must not be blank" ) String name ) {
}
