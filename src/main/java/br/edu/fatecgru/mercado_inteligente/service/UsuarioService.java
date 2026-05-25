package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioDTO;
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

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	// Listar pelo ID do usuário
	public Usuario getById(Long id) {
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

	// Métodos para cadastrar usuário
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario cadastrar(UsuarioDTO dto) {

		if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new RuntimeException("Email já cadastrado");
		}

		Usuario usuario = new Usuario();

		usuario.setNome(dto.getNome());
		usuario.setSobrenome(dto.getSobrenome());
		usuario.setTelefone(dto.getTelefone());
		usuario.setEmail(dto.getEmail());

		// senha criptografada
		usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

		return usuarioRepository.save(usuario);
	}

	// Método para atualizar usuário
	public Usuario atualizar(Long id, UsuarioDTO dto) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		usuario.setNome(dto.getNome());
		usuario.setSobrenome(dto.getSobrenome());
		usuario.setTelefone(dto.getTelefone());
		usuario.setEmail(dto.getEmail());

		if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
			usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
		}

		return usuarioRepository.save(usuario);
	}

	// Método para excluir usuário
	public void deleteUsuario(Long id) {
		usuarioRepository.deleteById(id);
	}

}
