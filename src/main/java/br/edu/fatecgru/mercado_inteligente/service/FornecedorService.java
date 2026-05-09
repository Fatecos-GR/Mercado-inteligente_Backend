package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;

@Service
public class FornecedorService {

	// Método para listar todos
	@Autowired
	private FornecedorRepository fornecedorRepository;

	public List<Fornecedor> listarTodos() {
		return fornecedorRepository.findAll();
	}

	// Listar pelo ID do fornecedor
	public Fornecedor getById(Long id) {
		return fornecedorRepository.findById(id).orElse(null);
	}

	// Listar produto pelo o nome "contido"
	public List<Fornecedor> getByContainsName(String nome) {
		return fornecedorRepository.findByNomeContains(nome);
	}

}
