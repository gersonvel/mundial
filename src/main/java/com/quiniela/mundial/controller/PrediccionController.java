package com.quiniela.mundial.controller;

import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.dto.PrediccionRequestDTO;
import com.quiniela.mundial.model.Prediccion;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.UsuarioRepository;
import com.quiniela.mundial.service.PrediccionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predicciones")
@CrossOrigin(origins = "*")
public class PrediccionController {

    @Autowired
    private PrediccionService prediccionService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<ResponseDTO> registrarPrediccion(@RequestBody PrediccionRequestDTO request) {
        try {
            // MAGIA: Obtenemos el username directamente de la sesión protegida por JWT
            String usernameContext = SecurityContextHolder.getContext().getAuthentication().getName();

            // Buscamos el ID real del usuario a partir del username del token
            Usuario usuario = usuarioRepository.findByUsername(usernameContext)
                    .orElseThrow(() -> new RuntimeException("Usuario no válido en la sesión"));

            // Mandamos el ID real al servicio sin depender de headers tramposos
            Prediccion guardada = prediccionService.guardarPrediccion(usuario.getId(), request);

            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Predicción guardada exitosamente",
                    guardada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    true,
                    e.getMessage(),
                    null));
        }
    }

    @GetMapping("/mis-predicciones")
    public ResponseEntity<ResponseDTO> obtenerMisPredicciones() {
        try {
            // Obtenemos el username de la sesión segura del JWT
            String usernameContext = SecurityContextHolder.getContext().getAuthentication().getName();

            Usuario usuario = usuarioRepository.findByUsername(usernameContext)
                    .orElseThrow(() -> new RuntimeException("Usuario no válido en la sesión"));

            // Cambia 'obtenerPorUsuarioId' por el nombre exacto de tu método en el Service
            List<Prediccion> misPredicciones = prediccionService.obtenerPrediccionesPorUsuario(usuario.getId());

            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Tus pronósticos guardados han sido recuperados",
                    misPredicciones));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    true,
                    e.getMessage(),
                    null));
        }
    }
}