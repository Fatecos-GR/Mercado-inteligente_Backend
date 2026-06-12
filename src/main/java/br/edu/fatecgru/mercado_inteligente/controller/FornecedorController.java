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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Endpoints relacionados aos Fornecedores")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class FornecedorController {

	@Autowired
	private FornecedorService fornecedorService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	private final String pastaFornecedores = "suppliers/";

	@GetMapping
	@Operation(summary = "Listar todos os fornecedores")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fornecedores listados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
	public ResponseEntity<List<FornecedorResponseDTO>> listarTodos() {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.listarTodos().stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
	public ResponseEntity<FornecedorResponseDTO> buscarPorId(@PathVariable Long id) {
		Fornecedor fornecedor = fornecedorService.getById(id);
		if (fornecedor == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Fornecedor não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar fornecedor por nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fornecedores encontrados"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
	public ResponseEntity<List<FornecedorResponseDTO>> buscarPorNome(
			@org.springframework.web.bind.annotation.RequestParam String nome) {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.getByContainsName(nome).stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar fornecedor (Apenas ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
	public ResponseEntity<FornecedorResponseDTO> insert(@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<FornecedorDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null, createBindingResult(dto, violations));
		}

		Fornecedor fornecedor = fornecedorService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar fornecedor (Apenas ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fornecedor alterado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
	public ResponseEntity<FornecedorResponseDTO> atualizar(@PathVariable Long id,
			@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<FornecedorDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null, createBindingResult(dto, violations));
		}

		Fornecedor fornecedor = fornecedorService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir fornecedor (Apenas ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Fornecedor excluído com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		fornecedorService.deletar(id);
		return ResponseEntity.noContent().build();
	}

	private org.springframework.validation.BindingResult createBindingResult(Object target, java.util.Set<? extends jakarta.validation.ConstraintViolation<?>> violations) {
		org.springframework.validation.BeanPropertyBindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(target, "dto");
		for (jakarta.validation.ConstraintViolation<?> violation : violations) {
			bindingResult.addError(new org.springframework.validation.FieldError("dto", violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return bindingResult;
	}
}
