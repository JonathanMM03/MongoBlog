package org.utl.dsm.MongoBlog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utl.dsm.MongoBlog.entity.EstadisticasDTO;
import org.utl.dsm.MongoBlog.entity.Publicacion;
import org.utl.dsm.MongoBlog.entity.Reaccion;
import org.utl.dsm.MongoBlog.entity.Usuario;
import org.utl.dsm.MongoBlog.repository.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("/estadisticas")
@Tag(name = "Estadisticas", description = "Operaciones relacionadas con estadísticas")
public class EstadisticasController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/contar")
    @Operation(summary = "Obtener estadísticas de tags, comentarios y reacciones por tipo")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas() {
        List<Usuario> usuarioList = usuarioRepository.findAll();
        EstadisticasDTO estadisticas = new EstadisticasDTO();

        // Contar total de tags, comentarios y reacciones por tipo
        long totalTags = contarTotalTags(usuarioList);
        long totalComentarios = contarTotalComentarios(usuarioList);
        long totalReaccionesMeEncanta = contarTotalReaccionesPorTipo(usuarioList, Reaccion.ME_ENCANTA);
        long totalReaccionesMeGusta = contarTotalReaccionesPorTipo(usuarioList, Reaccion.ME_GUSTA);

        // Asignar los valores a las propiedades del DTO
        estadisticas.setTotalTags(totalTags);
        estadisticas.setTotalComentarios(totalComentarios);
        estadisticas.setTotalReaccionesMeEncanta(totalReaccionesMeEncanta);
        estadisticas.setTotalReaccionesMeGusta(totalReaccionesMeGusta);

        return ResponseEntity.ok(estadisticas);
    }

    private long contarTotalTags(List<Usuario> usuarioList) {
        long totalTags = 0;
        for (Usuario usuario : usuarioList) {
            for (Publicacion publicacion : usuario.getPublicaciones()) {
                totalTags += publicacion.getTags().size();
            }
        }
        return totalTags;
    }

    private long contarTotalComentarios(List<Usuario> usuarioList) {
        long totalComentarios = 0;
        for (Usuario usuario : usuarioList) {
            for (Publicacion publicacion : usuario.getPublicaciones()) {
                totalComentarios += publicacion.getComentarios().size();
            }
        }
        return totalComentarios;
    }

    private long contarTotalReaccionesPorTipo(List<Usuario> usuarioList, Reaccion tipoReaccion) {
        long totalReacciones = 0;
        for (Usuario usuario : usuarioList) {
            for (Publicacion publicacion : usuario.getPublicaciones()) {
                totalReacciones += publicacion.getReacciones().stream()
                        .filter(reaccion -> reaccion.equals(tipoReaccion))
                        .count();
            }
        }
        return totalReacciones;
    }
}
