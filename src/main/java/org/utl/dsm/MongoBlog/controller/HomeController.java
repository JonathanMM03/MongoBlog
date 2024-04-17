package org.utl.dsm.MongoBlog.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.utl.dsm.MongoBlog.entity.Usuario;
import org.utl.dsm.MongoBlog.repository.UsuarioRepository;

@Controller
@RequestMapping
public class HomeController {
    private UsuarioRepository usuarioRepository;
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Usuario procesarRegistro(@RequestBody Usuario usuario) {
        // Lógica para validar y guardar el nuevo usuario en la base de datos
        return usuarioRepository.save(usuario);
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/")
    public String index() {
        return "home"; // Retorna la vista home.html para la ruta raíz
    }
}
