package com.quiniela.mundial.service;

import com.quiniela.mundial.dto.DetallePrediccionesPartidosDTO;
import com.quiniela.mundial.dto.PrediccionRequestDTO;
import com.quiniela.mundial.model.Partido;
import com.quiniela.mundial.model.Prediccion;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.PartidoRepository;
import com.quiniela.mundial.repository.PrediccionRepository;
import com.quiniela.mundial.repository.UsuarioRepository;
import com.quiniela.mundial.service.PrediccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrediccionServiceImpl implements PrediccionService {

    @Autowired
    private PrediccionRepository prediccionRepository;
    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Prediccion guardarPrediccion(Long usuarioId, PrediccionRequestDTO request) {
        // 1. Validar si el partido existe
        Partido partido = partidoRepository.findById(request.getPartidoId())
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        // 2. REGLA CLAVE: Validar si el partido ya empezó o se jugó
        if (LocalDateTime.now().isAfter(partido.getFechaHora()) || "JUGADO".equals(partido.getEstado())) {
            throw new RuntimeException("No puedes apostar, el partido ya ha comenzado o finalizado.");
        }

        // 3. Buscar el usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 4. Si ya existe una predicción de este usuario para este partido, la
        // actualizamos. Si no, creamos una nueva.
        Prediccion prediccion = prediccionRepository.findByUsuarioIdAndPartidoId(usuarioId, partido.getId())
                .orElse(new Prediccion());

        prediccion.setUsuario(usuario);
        prediccion.setPartido(partido);
        prediccion.setGolesLocalPred(request.getGolesLocalPred());
        prediccion.setGolesVisitantePred(request.getGolesVisitantePred());

        return prediccionRepository.save(prediccion);
    }

    @Override
    public List<Prediccion> obtenerPrediccionesPorUsuario(Long usuarioId) {
        return prediccionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<DetallePrediccionesPartidosDTO> obtenerPronosticosPorPartido(Long partidoId) {
        List<Prediccion> predicciones = prediccionRepository.findByPartidoId(partidoId);

        return predicciones.stream()
                .map(p -> new DetallePrediccionesPartidosDTO(
                        p.getUsuario().getUsername(),
                        p.getGolesLocalPred(),
                        p.getGolesVisitantePred(),
                        p.getPuntosGanados() != null ? p.getPuntosGanados() : 0))
                .collect(Collectors.toList());
    }
}