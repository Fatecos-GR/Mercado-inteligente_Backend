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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Endpoints relacionados aos Fornecedores")
public class FornecedorController {

	@Autowired
	private FornecedorService fornecedorService;

	// Lista todas os fornecedores
	@GetMapping
	@Operation(summary = "Listar todos os fornecedores")
	public List<Fornecedor> listarTodos() {
		return fornecedorService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
	public Fornecedor buscarPorId(@PathVariable Long id) {
		return fornecedorService.getById(id);
	}

	// Busca de fornecedor pelo nome
	@GetMapping("/contem-nome/{nome}")
	@Operation(summary = "Buscar fornecedor por nome")
	public List<Fornecedor> buscarPorContemNome(@PathVariable String nome) {
		return fornecedorService.getByContainsName(nome);
	}
}
