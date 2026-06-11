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

import br.edu.fatecgru.mercado_inteligente.mapper.ProdutoMapper;
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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints relacionados aos Produtos")
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

	private final String pastaProdutos = "products/";

	@GetMapping
	@Operation(summary = "Listar todos os produtos")
	public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
		List<ProdutoResponseDTO> produtos = produtoService.listarTodos().stream().map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Listar produto por ID")
	public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
		Produto produto = produtoService.getById(id);
		if (produto == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Produto não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(ProdutoMapper.toDTO(produto));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar produtos por Nome")
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(
			@org.springframework.web.bind.annotation.RequestParam String nome) {
		List<ProdutoResponseDTO> produtos = produtoService.getByContainsName(nome).stream().map(ProdutoMapper::toDTO)
				.toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/categoria/{id}")
	@Operation(summary = "Listar produtos por ID da categoria")
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdCategoria(@PathVariable Long id) {
		List<ProdutoResponseDTO> produtos = produtoService.getByCategoryId(id).stream().map(ProdutoMapper::toDTO)
				.toList();
		return ResponseEntity.ok(produtos);
	}

	@GetMapping("/marca/{id}")
	@Operation(summary = "Listar produtos por ID da Marca")
	public ResponseEntity<List<ProdutoResponseDTO>> buscarPorIdMarca(@PathVariable Long id) {
		List<ProdutoResponseDTO> produtos = produtoService.getByBrandId(id).stream().map(ProdutoMapper::toDTO).toList();
		return ResponseEntity.ok(produtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Produto")
	public ResponseEntity<ProdutoResponseDTO> insert(@RequestPart("produto") String produtoJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ProdutoDTO dto = mapper.readValue(produtoJson, ProdutoDTO.class);

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

		String nomeImagem = imagemService.salvarImagem(imagem, pastaProdutos);
		produto.setImagem(nomeImagem);

		Produto salvo = produtoService.saveProduto(produto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoMapper.toDTO(salvo));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Produto")
	public ResponseEntity<ProdutoResponseDTO> update(@PathVariable Long id, @RequestPart("produto") String produtoJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ProdutoDTO dto = mapper.readValue(produtoJson, ProdutoDTO.class);

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

		String imagemAntiga = atual.getImagem();
		String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaProdutos);
		atual.setImagem(imagemAtualizada);

		Produto atualizado = produtoService.saveProduto(atual);
		return ResponseEntity.ok(ProdutoMapper.toDTO(atualizado));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Produto")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Produto produto = produtoService.getById(id);
		if (produto == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Produto não encontrado com ID: " + id);
		}

		if (produto.getImagem() != null) {
			imagemService.deletarImagem(produto.getImagem(), pastaProdutos);
		}

		produtoService.deleteProduto(id);
		return ResponseEntity.noContent().build();
	}
}