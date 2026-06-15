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

import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/marcas")
@Tag(name = "Marcas", description = "Endpoints relacionados as Marcas")
public class MarcaController {

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private jakarta.validation.Validator validator;

	@GetMapping
	@Operation(summary = "Listar todas as Marcas")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Marcas listadas com sucesso") })
	public ResponseEntity<List<MarcaResponseDTO>> listarTodos() {
		List<MarcaResponseDTO> marcas = marcaService.listarTodos().stream().map(MarcaResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(marcas);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar marca por ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Marca encontrada"),
			@ApiResponse(responseCode = "404", description = "Marca não encontrada") })
	public ResponseEntity<MarcaResponseDTO> buscarPorId(
			@Parameter(description = "ID da marca", required = true) @PathVariable Long id) {
		Marca marca = marcaService.getById(id);
		if (marca == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(MarcaResponseDTO.fromEntity(marca));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar marcas por nome")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Marcas encontradas") })
	public ResponseEntity<List<MarcaResponseDTO>> buscarPorNome(
			@Parameter(description = "Nome ou parte do nome", required = true) @RequestParam String nome) {
		List<MarcaResponseDTO> marcas = marcaService.getByContainsName(nome).stream().map(MarcaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(marcas);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Marca")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Marca criada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<MarcaResponseDTO> insert(
			@Parameter(description = "Dados da marca em JSON", required = true) @RequestPart("marca") String marcaJson,
			@Parameter(description = "Arquivo de imagem da marca") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		MarcaDTO dto = mapper.readValue(marcaJson, MarcaDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<MarcaDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Marca marca = marcaService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(MarcaResponseDTO.fromEntity(marca));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Marca")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Marca alterada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Marca não encontrada") })
	public ResponseEntity<MarcaResponseDTO> update(
			@Parameter(description = "ID da marca", required = true) @PathVariable Long id,
			@Parameter(description = "Dados atualizados da marca em JSON", required = true) @RequestPart("marca") String marcaJson,
			@Parameter(description = "Novo arquivo de imagem (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		MarcaDTO dto = mapper.readValue(marcaJson, MarcaDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<MarcaDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Marca marca = marcaService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(MarcaResponseDTO.fromEntity(marca));

	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Marca")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Marca excluída com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Marca não encontrada") })
	public ResponseEntity<Void> delete(@Parameter(description = "ID da marca", required = true) @PathVariable Long id) {

		marcaService.delete(id);

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
