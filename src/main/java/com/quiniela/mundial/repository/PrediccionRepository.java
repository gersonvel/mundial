package com.quiniela.mundial.repository;

import com.quiniela.mundial.model.Prediccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PrediccionRepository extends JpaRepository<Prediccion, Long> {

    // Para evitar que un usuario duplique su apuesta en un mismo partido
    Optional<Prediccion> findByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId);

    // Para que el servicio encuentre a quiénes repartirles puntos cuando termine el
    // partido
    List<Prediccion> findByPartidoId(Long partidoId);

    List<Prediccion> findByUsuarioId(Long usuarioId);
}