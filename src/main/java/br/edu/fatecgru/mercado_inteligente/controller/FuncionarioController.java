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

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.service.FuncionarioService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "Endpoints relacionados aos Funcionários")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class FuncionarioController {

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	@GetMapping
	@Operation(summary = "Listar todos os funcionários")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Funcionários listados com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
		List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos().stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Listar funcionário por ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") })
	public ResponseEntity<FuncionarioResponseDTO> buscarPorId(
			@Parameter(description = "ID do funcionário", required = true) @PathVariable Long id) {
		Funcionario funcionario = funcionarioService.getById(id);
		if (funcionario == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Funcionário não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar admins (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Admins listados com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<FuncionarioResponseDTO>> listarAdmins() {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarAdministradores().stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Estoquistas listados com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<FuncionarioResponseDTO>> listarEstoquistas() {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarEstoquistas().stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar funcionário (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<FuncionarioResponseDTO> insert(
			@Parameter(description = "Dados do funcionário em JSON", required = true) @RequestPart("funcionario") String funcionarioJson,
			@Parameter(description = "Arquivo de imagem do funcionário") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<FuncionarioCadastroDTO>> violations = validator
				.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Funcionario funcionario = funcionarioService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Alterar funcionário")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Funcionário alterado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") })
	public ResponseEntity<FuncionarioResponseDTO> atualizar(
			@Parameter(description = "ID do funcionário", required = true) @PathVariable Long id,
			@Parameter(description = "Dados atualizados do funcionário em JSON", required = true) @RequestPart("funcionario") String funcionarioJson,
			@Parameter(description = "Novo arquivo de imagem (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<FuncionarioCadastroDTO>> violations = validator
				.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Funcionario funcionario = funcionarioService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir funcionário (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Funcionário excluído com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") })
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID do funcionário", required = true) @PathVariable Long id) {
		funcionarioService.deletar(id);
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
