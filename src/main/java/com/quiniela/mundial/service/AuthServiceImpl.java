package com.quiniela.mundial.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quiniela.mundial.dto.AuthRequestDTO;
import com.quiniela.mundial.dto.AuthResponseDTO;
import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.model.Rol;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.model.UsuarioRol;
import com.quiniela.mundial.repository.RolRepository;
import com.quiniela.mundial.repository.UsuarioRepository;
import com.quiniela.mundial.repository.UsuarioRolRepository;
import com.quiniela.mundial.security.JwtUtils;

import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        @Autowired
        private JwtUtils jwtUtils;
        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private UsuarioRepository userRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private RolRepository rolRepository;

        @Autowired
        private UsuarioRolRepository usuarioRolRepository;

        @Override
        public AuthResponseDTO login(AuthRequestDTO authRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(authRequest.username(),
                                                authRequest.password()));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String token = jwtUtils.generateToken(authentication);

                Set<String> roles = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

                Usuario userEntity = userRepository.findByUsername(authRequest.username())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return new AuthResponseDTO(
                                userEntity.getUsername(),
                                "",
                                "",
                                "Login exitoso",
                                token,
                                true,
                                roles);
        }

        @Override
        @Transactional
        public ResponseDTO registrarUsuario(Map<String, String> request) {
                String username = request.get("username");
                String password = request.get("password");

                // 1. Validar si el usuario ya existe
                if (userRepository.findByUsername(username).isPresent()) {
                        return new ResponseDTO(
                                        HttpStatus.BAD_REQUEST.value(),
                                        true,
                                        "El nombre de usuario ya está en uso",
                                        null);
                }

                // 2. Crear el nuevo usuario y ENCRIPTAR la contraseña
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setUsername(username);
                nuevoUsuario.setPassword(passwordEncoder.encode(password));
                nuevoUsuario.setPuntosTotales(0);

                // Guardamos el usuario base
                Usuario usuarioGuardado = userRepository.save(nuevoUsuario);

                // 3. Buscar el rol por defecto (ROLE_USER)
                Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                                .orElseThrow(() -> new RuntimeException(
                                                "Error: El rol ROLE_USER no existe en la base de datos."));

                // 4. CREAR Y GUARDAR LA RELACIÓN EN LA TABLA INTERMEDIA (CORREGIDO)
                UsuarioRol usuarioRol = new UsuarioRol(usuarioGuardado, rolUser);

                // PASO CLAVE: Guardamos explícitamente en la tabla intermedia
                // 'usuarios_roles'
                usuarioRolRepository.save(usuarioRol);

                // Sincronizamos la lista en memoria del objeto usuario (Buenas prácticas de
                // JPA)
                usuarioGuardado.getUsuarioRoles().add(usuarioRol);

                return new ResponseDTO(
                                HttpStatus.CREATED.value(),
                                false,
                                "Usuario registrado exitosamente",
                                null);
        }
}
