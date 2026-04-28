package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.service.ProdutoService;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/produtos")

public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	// Lista todos
	@GetMapping
	public List<Produto> listarTodos() {
		return produtoService.listarTodos();
	}

	// Busca produto por ID
	@GetMapping("/{id}")
	public Produto buscarPorId(@PathVariable int id) {
		return produtoService.getById(id);
	}

	// Busca produto por nome
	@GetMapping("/contem-nome/{nome}")
	public List<Produto> buscarPorContemNome(@PathVariable String nome) {
		return produtoService.getByContainsName(nome);
	}

	// Busca por ID da categoria
	@GetMapping("/categoria/{id}")
	public List<Produto> buscarPorIdCategoria(@PathVariable int id) {
		return produtoService.getByCategoryId(id);
	}

	// Busca por ID da marca
	@GetMapping("/marca/{id}")
	public List<Produto> buscarPorIdMarca(@PathVariable int id) {
		return produtoService.getByBrandId(id);
	}

}
