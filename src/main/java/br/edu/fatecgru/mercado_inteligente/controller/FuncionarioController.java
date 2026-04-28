package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.service.FuncionarioService;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

	@Autowired
	private FuncionarioService funcionarioService;

	// Lista todas os funcionários
	@GetMapping
	public List<Funcionario> listarTodos() {
		return funcionarioService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	public Funcionario buscarPorId(@PathVariable int id) {
		return funcionarioService.getById(id);
	}

}
