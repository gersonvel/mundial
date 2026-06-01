package com.quiniela.mundial.service;

import com.quiniela.mundial.model.Partido;
import com.quiniela.mundial.model.Prediccion;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.PartidoRepository;
import com.quiniela.mundial.repository.PrediccionRepository;
import com.quiniela.mundial.repository.UsuarioRepository;
import com.quiniela.mundial.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartidoServiceImpl implements PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private PrediccionRepository prediccionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

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
        partido.setGanadorPenales(ganadorPenales); // 🌟 Guardamos quién ganó en penales en el partido
        partido.setEstado("JUGADO");
        partidoRepository.save(partido);

        // 3. Buscar todas las predicciones de este partido para calcular puntos
        List<Prediccion> predicciones = prediccionRepository.findByPartidoId(partidoId);

        for (Prediccion pred : predicciones) {
            // 🌟 CORREGIDO: Ahora pasamos los 6 parámetros usando los datos del partido
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
        // 🌟 REGLA 1: Marcador exacto -> 5 puntos (Atinó a ambos goles)
        if (gLocalReal == gLocalPred && gVisitReal == gVisitPred) {
            return 5;
        }

        // Calculamos las tendencias reales en el tiempo regular/extra (90 o 120 min)
        boolean ganaLocalReal = gLocalReal > gVisitReal;
        boolean ganaVisitanteReal = gVisitReal > gLocalReal;
        boolean empateReal = gLocalReal == gVisitReal;

        // Calculamos las tendencias que predijo el usuario
        boolean ganaLocalPred = gLocalPred > gVisitPred;
        boolean ganaVisitantePred = gVisitPred > gLocalPred;
        boolean empatePred = gLocalPred == gVisitPred;

        // Determinar quién es el ganador real definitivo
        boolean atinoGanador = false;

        // Si la fase empieza con "JORNADA", es Fase de Grupos (reglas estándar)
        if (fase != null && fase.startsWith("JORNADA")) {
            atinoGanador = (ganaLocalReal && ganaLocalPred) ||
                    (ganaVisitanteReal && ganaVisitantePred) ||
                    (empateReal && empatePred);
        }
        // Si NO es fase de grupos, estamos en Eliminación Directa (Octavos, Cuartos,
        // etc.)
        else {
            if (empateReal) {
                // El partido real quedó en empate y se decidió por PENALES.
                if (ganadorPenalesReal != null) {
                    // Si el usuario predijo que ganaba el Local en los 90 min, y el Local pasó en
                    // penales: ACERTÓ
                    if (ganaLocalPred && ganadorPenalesReal.equals("LOCAL")) {
                        atinoGanador = true;
                    }
                    // Si el usuario predijo que ganaba el Visitante en los 90 min, y el Visitante
                    // pasó en penales: ACERTÓ
                    else if (ganaVisitantePred && ganadorPenalesReal.equals("VISITANTE")) {
                        atinoGanador = true;
                    }
                    // 🌟 CAMBIO 1: Si el usuario predijo un EMPATE en los 90 min (como tu 1-1),
                    // ya NO le damos la tendencia por buena de forma automática, porque no eligió
                    // quién pasaba.
                    else if (empatePred) {
                        atinoGanador = false; // 👈 Cambiado de true a false
                    }
                }
            } else {
                // Si en eliminación directa un equipo ganó en los 90/120 minutos normales, la
                // regla vuelve a ser la estándar
                atinoGanador = (ganaLocalReal && ganaLocalPred) || (ganaVisitanteReal && ganaVisitantePred);
            }
        }

        // ¿Le atinó a los goles individuales de algún equipo?
        boolean atinoGolesLocal = gLocalReal == gLocalPred;
        boolean atinoGolesVisitante = gVisitReal == gVisitPred;

        // 🌟 CAMBIO 2 (Opcional): Si quieres que poner empate y que quede empate dé 0
        // puntos absolutos
        // cuando no le pegas a los goles (como tu 1-1 vs 2-2), lo dejamos así.
        // Si en el futuro quisieras dar 1 punto de consolación por "atinar al empate
        // general",
        // podrías agregar: || (empateReal && empatePred)
        boolean atinoAlMenosUnEquipo = atinoGolesLocal || atinoGolesVisitante;

        // REGLA 2: Atinó al ganador Y a los goles de UN equipo -> 4 puntos
        if (atinoGanador && atinoAlMenosUnEquipo) {
            return 4;
        }

        // REGLA 3: Atinó únicamente al ganador -> 3 puntos
        if (atinoGanador) {
            return 3;
        }

        // REGLA 4: No atinó al ganador, pero sí a los goles de un equipo -> 1 punto
        if (atinoAlMenosUnEquipo) {
            return 1;
        }

        // No le pegó a absolutamente nada -> 0 puntos
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