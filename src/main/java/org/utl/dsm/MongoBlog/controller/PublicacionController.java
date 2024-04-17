package org.utl.dsm.MongoBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.utl.dsm.MongoBlog.entity.*;
import org.utl.dsm.MongoBlog.repository.PublicacionRepository;
import org.utl.dsm.MongoBlog.repository.UsuarioRepository;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/publicaciones")
@Tag(name = "Publicación", description = "Operaciones relacionadas con publicaciones")
public class PublicacionController {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/getAllByUsuario/{usuario}/all")
    @Operation(summary = "Obtener todas las publicaciones de un usuario por email o nombre de usuario", description = "Este endpoint devuelve todas las publicaciones de un usuario específico, identificado ya sea por su email o su nombre de usuario.")
    public List<Publicacion> getAllByUsuarioPublicaciones(@PathVariable String usuario) {
        // Buscar al usuario por su email o nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);
        if (user == null) {
            throw new RuntimeException("No se encontró ningún usuario con el email o nombre de usuario proporcionado: " + usuario);
        }
        // Retornar las publicaciones del usuario encontrado
        return user.getPublicaciones().isEmpty() ? Collections.emptyList() : user.getPublicaciones();
    }

    @GetMapping("/findByPublish/{usuario}/{id}")
    @Operation(summary = "Obtener publicación con el usuario y número de publicación", description = "Este endpoint devuelve la publicación de un usuario específico por su usuario y el número de publicación.")
    public ResponseEntity<?> getAllByUsuarioPublicaciones(@PathVariable String usuario, @PathVariable int id) {
        // Buscar al usuario por su email o nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún usuario con el email o nombre de usuario proporcionado: " + usuario);
        }

        // Obtener la lista de publicaciones del usuario
        List<Publicacion> publicaciones = user.getPublicaciones();

