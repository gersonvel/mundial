package com.quiniela.mundial.controller;

import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.model.Rol;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.model.UsuarioRol;
import com.quiniela.mundial.repository.RolRepository;
import com.quiniela.mundial.repository.UsuarioRepository;
import com.quiniela.mundial.repository.UsuarioRolRepository;
import com.quiniela.mundial.security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtils jwtUtil; // Ajustar a JwtUtil si corresponde

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            // Valida las credenciales contra la BD
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
            String token = jwtUtil.generarToken(username);

            // Datos estructurados para el Frontend
            Map<String, Object> dataData = new HashMap<>();
            dataData.put("token", token);
            dataData.put("username", usuario.getUsername());
            dataData.put("roles", usuario.getUsuarioRoles().stream()
                    .map(ur -> ur.getRol().getNombre())
                    .toList());

            return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), false, "Login exitoso", dataData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), true, "Credenciales inválidas", null));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<ResponseDTO> registrarUsuario(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            // 1. Validar si el usuario ya existe
            if (usuarioRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseDTO(
                        HttpStatus.BAD_REQUEST.value(), true, "El nombre de usuario ya está en uso", null));
            }

            // 2. Crear el nuevo usuario y ENCRIPTAR la contraseña con BCrypt
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            nuevoUsuario.setPuntosTotales(0);

            // Guardamos inicialmente para generar el ID del usuario
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            // 3. Buscar el rol por defecto (ROLE_USER)
            Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: El rol ROLE_USER no existe en la base de datos."));

            // 4. Crear la relación en la tabla intermedia y asociarla
            UsuarioRol usuarioRol = new UsuarioRol(usuarioGuardado, rolUser);
            usuarioGuardado.getUsuarioRoles().add(usuarioRol);

            // Guardamos definitivamente el usuario con su rol asociado
            usuarioRepository.save(usuarioGuardado);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    HttpStatus.CREATED.value(),
                    false,
                    "Usuario registrado exitosamente",
                    null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    true,
                    "Error en el registro: " + e.getMessage(),
                    null));
        }
    }
}