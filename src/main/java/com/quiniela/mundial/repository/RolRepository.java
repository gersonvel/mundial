package com.quiniela.mundial.repository;

import com.quiniela.mundial.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    // Método clave para buscar un rol por su nombre string
    Optional<Rol> findByNombre(String nombre);
}