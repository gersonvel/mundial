package com.quiniela.mundial.service;

import com.quiniela.mundial.model.Partido;
import com.quiniela.mundial.model.Prediccion;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.PartidoRepository;
import com.quiniela.mundial.repository.PrediccionRepository;
import com.quiniela.mundial.repository.UsuarioRepository;
import com.quiniela.mundial.service.PartidoService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
// @RequiredArgsConstructor
public class PartidoServiceImpl implements PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private PrediccionRepository prediccionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // private final PartidoRepository partidoRepository;
    // private final PrediccionRepository prediccionRepository;
    // private final UsuarioRepository usuarioRepository;

    @Override
    public List<Partido> obtenerPartidosActivos() {
        // Trae los partidos pendientes cuya fecha es mayor a la hora actual
        return partidoRepository.buscarPartidosActivos(LocalDateTime.now());
    }

    @Override
    @Transactional // Asegura que si algo falla, no se guarden puntos a medias
    public Partido registrarResultado(Long partidoId, int golesLocal, int golesVisitante, String ganadorPenales) {
        // 1. Buscar el partido
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        if ("JUGADO".equals(partido.getEstado())) {
            throw new RuntimeException("Este partido ya fue procesado anteriormente");
        }

        // 2. Actualizar resultado real, guardar desempate si aplica y cambiar estado
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setGanadorPenales(ganadorPenales); // Guardamos quién ganó en penales en el partido
        partido.setEstado("JUGADO");
        partidoRepository.save(partido);

        // 3. Buscar todas las predicciones de este partido para calcular puntos
        List<Prediccion> predicciones = prediccionRepository.findByPartidoId(partidoId);

        for (Prediccion pred : predicciones) {
            // CORREGIDO: Ahora pasamos los 6 parámetros usando los datos del partido
            int puntosGanados = calcularPuntos(
                    golesLocal,
                    golesVisitante,
                    pred.getGolesLocalPred(),
                    pred.getGolesVisitantePred(),
                    partido.getFase(), // <- Pasamos la fase (ej: "OCTAVOS", "JORNADA_1")
                    ganadorPenales // <- Pasamos quién ganó en penales ("LOCAL" o "VISITANTE")
            );

            // Guardar puntos en la predicción
            pred.setPuntosGanados(puntosGanados);
            prediccionRepository.save(pred);

            // Sumar puntos al total del usuario
            Usuario usuario = pred.getUsuario();
            usuario.setPuntosTotales(usuario.getPuntosTotales() + puntosGanados);
            usuarioRepository.save(usuario);
        }

        return partido;
    }

    private int calcularPuntos(int gLocalReal, int gVisitReal, int gLocalPred, int gVisitPred, String fase,
            String ganadorPenalesReal) {

        // 🌟 LOGS DE AUDITORÍA: Verás en la consola de Spring Boot qué datos entran
        // exactamente
        System.out.println("\n [QUINIELA] === CALCULANDO PUNTOS ===");
        System.out.println("Fase del partido: " + fase);
        System.out.println("Marcador REAL:    " + gLocalReal + " - " + gVisitReal);
        System.out.println("Marcador PREDICHO: " + gLocalPred + " - " + gVisitPred);

        // REGLA 1: Marcador exacto -> 5 puntos (Atinó a ambos goles exactos)
        if (gLocalReal == gLocalPred && gVisitReal == gVisitPred) {
            System.out.println("RESULTADO: 5 PUNTOS (Marcador exacto)");
            return 5;
        }

        // Calculamos las tendencias reales
        boolean ganaLocalReal = gLocalReal > gVisitReal;
        boolean ganaVisitanteReal = gVisitReal > gLocalReal;
        boolean empateReal = gLocalReal == gVisitReal;

        // Calculamos las tendencias que predijo el usuario
        boolean ganaLocalPred = gLocalPred > gVisitPred;
        boolean ganaVisitantePred = gVisitPred > gLocalPred;
        boolean empatePred = gLocalPred == gVisitPred;

        boolean atinoGanador = false;

        // Mantenemos tu regla favorita con startsWith para Fase de Grupos
        if (fase != null && fase.startsWith("JORNADA")) {
            atinoGanador = (ganaLocalReal && ganaLocalPred) ||
                    (ganaVisitanteReal && ganaVisitantePred) ||
                    (empateReal && empatePred);
        }

        // Eliminación directa (Octavos, Cuartos, etc.)
        else {
            if (empateReal) {
                if (ganadorPenalesReal != null) {
                    if (ganaLocalPred && "LOCAL".equals(ganadorPenalesReal)) {
                        atinoGanador = true;
                    } else if (ganaVisitantePred && "VISITANTE".equals(ganadorPenalesReal)) {
                        atinoGanador = true;
                    } else if (empatePred) {
                        atinoGanador = false;
                    }
                }
            } else {
                atinoGanador = (ganaLocalReal && ganaLocalPred) || (ganaVisitanteReal && ganaVisitantePred);
            }
        }

        // ¿Le atinó a los goles individuales de algún equipo?
        boolean atinoGolesLocal = gLocalReal == gLocalPred;
        boolean atinoGolesVisitante = gVisitReal == gVisitPred;
        boolean atinoAlMenosUnEquipo = atinoGolesLocal || atinoGolesVisitante;

        // REGLA 2: Atinó al ganador Y a los goles de UN equipo -> 4 puntos
        if (atinoGanador && atinoAlMenosUnEquipo) {
            System.out.println("RESULTADO: 4 PUNTOS (Atinó Ganador + Goles de un equipo)");
            return 4;
        }

        // REGLA 3: Atinó únicamente al ganador (sin pegarle a los goles de nadie) -> 3
        // puntos
        if (atinoGanador) {
            System.out.println("RESULTADO: 3 PUNTOS (Atinó únicamente al Ganador)");
            return 3;
        }

        // REGLA 4: No atinó al ganador, pero sí a los goles de un equipo -> 1 punto
        if (atinoAlMenosUnEquipo) {
            System.out.println("RESULTADO: 1 PUNTO (Consolación: Goles de un equipo)");
            return 1;
        }

        System.out.println("RESULTADO: 0 PUNTOS");
        return 0;
    }

    @Override
    public Partido guardarPartido(Partido partido) {
        partido.setEstado("PENDIENTE");
        return partidoRepository.save(partido);
    }

    @Override
    public List<Partido> obtenerTodos() {
        return partidoRepository.findAll();
    }

    @Override
    public void eliminarPorId(Long partidoId) {

        partidoRepository.deleteById(partidoId);
    }
}