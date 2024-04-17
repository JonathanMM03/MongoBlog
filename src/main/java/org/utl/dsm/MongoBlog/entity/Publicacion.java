package org.utl.dsm.MongoBlog.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Publicacion {
    @Id
    private int id;
    private List<Comentario> comentarios = new ArrayList<>();
    private List<Reacciones> reacciones = new ArrayList<>();
    private List<Tags> tags = new ArrayList<>();
    @NotNull
    private String contenido;
    private String fechaPublicacion;
    private String fechaModificacion;
    private String estatus;
}