package com.quiniela.mundial.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository userRepository;

    @Override
    @Transactional
    public ResponseDTO alternarEstadoUsuario(String username) {
        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Invierte el valor actual (si es true pasa a false, si es false pasa a true)
        usuario.setActivo(!usuario.isActivo());
        userRepository.save(usuario);

        String mensaje = usuario.isActivo() ? "Usuario activado con éxito" : "Usuario desactivado con éxito";

        return new ResponseDTO(HttpStatus.OK.value(), false, mensaje, null);
    }

    @Override
    public List<Usuario> obtenerTodos() {

        return userRepository.findAll();
    }
}
