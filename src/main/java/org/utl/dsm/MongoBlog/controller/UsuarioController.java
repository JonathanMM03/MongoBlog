package org.utl.dsm.MongoBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.utl.dsm.MongoBlog.entity.Usuario;
import org.utl.dsm.MongoBlog.service.UsuarioService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuario", description = "Operaciones relacionadas con usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    @Operation(summary = "Obtener todos los usuarios")
    public List<Usuario> getAllUsers() {
        return usuarioService.getAll();
    }

    @GetMapping("/findByEmail/{email}")
    @Operation(summary = "Obtener usuario por email")
    public Usuario getUserByEmail(@PathVariable String email) {
        return usuarioService.getByEmail(email);
    }

    @GetMapping("/findByUser/{user}")
    @Operation(summary = "Obtener usuario por usuario")
    public Usuario getUserByUser(@PathVariable String user) {
        return usuarioService.getByUser(user);
    }

    @PostMapping("/")
    public Usuario createUser(@RequestBody Usuario user) {
        System.out.println(user.toString());
        // Encriptar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFechaCreacion(Instant.now().toString());
        user.setEstatus("Activo");

        System.out.println(user.toString());

        return usuarioService.create(user);
    }

    @PutMapping("/{email}")
    @Operation(summary = "Actualizar usuario por email")
    public Usuario updateUser(@PathVariable String email, @RequestBody Usuario userDetails) {
        userDetails.setFechaModificacion(Instant.now().toString());
        return usuarioService.update(email, userDetails);
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Eliminar usuario por email")
    public void deleteUser(@PathVariable String email) {
        Usuario usuario = usuarioService.getByEmail(email);
        usuario.setFechaModificacion(Instant.now().toString());
        usuario.setEstatus("Inactivo");
        usuarioService.update(email, usuario);
    }

    @PutMapping("/activar/{email}")
    @Operation(summary = "Eliminar usuario por email")
    public void activateUser(@PathVariable String email) {
        Usuario usuario = usuarioService.getByEmail(email);
        usuario.setFechaModificacion(Instant.now().toString());
        usuario.setEstatus("Activo");
        usuarioService.update(email, usuario);
    }
}