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

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.service.FuncionarioService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "Endpoints relacionados aos Funcionários")
public class FuncionarioController {

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private ImagemService imagemService;

	// Lista todas os funcionários
	@GetMapping
	@Operation(summary = "Listar todos os funcionários")
	public List<Funcionario> listarTodos() {
		return funcionarioService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	@Operation(summary = "Listar funcionário por ID")
	public Funcionario buscarPorId(@PathVariable Long id) {
		return funcionarioService.getById(id);
	}

	// Método para listar administradores
	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar admins (Apenas ADMIN)")
	public ResponseEntity<List<Funcionario>> listarAdmins() {

		return ResponseEntity.ok(funcionarioService.listarAdministradores());
	}

	// Método para listar estoquistas
	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)")
	public ResponseEntity<List<Funcionario>> listarEstoquistas() {

		return ResponseEntity.ok(funcionarioService.listarEstoquistas());
	}

	// Pasta dos funcionários para salvar as imagens
	String pastaFuncionarios = "employees/";

	// Método para cadastrar funcionário
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar funcionário (Apenas ADMIN)")
	public ResponseEntity<?> insert(@RequestPart("funcionario") String funcionarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

			Funcionario funcionario = funcionarioService.cadastrar(dto, imagem);

			return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponseDTO.fromEntity(funcionario));

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Método para alterar funcionário
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Alterar funcionário")
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestPart("funcionario") String funcionarioJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

			// atualiza funcionário
			Funcionario funcionario = funcionarioService.atualizar(id, dto, imagem);

			// response
			return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Método para excluir funcionário
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir funcionário (Apenas ADMIN)")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {

			funcionarioService.deletar(id);

			return ResponseEntity.noContent().build();

		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar: " + e.getMessage());
		}

	}

}