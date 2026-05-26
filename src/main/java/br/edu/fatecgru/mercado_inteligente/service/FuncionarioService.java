package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

	// Método para listar todos
	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Funcionario> listarTodos() {
		return funcionarioRepository.findAll();
	}

	// Listar pelo ID do funcionário
	public Funcionario getById(Long id) {
		return funcionarioRepository.findById(id).orElse(null);
	}

	// Listar administradores
	public List<Funcionario> listarAdministradores() {

		return funcionarioRepository.findByTipoFuncionario(TipoFuncionario.ADMIN);
	}

	// Listar estoquistas
	public List<Funcionario> listarEstoquistas() {

		return funcionarioRepository.findByTipoFuncionario(TipoFuncionario.ESTOQUISTA);
	}

	// Métodos para cadastrar funcionário
	public Funcionario save(Funcionario funcionario) {
		return funcionarioRepository.save(funcionario);
	}

	public Funcionario cadastrar(FuncionarioDTO dto) {

		if (funcionarioRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new RuntimeException("Email já cadastrado");
		}

		// validação do tipo do funcionário
		if (dto.getTipoFuncionario() == null) {
			throw new RuntimeException("Tipo do funcionário é obrigatório");
		}

		Funcionario funcionario = new Funcionario();

		funcionario.setNome(dto.getNome());
		funcionario.setSobrenome(dto.getSobrenome());
		funcionario.setTelefone(dto.getTelefone());
		funcionario.setEmail(dto.getEmail());
		funcionario.setTipoFuncionario(dto.getTipoFuncionario());

		// senha criptografada
		funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));

		return funcionarioRepository.save(funcionario);
	}

	// Método para atualizar funcionário
	public Funcionario atualizar(Long id, FuncionarioDTO dto) {

		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

		funcionario.setNome(dto.getNome());
		funcionario.setSobrenome(dto.getSobrenome());
		funcionario.setTelefone(dto.getTelefone());
		funcionario.setEmail(dto.getEmail());
		funcionario.setTipoFuncionario(dto.getTipoFuncionario());

		if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
			funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));
		}

		// validação do tipo do funcionário
		if (dto.getTipoFuncionario() == null) {
			throw new RuntimeException("Tipo do funcionário é obrigatório");
		}

		return funcionarioRepository.save(funcionario);
	}

	// Método para excluir funcionário
	public void deleteFuncionario(Long id) {
		funcionarioRepository.deleteById(id);
	}

}
