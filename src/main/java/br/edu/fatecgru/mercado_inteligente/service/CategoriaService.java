package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.repository.CategoriaRepository;

@Service
public class CategoriaService {

	// Método de listar todos
	@Autowired
	private CategoriaRepository categoriaRepository;

	public List<Categoria> listarTodos() {
		return categoriaRepository.findAll();
	}

	// Consulta por categoria que contém no nome
	public List<Categoria> getByContainsName(String nome) {
		return categoriaRepository.findByNomeContains(nome);
	}

	// Consulta por id categoria
	public Categoria getById(int id) {
		return categoriaRepository.findById(id).orElse(null);
	}

}
