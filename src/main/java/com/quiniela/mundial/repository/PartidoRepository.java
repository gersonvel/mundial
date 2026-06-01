package com.quiniela.mundial.repository;

import com.quiniela.mundial.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

    // Trae partidos PENDIENTES que arranquen después de la hora que le pasemos por
    // parámetro
    @Query("SELECT p FROM Partido p WHERE p.fechaHora > :ahora AND p.estado = 'PENDIENTE' ORDER BY p.fechaHora ASC")
    List<Partido> buscarPartidosActivos(@Param("ahora") LocalDateTime ahora);
}
