package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/marcas")

public class MarcaController {

	@Autowired
	private MarcaService marcaService;

	// Lista todas as categorias
	@GetMapping
	public List<Marca> listarTodos() {
		return marcaService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	public Marca buscarPorId(@PathVariable Long id) {
		return marcaService.getById(id);
	}

	// Busca de marca por nome
	@GetMapping("/contem-nome/{nome}")
	public List<Marca> buscarPorContemNome(@PathVariable String nome) {
		return marcaService.getByContainsName(nome);
	}

}
