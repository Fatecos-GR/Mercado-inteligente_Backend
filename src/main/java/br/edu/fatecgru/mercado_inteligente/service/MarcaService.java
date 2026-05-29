package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO;
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

	// salvar
	public Marca save(Marca marca) {
		return marcaRepository.save(marca);
	}

	// cadastrar
	public Marca cadastrar(MarcaDTO dto) {

		Marca marca = new Marca();

		marca.setNome(dto.getNome());
		marca.setDescricao(dto.getDescricao());

		return marcaRepository.save(marca);
	}

	// atualizar
	public Marca atualizar(Long id, MarcaDTO dto) {

		Marca marca = marcaRepository.findById(id).orElseThrow(() -> new RuntimeException("Marca não encontrada"));

		marca.setNome(dto.getNome());
		marca.setDescricao(dto.getDescricao());

		return marcaRepository.save(marca);
	}

	// deletar
	public void delete(Long id) {
		marcaRepository.deleteById(id);
	}

}
