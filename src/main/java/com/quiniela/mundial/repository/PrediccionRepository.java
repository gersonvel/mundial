package com.quiniela.mundial.repository;

import com.quiniela.mundial.dto.RankingDesempateDTO;
import com.quiniela.mundial.model.Prediccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrediccionRepository extends JpaRepository<Prediccion, Long> {

    // Para evitar que un usuario duplique su apuesta en un mismo partido
    Optional<Prediccion> findByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId);

    List<Prediccion> findByPartidoId(Long partidoId);

    List<Prediccion> findByUsuarioId(Long usuarioId);

    @Query("SELECT new com.quiniela.mundial.dto.RankingDesempateDTO(" +
            "p.usuario.username, " +
            "SUM(p.puntosGanados), " +
            "COUNT(CASE WHEN p.puntosGanados = 5 THEN 1 END), " +
            "COUNT(CASE WHEN p.puntosGanados = 4 THEN 1 END), " +
            "COUNT(CASE WHEN p.puntosGanados = 3 THEN 1 END), " +
            "COUNT(CASE WHEN p.puntosGanados = 2 THEN 1 END), " +
            "COUNT(CASE WHEN p.puntosGanados = 1 THEN 1 END)) " +
            "FROM Prediccion p " +
            "GROUP BY p.usuario.username")
    List<RankingDesempateDTO> obtenerEstadisticasDesempateGlobal();
}