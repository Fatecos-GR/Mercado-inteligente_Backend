package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints relacionados aos Usuários")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ImagemService imagemService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar todos os usuários(Apenas ADMIN)")
	public List<Usuario> listarTodos() {
		return usuarioService.listarTodos();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar usuário por ID (Apenas ADMIN)")
	public Usuario buscarPorId(@PathVariable Long id) {
		return usuarioService.getById(id);
	}

	@GetMapping("/contem-nome/{nome}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar usuário por Nome (Apenas ADMIN)")
	public List<Usuario> buscarPorContemNome(@PathVariable String nome) {
		return usuarioService.getByContainsName(nome);
	}

	// Método para listar clientes
	@GetMapping("/clientes")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar clientes (Apenas ADMIN)")
	public ResponseEntity<List<Usuario>> listarClientes() {

		return ResponseEntity.ok(usuarioService.listarClientes());
	}

	// Método para listar administradores
	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar admins (Apenas ADMIN)")
	public ResponseEntity<List<Funcionario>> listarAdmins() {

		return ResponseEntity.ok(usuarioService.listarAdministradores());
	}

	// Método para listar estoquistas
	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)")
	public ResponseEntity<List<Funcionario>> listarEstoquistas() {

		return ResponseEntity.ok(usuarioService.listarEstoquistas());
	}

	// Pasta dos usuário para salvar as imagens
	String pastaUsuarios = "users/";

	// Método para cadastrar usuário
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar usuário (Apenas ADMIN)")
	public ResponseEntity<?> insert(@RequestPart("usuario") String usuarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();
			UsuarioDTO dto = mapper.readValue(usuarioJson, UsuarioDTO.class);

			// cadastra usuário
			Usuario usuario = usuarioService.cadastrar(dto);

			// salva imagem
			String nomeImagem = imagemService.salvarImagem(imagem, pastaUsuarios);

			// adiciona imagem no usuário
			if (nomeImagem != null) {

				usuario.setImagem(nomeImagem);

				usuarioService.save(usuario);
			}

			// response
			UsuarioResponseDTO response = new UsuarioResponseDTO(usuario.getId(), usuario.getNome(),
					usuario.getSobrenome(), usuario.getTelefone(), usuario.getEmail());

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Método para alterar usuário
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Alterar usuário")
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestPart("usuario") String usuarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			UsuarioDTO dto = mapper.readValue(usuarioJson, UsuarioDTO.class);

			// atualiza usuário
			Usuario usuario = usuarioService.atualizar(id, dto);

			String imagemAntiga = usuario.getImagem();

			// atualiza imagem
			String imagemAtualizada = imagemService.substituirImagem(usuario.getImagem(), imagem, pastaUsuarios);

			usuario.setImagem(imagemAtualizada);

			usuarioService.save(usuario);

			// response
			UsuarioResponseDTO response = new UsuarioResponseDTO(usuario.getId(), usuario.getNome(),
					usuario.getSobrenome(), usuario.getTelefone(), usuario.getEmail());

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Método para excluir usuário
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir usuário (Apenas ADMIN)")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			Usuario usuario = usuarioService.getById(id);

			if (usuario == null) {
				return ResponseEntity.notFound().build();
			}

			if (usuario.getImagem() != null) {
				imagemService.deletarImagem(usuario.getImagem(), pastaUsuarios);
			}

			usuarioService.deleteUsuario(id);

			return ResponseEntity.noContent().build(); // 204

		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao deletar: " + e.getMessage());
		}
	}

}
