package org.utl.dsm.MongoBlog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasDTO {
    private long totalTags;
    private long totalComentarios;
    private long totalReaccionesMeEncanta;
    private long totalReaccionesMeGusta;
}