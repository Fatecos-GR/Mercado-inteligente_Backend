package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
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

	// Pasta dos produtos para salvar
	String pastaProdutos = "products/";

	// Salvar produto
	@PostMapping
	@Operation(summary = "Salvar Produto")
	public ResponseEntity<?> insert(@RequestPart("produto") String produtoJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			Produto produto = mapper.readValue(produtoJson, Produto.class);

			String nomeImagem = imagemService.salvarImagem(imagem, pastaProdutos);

			if (nomeImagem != null) {
				produto.setImagem(nomeImagem);
			}

			return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.saveProduto(produto));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// Alterar produto
	@PutMapping("/{id}")
	@Operation(summary = "Alterar Produto")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestPart("produto") String produtoJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			Produto novo = mapper.readValue(produtoJson, Produto.class);

			Produto atual = produtoService.getById(id);

			if (atual == null) {
				return ResponseEntity.notFound().build();
			}

			atual.setNome(novo.getNome());
			atual.setDescricao(novo.getDescricao());
			atual.setPreco(novo.getPreco());
			atual.setValidade(novo.getValidade());
			atual.setMarca(novo.getMarca());
			atual.setCategoria(novo.getCategoria());
			atual.setFornecedor(novo.getFornecedor());

			String imagemAtualizada = imagemService.substituirImagem(atual.getImagem(), imagem, pastaProdutos);

			atual.setImagem(imagemAtualizada);

			return ResponseEntity.ok(produtoService.saveProduto(atual));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// Deletar produto
	@DeleteMapping("/{id}")
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
