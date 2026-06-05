package com.quiniela.mundial.controller;

import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.dto.ResultadoPartidoRequestDTO;
import com.quiniela.mundial.model.Partido;
import com.quiniela.mundial.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @GetMapping
    public ResponseEntity<ResponseDTO> obtenerTodosLosPartidos() {
        try {
            List<Partido> todos = partidoService.obtenerTodos();
            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Todos los partidos recuperados",
                    todos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    true,
                    e.getMessage(),
                    null));
        }
    }

    // Endpoint para que los Usuarios vean los partidos activos
    @GetMapping("/activos")
    public ResponseEntity<ResponseDTO> obtenerActivos() {
        try {
            List<Partido> activos = partidoService.obtenerPartidosActivos();
            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Lista de partidos activos recuperada",
                    activos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    true,
                    e.getMessage(),
                    null));
        }
    }

    // Endpoint para que el Admin registre el marcador final
    @PutMapping("/{id}/resultado")
    public ResponseEntity<ResponseDTO> finalizarPartido(
            @PathVariable Long id,
            @RequestBody ResultadoPartidoRequestDTO request) {
        try {
            Partido partidoProcesado = partidoService.registrarResultado(
                    id,
                    request.getGolesLocal(),
                    request.getGolesVisitante(),
                    request.getGanadorPenales());

            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Partido finalizado y puntos repartidos correctamente",
                    partidoProcesado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    true,
                    e.getMessage(),
                    null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> crearPartido(@RequestBody Partido nuevoPartido) {
        try {
            Partido partidoGuardado = partidoService.guardarPartido(nuevoPartido);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    HttpStatus.CREATED.value(),
                    false,
                    "Partido creado exitosamente",
                    partidoGuardado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    true,
                    "Error al crear el partido: " + e.getMessage(),
                    null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> eliminarPartido(@PathVariable Long id) {
        try {
            partidoService.eliminarPorId(id);
            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Partido eliminado correctamente de la base de datos",
                    null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    true,
                    "No se pudo eliminar el partido: " + e.getMessage(),
                    null));
        }
    }
}