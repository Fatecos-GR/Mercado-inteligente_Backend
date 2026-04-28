package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	// Listar todos
	@GetMapping
	public List<Usuario> listarTodos() {
		return usuarioService.listarTodos();
	}

	// Busca pelo ID
	@GetMapping("/{id}")
	public Usuario buscarPorId(@PathVariable int id) {
		return usuarioService.getById(id);
	}

	// Busca usuário por nome
	@GetMapping("/contem-nome/{nome}")
	public List<Usuario> buscarPorContemNome(@PathVariable String nome) {
		return usuarioService.getByContainsName(nome);
	}

}
