package com.quiniela.mundial.controller;

import com.quiniela.mundial.dto.ResponseDTO;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*") // Permite que React consuma el endpoint sin errores de CORS
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping
    public ResponseEntity<ResponseDTO> obtenerRankingGlobal() {
        try {
            List<Usuario> ranking = rankingService.obtenerRanking();

            // Retornamos tu estructura ResponseDTO de forma manual
            return ResponseEntity.ok(new ResponseDTO(
                    HttpStatus.OK.value(),
                    false,
                    "Ranking global recuperado exitosamente",
                    ranking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    true,
                    "Error al obtener el ranking: " + e.getMessage(),
                    null));
        }
    }
}