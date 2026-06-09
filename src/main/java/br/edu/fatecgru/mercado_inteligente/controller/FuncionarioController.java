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

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.service.FuncionarioService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
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

	private final String pastaFuncionarios = "employees/";

	@GetMapping
	@Operation(summary = "Listar todos os funcionários")
	public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
		List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos().stream()
				.map(FuncionarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Listar funcionário por ID")
	public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
		Funcionario funcionario = funcionarioService.getById(id);
		if (funcionario == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Funcionário não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar admins (Apenas ADMIN)")
	public ResponseEntity<List<FuncionarioResponseDTO>> listarAdmins() {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarAdministradores().stream()
				.map(FuncionarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)")
	public ResponseEntity<List<FuncionarioResponseDTO>> listarEstoquistas() {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarEstoquistas().stream()
				.map(FuncionarioResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar funcionário (Apenas ADMIN)")
	public ResponseEntity<FuncionarioResponseDTO> insert(@RequestPart("funcionario") String funcionarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		Funcionario funcionario = funcionarioService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Alterar funcionário")
	public ResponseEntity<FuncionarioResponseDTO> atualizar(@PathVariable Long id, @RequestPart("funcionario") String funcionarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		Funcionario funcionario = funcionarioService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir funcionário (Apenas ADMIN)")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		funcionarioService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
