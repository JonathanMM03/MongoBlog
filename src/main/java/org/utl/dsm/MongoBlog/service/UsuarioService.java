package org.utl.dsm.MongoBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.utl.dsm.MongoBlog.entity.Usuario;
import org.utl.dsm.MongoBlog.repository.UsuarioRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) return new Usuario(usuario);
        return null;
    }

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    public Usuario getByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario getByUser(String user) {
        return usuarioRepository.findByUsuario(user);
    }

    public Usuario create(Usuario usuario) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuario.setUsuario(Instant.parse(usuario.getFechaNacimiento() + "T00:00:00.000Z").toString());

        // Verificar si existen usuarios en la base de datos
        boolean existenUsuarios = !usuarios.isEmpty();

        // Obtener el último ID de la lista de usuarios
        int nuevoId = existenUsuarios ? usuarios.stream().mapToInt(u -> Integer.parseInt(u.getId())).max().getAsInt() + 1 : 0;

        // Asignar el nuevo ID al usuario
        usuario.setId(String.valueOf(nuevoId));

        // Encriptar la contraseña del usuario
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar el usuario en la base de datos
        return usuarioRepository.save(usuario);
    }

    public Usuario update(String email, Usuario usuario) {
        Usuario u = usuarioRepository.findByEmail(email);
        if (u == null) {
            throw new RuntimeException("Usuario no encontrado con email: " + email);
        }

        u.setNombre(usuario.getNombre());
        u.setApellidos(usuario.getApellidos());
        u.setUsuario(usuario.getUsuario());

        // Si se proporciona una nueva contraseña, encriptarla y asignarla al usuario
        if (usuario.getPassword() != null || !usuario.getPassword().isEmpty())
            u.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Convertir String a Date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date fechaNacimiento = sdf.parse(usuario.getFechaNacimiento());
            u.setFechaNacimiento(fechaNacimiento.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar la excepción en caso de que ocurra un error al parsear la fecha
        }

        return usuarioRepository.save(u);
    }

    public void delete(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }
}
