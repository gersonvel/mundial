package com.quiniela.mundial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.service.UsuarioService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @PutMapping("/usuarios/{username}/activar")
    public ResponseEntity<ResponseDTO> alternarActivo(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.alternarEstadoUsuario(username));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        // Le pedimos al servicio que traiga absolutamente a todos los usuarios de la BD
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @PutMapping("/usuarios/{username}/reset-password")
    public ResponseEntity<ResponseDTO> resetearPassword(
            @PathVariable String username,
            @jakarta.validation.Valid @RequestBody com.quiniela.mundial.dto.PasswordResetRequestDTO request) {

        return ResponseEntity.ok(usuarioService.resetearPasswordPorAdmin(username, request));
    }
}
