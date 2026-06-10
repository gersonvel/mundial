package com.quiniela.mundial.controller;

import com.quiniela.mundial.dto.AuthRequestDTO;
import com.quiniela.mundial.dto.AuthResponseDTO;
import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            return ResponseEntity.ok(authService.login(authRequest));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<ResponseDTO> registrarUsuario(@RequestBody Map<String, String> request) {
        try {

            ResponseDTO response = authService.registrarUsuario(request);

            if (response.getError()) {
                return ResponseEntity.status(response.getStatus()).body(response);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    true,
                    "Error en el registro: " + e.getMessage(),
                    null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> obtenerUsuarioActual(@RequestHeader("Authorization") String token) {
        // Quitamos la palabra "Bearer " del token si viene incluida
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // Le pedimos al servicio que busque al usuario dueño de ese token y devuelva
        // sus datos frescos
        return ResponseEntity.ok(authService.obtenerUsuarioPorToken(jwtToken));
    }
}