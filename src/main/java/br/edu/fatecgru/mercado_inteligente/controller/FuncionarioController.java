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
import jakarta.validation.Valid;

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
	@Operation(summary = "Listar todos os funcionários", description = "Retorna uma lista de todos os funcionários cadastrados. Por padrão, retorna apenas os ativos. Requer permissão de ADMIN ou ESTOQUISTA.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Funcionários listados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos(
			@Parameter(description = "Se true, inclui funcionários desativados na lista") @RequestParam(defaultValue = "false") boolean incluirInativos) {
		List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos(incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar funcionário por ID", description = "Busca os detalhes de um funcionário específico através do seu ID único.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") 
	})
	public ResponseEntity<FuncionarioResponseDTO> buscarPorId(
			@Parameter(description = "ID do funcionário", required = true, example = "1") @PathVariable Long id) {
		Funcionario funcionario = funcionarioService.getById(id);
		if (funcionario == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Funcionário não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@GetMapping("/buscar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Buscar funcionário por nome (Apenas ADMIN)", description = "Realiza uma busca por funcionários ativos cujo nome contenha o termo pesquisado.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorNome(
			@Parameter(description = "Parte do nome ou nome completo para busca", required = true, example = "Carlos") @RequestParam String nome,
			@Parameter(description = "Se true, inclui funcionários desativados na busca") @RequestParam(defaultValue = "false") boolean incluirInativos) {

		List<FuncionarioResponseDTO> funcionarios = funcionarioService.getByNomeCompleto(nome, incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();

		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar administradores (Apenas ADMIN)", description = "Retorna uma lista contendo apenas os administradores. Por padrão, apenas os ativos.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Administradores listados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> listarAdmins(
			@Parameter(description = "Se true, inclui administradores desativados na lista") @RequestParam(defaultValue = "false") boolean incluirInativos) {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarAdministradores(incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)", description = "Retorna uma lista contendo apenas os estoquistas. Por padrão, apenas os ativos.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Estoquistas listados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> listarEstoquistas(
			@Parameter(description = "Se true, inclui estoquistas desativados na lista") @RequestParam(defaultValue = "false") boolean incluirInativos) {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarEstoquistas(incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar novo funcionário (Apenas ADMIN)", description = "Cadastra um novo funcionário no sistema, definindo obrigatoriamente se é ADMIN ou ESTOQUISTA. Requer privilégios de administrador.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<FuncionarioResponseDTO> insert(
			@Parameter(description = "Dados do funcionário em formato JSON", required = true) @RequestPart("funcionario") String funcionarioJson,
			@Parameter(description = "Arquivo de imagem de perfil (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<FuncionarioCadastroDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Funcionario funcionario = funcionarioService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Atualizar funcionário", description = "Atualiza os dados de um funcionário existente. Um ADMIN pode atualizar qualquer funcionário, enquanto um funcionário só pode atualizar o seu próprio perfil.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") 
	})
	public ResponseEntity<FuncionarioResponseDTO> atualizar(
			@Parameter(description = "ID do funcionário a ser atualizado", required = true, example = "1") @PathVariable Long id,
			@Parameter(description = "Novos dados do funcionário em formato JSON", required = true) @RequestPart("funcionario") String funcionarioJson,
			@Parameter(description = "Nova imagem de perfil (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<FuncionarioCadastroDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Funcionario funcionario = funcionarioService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Desativar funcionário (Apenas ADMIN)", description = "Desativa um funcionário do sistema. Isso bloqueia seu acesso mas mantém o histórico de suas movimentações de estoque para fins de auditoria.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "204", description = "Funcionário desativado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") 
	})
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID do funcionário a ser removido", required = true, example = "1") @PathVariable Long id) {
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
