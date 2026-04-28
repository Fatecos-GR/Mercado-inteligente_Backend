package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@Service
public class UsuarioService {

	// Método para listar todos
	@Autowired
	private UsuarioRepository usuarioRepository;

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	// Listar pelo ID do usuário
	public Usuario getById(int id) {
		return usuarioRepository.findById(id).orElse(null);
	}

	// Listar produto pelo o nome "contido"
	public List<Usuario> getByContainsName(String nome) {
		return usuarioRepository.findByNomeContains(nome);
	}

}