        // Buscar la publicación por su ID
        Publicacion publicacion = publicaciones.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);

        // Verificar si se encontró la publicación
        if (publicacion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se encontró ninguna publicación con el ID proporcionado para el usuario: " + usuario);
        }

        // Retornar la publicación solicitada
        return ResponseEntity.ok(publicacion);
    }

    @GetMapping("getAllByEmail/{email}/all")
    @Operation(summary = "Obtener todas las publicaciones de un usuario por email o nombre de usuario", description = "Este endpoint devuelve todas las publicaciones de un usuario específico, identificado ya sea por su email o su nombre de usuario.")
    public List<Publicacion> getAllByEmailPublicaciones(@PathVariable String email) {
        // Buscar al usuario por su email o nombre de usuario
        Usuario user = usuarioRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No se encontró ningún usuario con el email o nombre de usuario proporcionado: " + email);
        }
        // Retornar las publicaciones del usuario encontrado
        return user.getPublicaciones();
    }

    @GetMapping("/all")
    @Operation(summary = "Obtener todas las publicaciones de todos los usuarios")
    public List<Publicacion> getAllPublicaciones() {
        List<Publicacion> todasLasPublicaciones = new ArrayList<>();
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            todasLasPublicaciones.addAll(usuario.getPublicaciones());
        }
        return todasLasPublicaciones;
    }


    @PostMapping("/{usuario}/postear")
    @Operation(summary = "Crear una nueva publicación para un usuario por email o nombre de usuario", description = "Este endpoint permite crear una nueva publicación para un usuario específico, identificado por su usuario.")
    public ResponseEntity<?> crearPublicacion(@PathVariable String usuario,@RequestBody Publicacion publicacion) {
        // Buscar al usuario por su email o nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);

        // Verificar si se encontró al usuario
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
                {"mensaje":"No se encontró ningún usuario con el email o nombre de usuario proporcionado: %s"}
                """, usuario));
        }

        // Obtener el último ID de la lista de publicaciones del usuario
        int lastId = user.getPublicaciones().isEmpty() ? 0 : user.getPublicaciones().get(user.getPublicaciones().size() - 1).getId();

        // Asignar el nuevo ID a la publicación
        publicacion.setId(lastId + 1);
        // Establecer la fecha de publicación
        publicacion.setFechaPublicacion(Instant.now().toString());
        publicacion.setEstatus("Activo");

        // Agregar la nueva publicación a la lista de publicaciones del usuario
        user.getPublicaciones().add(publicacion);

        // Actualizar solo la lista de publicaciones del usuario en la base de datos
        usuarioRepository.save(user);

        return ResponseEntity.ok(publicacion);
    }
    @PostMapping("/comentar/{usuario}/{idPublicacion}")
    @Operation(summary = "Comentar una publicación", description = "Este endpoint permite agregar un comentario a una publicación.")
    public ResponseEntity<?> comentarPublicacion(@PathVariable String usuario,
                                                 @PathVariable int idPublicacion,
                                                 @RequestBody Comentario comentario) {
        // Buscar al usuario por su nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);

        // Verificar si se encontró al usuario
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
        {"mensaje":"No se encontró ningún usuario con el nombre de usuario proporcionado: %s"}
        """, usuario));
        }

        // Buscar la publicación por su ID
        Optional<Publicacion> publicacionOptional = user.getPublicaciones().stream()
                .filter(publicacion -> publicacion.getId() == idPublicacion)
                .findFirst();

        if (publicacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
        {"mensaje":"No se encontró ninguna publicación con el ID proporcionado: %d"}
        """, idPublicacion));
        }

        // Obtener la publicación
        Publicacion publicacion = publicacionOptional.get();

        // Obtener el último ID de los comentarios
        int lastId = publicacion.getComentarios().isEmpty() ? 0 : publicacion.getComentarios().size();

        // Establecer el ID del comentario
        comentario.setId(lastId);
        // Establecer el usuario que hizo el comentario
        comentario.setUsuario(usuario);
        // Establecer la fecha del comentario
        comentario.setFecha(Instant.now().toString());

        // Agregar el comentario a la publicación
        publicacion.getComentarios().add(comentario);

        // Guardar la persona actualizada en la base de datos
        usuarioRepository.save(user);

        return ResponseEntity.ok(publicacion);
    }

    @PutMapping("/comentar/{usuario}/{idPublicacion}/{idComentario}")
    @Operation(summary = "Actualizar un comentario de una publicación", description = "Este endpoint permite actualizar un comentario específico de una publicación.")
    public ResponseEntity<?> actualizarComentario(@PathVariable String usuario,
                                                  @PathVariable int idPublicacion,
                                                  @PathVariable int idComentario,
                                                  @RequestBody Comentario comentarioActualizado) {
        // Buscar al usuario por su nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);

        // Verificar si se encontró al usuario
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
                {"mensaje":"No se encontró ningún usuario con el nombre de usuario proporcionado: %s"}
                """, usuario));
        }

        // Buscar la publicación por su ID
        Optional<Publicacion> publicacionOptional = user.getPublicaciones().stream()
                .filter(publicacion -> publicacion.getId() == idPublicacion)
                .findFirst();

        if (publicacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
                {"mensaje":"No se encontró ninguna publicación con el ID proporcionado: %d"}
                """, idPublicacion));
        }

        // Obtener la publicación
        Publicacion publicacion = publicacionOptional.get();

        // Buscar el comentario por su ID
        Optional<Comentario> comentarioOptional = publicacion.getComentarios().stream()
                .filter(comentario -> comentario.getId() == idComentario)
                .findFirst();

        if (comentarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
                {"mensaje":"No se encontró ningún comentario con el ID proporcionado: %d"}
                """, idComentario));
        }

        // Actualizar el contenido del comentario
        Comentario comentarioExistente = comentarioOptional.get();
        comentarioExistente.setContenido(comentarioActualizado.getContenido());

        // Guardar la publicación actualizada en la base de datos
        usuarioRepository.save(user);

        return ResponseEntity.ok(publicacion);
    }

    @PostMapping("/reaccionar/{user}/{publicacionId}/{tipo}")
    @Operation(summary = "Esta operación permite reaccionar a una publicación")
    public ResponseEntity<?> reaccionarPublicacion(@PathVariable String user,
                                                   @PathVariable int tipo,
                                                   @PathVariable int publicacionId) {
        // Buscar al usuario por su email o nombre de usuario
        Usuario usuario = usuarioRepository.findByUsuario(user);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("""
                    {"mensaje": "No se encontró ningún usuario con el email o nombre de usuario proporcionado: %s"}
                    """, user));
        }

        // Obtener la lista de publicaciones del usuario
        List<Publicacion> publicaciones = usuario.getPublicaciones();

        // Buscar la publicación por su ID
        Optional<Publicacion> publicacionOptional = publicaciones.stream()
                .filter(p -> p.getId() == publicacionId)
                .findFirst();

        // Verificar si se encontró la publicación
        if (publicacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("""
                    {"mensaje": "No se encontró ninguna publicación con el ID proporcionado para el usuario: %s"}
                    """, user));
        }

        Publicacion publicacion = publicacionOptional.get();

        // Verificar si el usuario ya ha reaccionado a esta publicación y si la reacción es diferente
        Optional<Reaccion> reaccionExistenteOptional = publicacion.getReacciones().stream()
                .filter(reaccion -> reaccion.getUsuario().equals(user))
                .map(Reacciones::getReaccion)
                .findFirst();

        if (reaccionExistenteOptional.isPresent()) {
            Reaccion reaccionExistente = reaccionExistenteOptional.get();
            Reaccion nuevaReaccion = tipo == 1 ? Reaccion.ME_ENCANTA : tipo == 2 ? Reaccion.ME_GUSTA : null;

            if (nuevaReaccion != null && reaccionExistente != nuevaReaccion) {
                // Actualizar la reacción existente por la nueva reacción
                publicacion.getReacciones().removeIf(reaccion -> reaccion.getUsuario().equals(user));
                publicacion.getReacciones().add(new Reacciones(nuevaReaccion, user));
            } else {
                // Anular la reacción existente
                publicacion.getReacciones().removeIf(reaccion -> reaccion.getUsuario().equals(user));
            }
        } else {
            // Agregar la nueva reacción
            Reaccion nuevaReaccion = tipo == 1 ? Reaccion.ME_ENCANTA : tipo == 2 ? Reaccion.ME_GUSTA : null;
            if (nuevaReaccion != null) {
                publicacion.getReacciones().add(new Reacciones(nuevaReaccion, user));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"mensaje\": \"Reacción no existente\"}");
            }
        }

        // Guardar la publicación actualizada en la base de datos
        usuarioRepository.save(usuario);

        // Retornar la publicación solicitada
        return ResponseEntity.ok(publicacion);
    }
    @DeleteMapping("/comentar/{usuario}/{idPublicacion}/{idComentario}")
    @Operation(summary = "Eliminar un comentario de una publicación", description = "Este endpoint permite eliminar un comentario específico de una publicación.")
    public ResponseEntity<?> eliminarComentario(@PathVariable String usuario,
                                                @PathVariable int idPublicacion,
                                                @PathVariable int idComentario) {
        // Buscar al usuario por su nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);

        // Verificar si se encontró al usuario
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
        {"mensaje":"No se encontró ningún usuario con el nombre de usuario proporcionado: %s"}
        """, usuario));
        }

        // Buscar la publicación por su ID
        Optional<Publicacion> publicacionOptional = user.getPublicaciones().stream()
                .filter(publicacion -> publicacion.getId() == idPublicacion)
                .findFirst();

        if (publicacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
        {"mensaje":"No se encontró ninguna publicación con el ID proporcionado: %d"}
        """, idPublicacion));
        }

        // Obtener la publicación
        Publicacion publicacion = publicacionOptional.get();

        // Buscar el comentario por su ID
        Optional<Comentario> comentarioOptional = publicacion.getComentarios().stream()
                .filter(comentario -> comentario.getId() == idComentario)
                .findFirst();

        if (comentarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
        {"mensaje":"No se encontró ningún comentario con el ID proporcionado: %d"}
        """, idComentario));
        }

        // Eliminar el comentario de la publicación
        Comentario comentario = comentarioOptional.get();
        publicacion.getComentarios().remove(comentario);

        // Guardar la publicación actualizada en la base de datos
        usuarioRepository.save(user);

        return ResponseEntity.ok(publicacion);
    }
    @DeleteMapping("/{usuario}/{id}")
    @Operation(summary = "Eliminar una publicación específica de un usuario", description = "Este endpoint permite eliminar una publicación específica de un usuario identificado por su nombre de usuario.")
    public ResponseEntity<?> eliminarPublicacion(@PathVariable String usuario, @PathVariable int id) {
        // Buscar al usuario por su nombre de usuario
        Usuario user = usuarioRepository.findByUsuario(usuario);

        // Verificar si se encontró al usuario
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
                    {"mensaje":"No se encontró ningún usuario con el nombre de usuario proporcionado: %s"}
                    """, usuario));
        }

        // Verificar si el usuario tiene la publicación con el ID especificado
        Optional<Publicacion> publicacionOptional = user.getPublicaciones().stream()
                .filter(publicacion -> publicacion.getId() == id)
                .findFirst();

        if (publicacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("""
                    {"mensaje":"No se encontró ninguna publicación con el ID proporcionado: %d"}
                    """, id));
        }

        // Eliminar la publicación del usuario
        Publicacion publicacion = publicacionOptional.get();
        user.getPublicaciones().remove(publicacion);

        // Guardar el usuario actualizado en la base de datos
        usuarioRepository.save(user);

        return ResponseEntity.ok().build();
    }
}