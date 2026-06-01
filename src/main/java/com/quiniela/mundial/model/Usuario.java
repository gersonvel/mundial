package com.quiniela.mundial.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore // Evita que la contraseña se envíe en los JSON de respuesta (como en el
                // Ranking)
    private String password;

    @Column(name = "puntos_totales", nullable = false)
    private int puntosTotales = 0;

    // Relación uno a muchos hacia la tabla intermedia
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UsuarioRol> usuarioRoles = new ArrayList<>();
}