package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioAtualizacaoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints relacionados aos Usuários")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ImagemService imagemService;

	private final String pastaUsuarios = "users/";

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar todos os usuários(Apenas ADMIN)")
	public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
		List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos().stream()
				.map(UsuarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar usuário por ID (Apenas ADMIN)")
	public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
		Usuario usuario = usuarioService.getById(id);
		if (usuario == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Usuário não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
	}

	@GetMapping("/search")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Buscar usuários por nome (Apenas ADMIN)")
	public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNome(@org.springframework.web.bind.annotation.RequestParam String nome) {
		List<UsuarioResponseDTO> usuarios = usuarioService.getByContainingName(nome).stream()
				.map(UsuarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/clientes")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar clientes (Apenas ADMIN)")
	public ResponseEntity<List<UsuarioResponseDTO>> listarClientes() {
		List<UsuarioResponseDTO> dtos = usuarioService.listarClientes().stream()
				.map(UsuarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar usuário (Apenas ADMIN)")
	public ResponseEntity<UsuarioResponseDTO> insert(@RequestPart("usuario") String usuarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		UsuarioCadastroDTO dto = mapper.readValue(usuarioJson, UsuarioCadastroDTO.class);

		Usuario usuario = usuarioService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.fromEntity(usuario));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Alterar usuário")
	public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestPart("usuario") String usuarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		UsuarioAtualizacaoDTO dto = mapper.readValue(usuarioJson, UsuarioAtualizacaoDTO.class);

		Usuario usuario = usuarioService.atualizar(id, dto);

		String imagemAntiga = usuario.getImagem();
		String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaUsuarios);
		usuario.setImagem(imagemAtualizada);

		usuarioService.save(usuario);

		return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir usuário (Apenas ADMIN)")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		usuarioService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
