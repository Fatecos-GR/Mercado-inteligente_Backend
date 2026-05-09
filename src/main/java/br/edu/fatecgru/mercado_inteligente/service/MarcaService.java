package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.repository.MarcaRepository;

@Service
public class MarcaService {

	// Método de listar todos
	@Autowired
	private MarcaRepository marcaRepository;

	public List<Marca> listarTodos() {
		return marcaRepository.findAll();
	}

	// Listar por marca que contém no nome
	public List<Marca> getByContainsName(String nome) {
		return marcaRepository.findByNomeContains(nome);
	}

	// Consulta por ID da marca
	public Marca getById(Long id) {
		return marcaRepository.findById(id).orElse(null);
	}

}
