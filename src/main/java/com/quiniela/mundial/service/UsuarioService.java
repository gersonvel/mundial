package com.quiniela.mundial.service;

import java.util.List;

import com.quiniela.mundial.dto.PasswordResetRequestDTO;
import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.model.Usuario;

public interface UsuarioService {

    ResponseDTO alternarEstadoUsuario(String username);

    List<Usuario> obtenerTodos();

    ResponseDTO resetearPasswordPorAdmin(String username, PasswordResetRequestDTO request);

}
