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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.edu.fatecgru.mercado_inteligente.mapper.ProdutoMapper;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.repository.CategoriaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MarcaRepository;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import br.edu.fatecgru.mercado_inteligente.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gestão do catálogo de produtos")
public class ProdutoController {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private MarcaRepository marcaRepository;

	@Autowired
	private FornecedorRepository fornecedorRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	private final String pastaProdutos = "products/";

	@GetMapping
	@Operation(summary = "Listar todos os produtos", description = "Retorna uma lista completa de produtos com suas categorias, marcas e fornecedores.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
	})
	public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
		List<ProdutoResponseDTO> produtos = produtoService.listarTodos().stream().map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Listar produto por ID", description = "Busca os detalhes completos de um produto específico.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Produto encontrado"),
		@ApiResponse(responseCode = "404", description = "Produto não encontrado")
	})
	public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable @Parameter(description = "ID do produto") Long id) {
		Produto produto = produtoService.getById(id);
		if (produto == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Produto não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(ProdutoMapper.toDTO(produto));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar produtos por Nome", description = "Filtra produtos cujo nome contenha o termo pesquisado (case-insensitive).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
	})
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam @Parameter(description = "Nome ou parte do nome para busca") String nome) {
		List<ProdutoResponseDTO> produtos = produtoService.getByContainingName(nome).stream().map(ProdutoMapper::toDTO)
				.toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/categoria/{id}")
	@Operation(summary = "Listar produtos por ID da categoria", description = "Retorna todos os produtos vinculados a uma categoria específica.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
		@ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	})
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdCategoria(@PathVariable @Parameter(description = "ID da categoria") Long id) {
		List<ProdutoResponseDTO> produtos = produtoService.getByCategoryId(id).stream().map(ProdutoMapper::toDTO)
				.toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/marca/{id}")
	@Operation(summary = "Listar produtos por ID da Marca", description = "Retorna todos os produtos vinculados a uma marca específica.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
		@ApiResponse(responseCode = "404", description = "Marca não encontrada")
	})
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdMarca(@PathVariable @Parameter(description = "ID da marca") Long id) {
		List<ProdutoResponseDTO> produtos = produtoService.getByBrandId(id).stream().map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(produtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Produto", description = "Cria um novo produto vinculando-o a categoria, marca e fornecedor, com suporte a upload de imagem (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
		@ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de validação"),
		@ApiResponse(responseCode = "403", description = "Acesso negado"),
		@ApiResponse(responseCode = "404", description = "Categoria, Marca ou Fornecedor não encontrados")
	})
	public ResponseEntity<ProdutoResponseDTO> insert(@RequestPart("produto") @Parameter(description = "Dados do produto em formato JSON") String produtoJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Arquivo de imagem") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ProdutoDTO dto = mapper.readValue(produtoJson, ProdutoDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Produto produto = new Produto();
		produto.setNome(dto.nome());
		produto.setDescricao(dto.descricao());
		produto.setPreco(dto.preco());
		produto.setValidade(dto.validade());

		Categoria categoria = categoriaRepository.findById(dto.categoriaId())
				.orElseThrow(() -> new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
						"Categoria não encontrada"));

		Marca marca = marcaRepository.findById(dto.marcaId())
				.orElseThrow(() -> new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
						"Marca não encontrada"));

		Fornecedor fornecedor = fornecedorRepository.findById(dto.fornecedorId())
				.orElseThrow(() -> new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
						"Fornecedor não encontrado"));

		produto.setCategoria(categoria);
		produto.setMarca(marca);
		produto.setFornecedor(fornecedor);

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaProdutos);

		if (imagemDTO != null) {
			produto.setImagem(imagemDTO.getUrl());
			produto.setPublicIdImagem(imagemDTO.getPublicId());
		}

		Produto salvo = produtoService.saveProduto(produto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoMapper.toDTO(salvo));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Produto", description = "Atualiza os dados de um produto existente e permite substituir a imagem (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
		@ApiResponse(responseCode = "404", description = "Produto, Categoria, Marca ou Fornecedor não encontrados"),
		@ApiResponse(responseCode = "400", description = "Dados inválidos"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<ProdutoResponseDTO> update(@PathVariable @Parameter(description = "ID do produto") Long id, @RequestPart("produto") @Parameter(description = "Dados do produto em formato JSON") String produtoJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Nova imagem (opcional)") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ProdutoDTO dto = mapper.readValue(produtoJson, ProdutoDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Produto atual = produtoService.getById(id);
		if (atual == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Produto não encontrado com ID: " + id);
		}

		atual.setNome(dto.nome());
		atual.setDescricao(dto.descricao());
		atual.setPreco(dto.preco());
		atual.setValidade(dto.validade());

		Categoria categoria = categoriaRepository.findById(dto.categoriaId())
				.orElseThrow(() -> new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
						"Categoria não encontrada"));

		Marca marca = marcaRepository.findById(dto.marcaId())
				.orElseThrow(() -> new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
						"Marca não encontrada"));

		Fornecedor fornecedor = fornecedorRepository.findById(dto.fornecedorId())
				.orElseThrow(() -> new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
						"Fornecedor não encontrado"));

		atual.setCategoria(categoria);
		atual.setMarca(marca);
		atual.setFornecedor(fornecedor);

		ImagemDTO novaImagem = imagemService.substituirImagem(atual.getPublicIdImagem(), imagem, pastaProdutos);

		if (novaImagem != null) {
			atual.setImagem(novaImagem.getUrl());
			atual.setPublicIdImagem(novaImagem.getPublicId());
		}

		Produto atualizado = produtoService.saveProduto(atual);
		return ResponseEntity.ok(ProdutoMapper.toDTO(atualizado));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Produto", description = "Exclui um produto do sistema se não estiver vinculado a carrinhos ativos (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Produto excluído com sucesso"),
		@ApiResponse(responseCode = "404", description = "Produto não encontrado"),
		@ApiResponse(responseCode = "400", description = "Erro de integridade (vínculo com carrinhos ativos)"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "ID do produto") Long id) {
		Produto produto = produtoService.getById(id);
		if (produto == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Produto não encontrado com ID: " + id);
		}

		if (produto.getPublicIdImagem() != null) {
			imagemService.deletarImagem(produto.getPublicIdImagem());
		}

		produtoService.deleteProduto(id);
		return ResponseEntity.noContent().build();
	}
}
