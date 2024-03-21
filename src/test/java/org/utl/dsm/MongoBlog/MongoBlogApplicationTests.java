package org.utl.dsm.MongoBlog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.utl.dsm.MongoBlog.config.SecurityConfig;
import org.utl.dsm.MongoBlog.entity.Usuario;
import org.utl.dsm.MongoBlog.repository.UsuarioRepository;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MongoBlogApplicationTests {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Test
	void testCrearUsuario() {
		Usuario admin = new Usuario();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		admin.setNombre("John");
		admin.setApellidos("Doe");
		admin.setFechaNacimiento("01/01/1990");
		admin.setEmail("admin@utleon.edu.mx");
		admin.setPassword(bCryptPasswordEncoder.encode("admin"));
		admin.setPublicaciones(new ArrayList<>());
		Usuario usuario = usuarioRepository.save(admin);
		assertThat(usuario.getId()).isNotNull();
	}

}
