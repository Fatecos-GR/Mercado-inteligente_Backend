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

import br.edu.fatecgru.mercado_inteligente.mapper.ProdutoMapper;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/marcas")
@Tag(name = "Marcas", description = "Endpoints para gestão de marcas do mercado")
public class MarcaController {

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	private final String pastaMarcas = "brands/";

	@GetMapping
	@Operation(summary = "Listar todas as Marcas", description = "Retorna uma lista de todas as marcas cadastradas.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
	})
	public ResponseEntity<List<MarcaResponseDTO>> listarTodos() {
		List<MarcaResponseDTO> marcas = marcaService.listarTodos().stream().map(MarcaResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(marcas);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar marca por ID", description = "Busca os detalhes de uma marca específica.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Marca encontrada"),
		@ApiResponse(responseCode = "404", description = "Marca não encontrada")
	})
	public ResponseEntity<MarcaResponseDTO> buscarPorId(@PathVariable @Parameter(description = "ID da marca") Long id) {
		Marca marca = marcaService.getById(id);
		if (marca == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(MarcaResponseDTO.fromEntity(marca));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar marcas por nome", description = "Filtra marcas cujo nome contenha o termo pesquisado (case-insensitive).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
	})
	public ResponseEntity<List<MarcaResponseDTO>> buscarPorNome(@RequestParam @Parameter(description = "Nome ou parte do nome para busca") String nome) {
		List<MarcaResponseDTO> marcas = marcaService.getByContainingName(nome).stream().map(MarcaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(marcas);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Marca", description = "Cria uma nova marca com suporte a upload de imagem (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Marca criada com sucesso"),
		@ApiResponse(responseCode = "400", description = "Dados inválidos"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<MarcaResponseDTO> insert(@RequestPart("marca") @Parameter(description = "Dados da marca em formato JSON") String marcaJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Arquivo de imagem") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO dto = mapper.readValue(marcaJson, br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Marca marca = marcaService.cadastrar(dto);

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaMarcas);

		if (imagemDTO != null) {
			marca.setImagem(imagemDTO.getUrl());
			marca.setPublicIdImagem(imagemDTO.getPublicId());
			marcaService.save(marca);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(MarcaResponseDTO.fromEntity(marca));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Marca", description = "Atualiza os dados de uma marca existente (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Marca atualizada com sucesso"),
		@ApiResponse(responseCode = "404", description = "Marca não encontrada"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<MarcaResponseDTO> update(@PathVariable @Parameter(description = "ID da marca") Long id, @RequestPart("marca") @Parameter(description = "Dados da marca em formato JSON") String marcaJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Nova imagem (opcional)") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO dto = mapper.readValue(marcaJson, br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Marca atual = marcaService.getById(id);

		if (atual == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}

		atual.setNome(dto.getNome());
		atual.setDescricao(dto.getDescricao());

		ImagemDTO novaImagem = imagemService.substituirImagem(atual.getPublicIdImagem(), imagem, pastaMarcas);

		if (novaImagem != null) {
			atual.setImagem(novaImagem.getUrl());
			atual.setPublicIdImagem(novaImagem.getPublicId());
		}

		Marca salvo = marcaService.save(atual);
		return ResponseEntity.ok(MarcaResponseDTO.fromEntity(salvo));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Marca", description = "Exclui uma marca do sistema (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Marca excluída com sucesso"),
		@ApiResponse(responseCode = "404", description = "Marca não encontrada"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "ID da marca") Long id) {
		Marca marca = marcaService.getById(id);

		if (marca == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}

		if (marca.getPublicIdImagem() != null) {
			imagemService.deletarImagem(marca.getPublicIdImagem());
		}

		marcaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
