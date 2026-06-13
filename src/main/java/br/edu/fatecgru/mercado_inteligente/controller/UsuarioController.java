package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioAtualizacaoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints relacionados aos Usuários")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	private final String pastaUsuarios = "users/";

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar todos os usuários(Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
		List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos().stream().map(UsuarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar usuário por ID (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado") })
	public ResponseEntity<UsuarioResponseDTO> buscarPorId(
			@Parameter(description = "ID do usuário", required = true) @PathVariable Long id) {
		Usuario usuario = usuarioService.getById(id);
		if (usuario == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Usuário não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
	}

	@GetMapping("/search")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Buscar usuários por nome (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuários encontrados"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNome(
			@Parameter(description = "Nome ou parte do nome", required = true) @RequestParam String nome) {
		List<UsuarioResponseDTO> usuarios = usuarioService.getByContainsName(nome).stream()
				.map(UsuarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/clientes")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar clientes (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<UsuarioResponseDTO>> listarClientes() {
		List<UsuarioResponseDTO> dtos = usuarioService.listarClientes().stream().map(UsuarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar usuário (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<UsuarioResponseDTO> insert(
			@Parameter(description = "Dados do usuário em JSON", required = true) @RequestPart("usuario") String usuarioJson,
			@Parameter(description = "Arquivo de imagem do usuário") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		UsuarioCadastroDTO dto = mapper.readValue(usuarioJson, UsuarioCadastroDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<UsuarioCadastroDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {

			String mensagem = violations.stream().map(jakarta.validation.ConstraintViolation::getMessage).findFirst()
					.orElse("Dados inválidos");

			throw new IllegalArgumentException(mensagem);
		}

		Usuario usuario = usuarioService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.fromEntity(usuario));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Alterar usuário")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuário alterado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado") })
	public ResponseEntity<UsuarioResponseDTO> atualizar(
			@Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
			@Parameter(description = "Dados atualizados do usuário em JSON", required = true) @RequestPart("usuario") String usuarioJson,
			@Parameter(description = "Novo arquivo de imagem (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		UsuarioAtualizacaoDTO dto = mapper.readValue(usuarioJson, UsuarioAtualizacaoDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<UsuarioAtualizacaoDTO>> violations = validator
				.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Usuario usuario = usuarioService.atualizar(id, dto);

		ImagemDTO novaImagem = imagemService.substituirImagem(usuario.getPublicIdImagem(), imagem, pastaUsuarios);

		if (novaImagem != null) {
			usuario.setImagem(novaImagem.getUrl());
			usuario.setPublicIdImagem(novaImagem.getPublicId());
		}

		usuarioService.save(usuario);

		return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir usuário (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado") })
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID do usuário", required = true) @PathVariable Long id) {
		usuarioService.deletar(id);
		return ResponseEntity.noContent().build();
	}

	private org.springframework.validation.BindingResult createBindingResult(Object target,
			java.util.Set<? extends jakarta.validation.ConstraintViolation<?>> violations) {
		org.springframework.validation.BeanPropertyBindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(
				target, "dto");
		for (jakarta.validation.ConstraintViolation<?> violation : violations) {
			bindingResult.addError(new org.springframework.validation.FieldError("dto",
					violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return bindingResult;
	}
}
