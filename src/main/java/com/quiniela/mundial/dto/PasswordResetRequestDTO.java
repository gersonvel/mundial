package com.quiniela.mundial.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDTO(
        @NotBlank(message = "La nueva contraseña es obligatoria") String nuevaPassword) {
}