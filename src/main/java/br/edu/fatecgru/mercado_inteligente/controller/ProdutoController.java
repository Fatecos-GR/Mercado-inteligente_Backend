package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;
import java.util.Set;

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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.edu.fatecgru.mercado_inteligente.mapper.ProdutoMapper;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoRequestDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.swagger.ProdutoMultipartRequest;
import br.edu.fatecgru.mercado_inteligente.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints relacionados aos Produtos")
public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private Validator validator;

	@GetMapping
	@Operation(summary = "Listar todos os produtos")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Produtos listados com sucesso") })
	public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
		List<ProdutoResponseDTO> produtos = produtoService.listarTodos().stream().map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/descontos")
	@Operation(summary = "Listar últimos produtos com redução de preço (Público)", description = "Retorna os últimos 5 produtos do catálogo que receberam descontos, ordenados do mais recente para o mais antigo.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista de descontos obtida com sucesso") })
	public ResponseEntity<List<ProdutoResponseDTO>> listarUltimosDescontos() {
		List<ProdutoResponseDTO> descontos = produtoService.listarUltimosDescontos().stream()
				.map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(descontos);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Listar produto por ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Produto encontrado"),
			@ApiResponse(responseCode = "404", description = "Produto não encontrado") })
	public ResponseEntity<ProdutoResponseDTO> buscarPorId(
			@Parameter(description = "ID do produto", required = true) @PathVariable Long id) {
		Produto produto = produtoService.getById(id);
		if (produto == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Produto não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(ProdutoMapper.toDTO(produto));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar produtos por Nome")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Produtos encontrados") })
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(
			@Parameter(description = "Nome ou parte do nome", required = true) @RequestParam String nome) {
		List<ProdutoResponseDTO> produtos = produtoService.getByContainsName(nome).stream().map(ProdutoMapper::toDTO)
				.toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/categoria/{id}")
	@Operation(summary = "Listar produtos por ID da categoria")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
			@ApiResponse(responseCode = "404", description = "Categoria não encontrada") })
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdCategoria(
			@Parameter(description = "ID da categoria", required = true) @PathVariable Long id) {
		List<ProdutoResponseDTO> produtos = produtoService.getByCategoryId(id).stream().map(ProdutoMapper::toDTO)
				.toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/marca/{id}")
	@Operation(summary = "Listar produtos por ID da Marca")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
			@ApiResponse(responseCode = "404", description = "Marca não encontrada") })
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdMarca(
			@Parameter(description = "ID da marca", required = true) @PathVariable Long id) {
		List<ProdutoResponseDTO> produtos = produtoService.getByBrandId(id).stream().map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/fornecedor/{id}")
	@Operation(summary = "Listar produtos por ID do fornecedor")
	@ApiResponses(value = {

			@ApiResponse(responseCode = "200", description = "Produtos encontrados"),

			@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")

	})
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdFornecedor(

			@Parameter(description = "ID do fornecedor", required = true)

			@PathVariable Long id) {

		List<ProdutoResponseDTO> produtos = produtoService.getBySupplierId(id).stream().map(ProdutoMapper::toDTO)
				.toList();

		return ResponseEntity.ok(produtos);
	}

	// Listar por maior preço
	@GetMapping("/preco/menor")
	public ResponseEntity<List<ProdutoResponseDTO>> listarPorMenorPreco() {

		List<ProdutoResponseDTO> produtos = produtoService.listarPorMenorPreco().stream().map(ProdutoMapper::toDTO)
				.toList();

		return ResponseEntity.ok(produtos);
	}

	// Listar por menor preço
	@GetMapping("/preco/maior")
	public ResponseEntity<List<ProdutoResponseDTO>> listarPorMaiorPreco() {

		List<ProdutoResponseDTO> produtos = produtoService.listarPorMaiorPreco().stream().map(ProdutoMapper::toDTO)
				.toList();

		return ResponseEntity.ok(produtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Produto",

			requestBody = @RequestBody(required = true,

					content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,

							schema = @Schema(implementation = ProdutoMultipartRequest.class))))

	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<ProdutoResponseDTO> insert(
			@Parameter(description = "Dados do produto", required = true) @RequestPart("produto") String produtoJson,
			@Parameter(description = "Arquivo de imagem do produto") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ProdutoRequestDTO dto = mapper.readValue(produtoJson, ProdutoRequestDTO.class);

		Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);

		if (!violations.isEmpty()) {

			String mensagem = violations.stream().map(ConstraintViolation::getMessage).findFirst()
					.orElse("Dados inválidos");

			throw new IllegalArgumentException(mensagem);
		}

		Produto produto = produtoService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoMapper.toDTO(produto));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Produto",

			requestBody = @RequestBody(required = true,

					content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,

							schema = @Schema(implementation = ProdutoMultipartRequest.class))))

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Produto alterado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Produto não encontrado") })
	public ResponseEntity<ProdutoResponseDTO> update(
			@Parameter(description = "ID do produto", required = true) @PathVariable Long id,
			@Parameter(description = "Dados atualizados do produto em JSON", required = true) @RequestPart("produto") String produtoJson,
			@Parameter(description = "Novo arquivo de imagem (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ProdutoRequestDTO dto = mapper.readValue(produtoJson, ProdutoRequestDTO.class);

		Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);

		if (!violations.isEmpty()) {

			String mensagem = violations.stream().map(ConstraintViolation::getMessage).findFirst()
					.orElse("Dados inválidos");

			throw new IllegalArgumentException(mensagem);
		}

		Produto produto = produtoService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(ProdutoMapper.toDTO(produto));

	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Produto")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Produto não encontrado") })
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID do produto", required = true) @PathVariable Long id) {

		produtoService.delete(id);

		return ResponseEntity.noContent().build();

	}

}
