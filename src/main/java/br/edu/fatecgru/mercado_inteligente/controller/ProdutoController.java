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

import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoDTO;
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

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
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

	// Lista todos
	@GetMapping
	@Operation(summary = "Listar todos os produtos")
	public List<Produto> listarTodos() {
		return produtoService.listarTodos();
	}

	// Busca produto por ID
	@GetMapping("/{id}")
	@Operation(summary = "Listar produto por ID")
	public Produto buscarPorId(@PathVariable Long id) {
		return produtoService.getById(id);
	}

	// Busca produto por nome
	@GetMapping("/contem-nome/{nome}")
	@Operation(summary = "Listar produto por Nome")
	public List<Produto> buscarPorContemNome(@PathVariable String nome) {
		return produtoService.getByContainsName(nome);
	}

	// Busca por ID da categoria
	@GetMapping("/categoria/{id}")
	@Operation(summary = "Listar produto por ID da categoria")
	public List<Produto> buscarPorIdCategoria(@PathVariable Long id) {
		return produtoService.getByCategoryId(id);
	}

	// Busca por ID da marca
	@GetMapping("/marca/{id}")
	@Operation(summary = "Listar produto por ID da Marca")
	public List<Produto> buscarPorIdMarca(@PathVariable Long id) {
		return produtoService.getByBrandId(id);
	}

	// Pasta dos produtos para salvar as imagens
	String pastaProdutos = "products/";

	// Salvar produto
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Produto")
	public ResponseEntity<?> insert(@RequestPart("produto") String produtoJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			ProdutoDTO dto = mapper.readValue(produtoJson, ProdutoDTO.class);

			Produto produto = new Produto();

			produto.setNome(dto.getNome());
			produto.setDescricao(dto.getDescricao());
			produto.setPreco(dto.getPreco());
			produto.setValidade(dto.getValidade());

			Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
					.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

			Marca marca = marcaRepository.findById(dto.getMarcaId())
					.orElseThrow(() -> new RuntimeException("Marca não encontrada"));

			Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
					.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

			produto.setCategoria(categoria);
			produto.setMarca(marca);
			produto.setFornecedor(fornecedor);

			String nomeImagem = imagemService.salvarImagem(imagem, pastaProdutos);

			produto.setImagem(nomeImagem);
			return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.saveProduto(produto));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// Alterar produto
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Produto")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestPart("produto") String produtoJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			ProdutoDTO dto = mapper.readValue(produtoJson, ProdutoDTO.class);

			Produto atual = produtoService.getById(id);

			if (atual == null) {
				return ResponseEntity.notFound().build();
			}

			atual.setNome(dto.getNome());
			atual.setDescricao(dto.getDescricao());
			atual.setPreco(dto.getPreco());
			atual.setValidade(dto.getValidade());

			Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
					.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

			Marca marca = marcaRepository.findById(dto.getMarcaId())
					.orElseThrow(() -> new RuntimeException("Marca não encontrada"));

			Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
					.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

			atual.setCategoria(categoria);
			atual.setMarca(marca);
			atual.setFornecedor(fornecedor);

			// substitui imagem
			String imagemAntiga = atual.getImagem();

			String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaProdutos);

			atual.setImagem(imagemAtualizada);

			return ResponseEntity.ok(produtoService.saveProduto(atual));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// Deletar produto
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Produto")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			Produto produto = produtoService.getById(id);

			if (produto == null) {
				return ResponseEntity.notFound().build();
			}

			if (produto.getImagem() != null) {
				imagemService.deletarImagem(produto.getImagem(), pastaProdutos);
			}

			produtoService.deleteProduto(id);

			return ResponseEntity.noContent().build(); // 204

		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao deletar: " + e.getMessage());
		}
	}
}
