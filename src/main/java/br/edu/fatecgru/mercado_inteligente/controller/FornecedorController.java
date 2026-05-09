package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/fornecedores")
public class FornecedorController {

	@Autowired
	private FornecedorService fornecedorService;

	// Lista todas os fornecedores
	@GetMapping
	public List<Fornecedor> listarTodos() {
		return fornecedorService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	public Fornecedor buscarPorId(@PathVariable Long id) {
		return fornecedorService.getById(id);
	}

	// Busca de fornecedor pelo nome
	@GetMapping("/contem-nome/{nome}")
	public List<Fornecedor> buscarPorContemNome(@PathVariable String nome) {
		return fornecedorService.getByContainsName(nome);
	}

}
