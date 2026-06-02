package com.quiniela.mundial.service;

import java.util.Map;

import com.quiniela.mundial.dto.AuthRequestDTO;
import com.quiniela.mundial.dto.AuthResponseDTO;
import com.quiniela.mundial.dto.ResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO authRequest);

    ResponseDTO registrarUsuario(Map<String, String> request);
}
