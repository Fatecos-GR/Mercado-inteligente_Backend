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

import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Endpoints para gestão de fornecedores")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class FornecedorController {

	@Autowired
	private FornecedorService fornecedorService;

	@Autowired
	private jakarta.validation.Validator validator;

	@GetMapping
	@Operation(summary = "Listar todos os fornecedores", description = "Retorna uma lista de todos os fornecedores cadastrados.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<FornecedorResponseDTO>> listarTodos() {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.listarTodos().stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID", description = "Busca os detalhes de um fornecedor específico.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
		@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<FornecedorResponseDTO> buscarPorId(@PathVariable @Parameter(description = "ID do fornecedor") Long id) {
		Fornecedor fornecedor = fornecedorService.getById(id);
		if (fornecedor == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Fornecedor não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar fornecedor por nome", description = "Filtra fornecedores cujo nome contenha o termo pesquisado (case-insensitive).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<FornecedorResponseDTO>> buscarPorNome(@RequestParam @Parameter(description = "Nome ou parte do nome para busca") String nome) {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.getByContainingName(nome).stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar fornecedor", description = "Cria um novo fornecedor com suporte a upload de logotipo (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso"),
		@ApiResponse(responseCode = "400", description = "Dados inválidos"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<FornecedorResponseDTO> insert(@RequestPart("fornecedor") @Parameter(description = "Dados do fornecedor em formato JSON") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Arquivo de imagem") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Fornecedor fornecedor = fornecedorService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar fornecedor", description = "Atualiza os dados de um fornecedor existente (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso"),
		@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<FornecedorResponseDTO> atualizar(@PathVariable @Parameter(description = "ID do fornecedor") Long id,
			@RequestPart("fornecedor") @Parameter(description = "Dados do fornecedor em formato JSON") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Nova imagem (opcional)") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Fornecedor fornecedor = fornecedorService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir fornecedor", description = "Remove um fornecedor do sistema (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Fornecedor excluído com sucesso"),
		@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "ID do fornecedor") Long id) {
		fornecedorService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
