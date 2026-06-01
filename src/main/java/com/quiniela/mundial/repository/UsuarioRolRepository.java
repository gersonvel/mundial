package com.quiniela.mundial.repository;

import com.quiniela.mundial.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    // Por si en el futuro necesitas listar qué roles tiene asignados un usuario
    // específico
    List<UsuarioRol> findByUsuarioId(Long usuarioId);
}