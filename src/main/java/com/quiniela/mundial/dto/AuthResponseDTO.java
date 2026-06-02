package com.quiniela.mundial.dto;

import java.util.Set;

public record AuthResponseDTO(
                String username,
                String email,
                String fullName,
                String message,
                String jwt,
                boolean status,
                Set<String> roles) {
}
