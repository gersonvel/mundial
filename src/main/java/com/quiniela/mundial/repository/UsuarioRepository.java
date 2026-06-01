package com.quiniela.mundial.repository;

import com.quiniela.mundial.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para el login o validaciones de registro
    Optional<Usuario> findByUsername(String username);

    // Para el Ranking global de la quiniela
    List<Usuario> findAllByOrderByPuntosTotalesDesc();
}