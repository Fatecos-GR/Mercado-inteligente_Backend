package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

	// Método para listar todos
	@Autowired
	private FuncionarioRepository funcionarioRepository;

	public List<Funcionario> listarTodos() {
		return funcionarioRepository.findAll();
	}

	// Listar pelo ID do funcionário
	public Funcionario getById(int id) {
		return funcionarioRepository.findById(id).orElse(null);
	}

}
