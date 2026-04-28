package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/categorias")

public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	// Lista todas as categorias
	@GetMapping
	public List<Categoria> listarTodos() {
		return categoriaService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	public Categoria buscarPorId(@PathVariable int id) {
		return categoriaService.getById(id);
	}

	// Busca de categoria pelo nome
	@GetMapping("/contem-nome/{nome}")
	public List<Categoria> buscarPorContemNome(@PathVariable String nome) {
		return categoriaService.getByContainsName(nome);
	}

}
