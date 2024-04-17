package org.utl.dsm.MongoBlog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.utl.dsm.MongoBlog.entity.Publicacion;
import org.utl.dsm.MongoBlog.entity.Usuario;

import java.util.List;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, Integer> {
    Usuario findByEmail(String email);
    Usuario findByUsuario(String usuario);
    @Query(value = "{ 'publicaciones.tags': { $exists: true, $not: { $size: 0 } } }", count = true)
    long countTotalTags();

    @Query(value = "{ 'publicaciones.comentarios': { $exists: true, $not: { $size: 0 } } }", count = true)
    long countTotalComentarios();

    @Query(value = "{ 'publicaciones.reacciones': { $elemMatch: { tipo: ?0 } } }", count = true)
    long countTotalReaccionesPorTipo(int tipo);
}