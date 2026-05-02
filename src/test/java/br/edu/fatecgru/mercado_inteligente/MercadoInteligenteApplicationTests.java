package br.edu.fatecgru.mercado_inteligente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.edu.fatecgru.mercado_inteligente.model.entity.TipoUsuario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@ActiveProfiles("test")
@SpringBootTest
class MercadoInteligenteApplicationTests {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void deveEncontrarUsuarioPorEmail() {
		// 1. Prepara (Cria e salva um usuário fictício)
		Usuario usuario = new Usuario();
		usuario.setNome("João");
		usuario.setSobrenome("Silva");
		usuario.setTelefone("11999999999");
		usuario.setSenha("123456");
		usuario.setEmail("joao@teste.com");
		usuario.setTipo(TipoUsuario.CLIENTE);

		usuarioRepository.save(usuario);

		// 2. Executa (Usa o seu método findByEmail)
		Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("joao@teste.com");

		// 3. Verifica (O teste só passa se isso for verdade)
		assertTrue(usuarioEncontrado.isPresent(), "Deveria ter encontrado o usuário joao@teste.com");
		assertEquals("João", usuarioEncontrado.get().getNome(), "O nome do usuário deveria ser João");
	}

}
