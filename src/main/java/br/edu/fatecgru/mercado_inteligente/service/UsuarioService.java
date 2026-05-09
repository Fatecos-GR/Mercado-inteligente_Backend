package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@Service
public class UsuarioService {

	// Método para listar todos
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private FuncionarioRepository funcionarioRepository;

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

	// Listar clientes
	public List<Usuario> listarClientes() {

		return usuarioRepository.findAll().stream().filter(usuario -> !(usuario instanceof Funcionario)).toList();
	}

	// Listar administradores
	public List<Funcionario> listarAdministradores() {

		return funcionarioRepository.findByTipoFuncionario(TipoFuncionario.ADMIN);
	}

	// Listar estoquistas
	public List<Funcionario> listarEstoquistas() {

		return funcionarioRepository.findByTipoFuncionario(TipoFuncionario.ESTOQUISTA);
	}

}
