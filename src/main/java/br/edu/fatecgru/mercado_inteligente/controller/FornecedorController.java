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

	private final String pastaFornecedores = "suppliers/";

	@GetMapping
	@Operation(summary = "Listar todos os fornecedores")
	public ResponseEntity<List<FornecedorResponseDTO>> listarTodos() {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.listarTodos().stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
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
	public ResponseEntity<List<FornecedorResponseDTO>> buscarPorNome(
			@org.springframework.web.bind.annotation.RequestParam String nome) {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.getByContainsName(nome).stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar fornecedor (Apenas ADMIN)")
	public ResponseEntity<FornecedorResponseDTO> insert(@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		Fornecedor fornecedor = fornecedorService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar fornecedor (Apenas ADMIN)")
	public ResponseEntity<FornecedorResponseDTO> atualizar(@PathVariable Long id,
			@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		Fornecedor fornecedor = fornecedorService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir fornecedor (Apenas ADMIN)")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Fornecedor fornecedor = fornecedorService.getById(id);

		fornecedorService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
