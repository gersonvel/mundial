package com.quiniela.mundial.dto;

import java.util.Set;

public record AuthResponseDTO(
        String username,
        String email,
        String fullName,
        String message,
        String jwt,
        boolean status,
        boolean activo,
        Set<String> roles) {
}
