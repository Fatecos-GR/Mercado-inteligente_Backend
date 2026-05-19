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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints relacionados a categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	// Lista todas as categorias
	@GetMapping
	@Operation(summary = "Listar todas as categorias")
	public List<Categoria> listarTodos() {
		return categoriaService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por ID")
	public Categoria buscarPorId(@PathVariable Long id) {
		return categoriaService.getById(id);
	}

	// Busca de categoria pelo nome
	@GetMapping("/contem-nome/{nome}")
	@Operation(summary = "Buscar por nome")
	public List<Categoria> buscarPorContemNome(@PathVariable String nome) {
		return categoriaService.getByContainsName(nome);
	}

}
