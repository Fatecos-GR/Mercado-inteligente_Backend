package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String pastaFuncionarios = "employees/";

	// Método para listar todos
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
	public Funcionario cadastrar(FuncionarioCadastroDTO dto, MultipartFile imagem) throws Exception {

		if (funcionarioRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new RuntimeException("Email já cadastrado");
		}

		Funcionario funcionario = new Funcionario();

		funcionario.setNome(dto.getNome());
		funcionario.setSobrenome(dto.getSobrenome());
		funcionario.setTelefone(dto.getTelefone());
		funcionario.setEmail(dto.getEmail());
		funcionario.setTipoFuncionario(dto.getTipoFuncionario());

		// senha criptografada
		funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaFuncionarios);

		if (imagemDTO != null) {
			funcionario.setImagem(imagemDTO.getUrl());
			funcionario.setPublicIdImagem(imagemDTO.getPublicId());
		}

		return funcionarioRepository.save(funcionario);
	}

	// Método para atualizar funcionário
	public Funcionario atualizar(Long id, FuncionarioCadastroDTO dto, MultipartFile imagem) throws Exception {

		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

		funcionario.setNome(dto.getNome());
		funcionario.setSobrenome(dto.getSobrenome());
		funcionario.setTelefone(dto.getTelefone());

		// Validação de email duplicado
		Funcionario funcionarioComMesmoEmail = funcionarioRepository.findByEmail(dto.getEmail()).orElse(null);

		if (funcionarioComMesmoEmail != null && !funcionarioComMesmoEmail.getId().equals(id)) {

			throw new RuntimeException("Email já cadastrado");
		}

		funcionario.setEmail(dto.getEmail());
		funcionario.setTipoFuncionario(dto.getTipoFuncionario());

		if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
			funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));
		}

		ImagemDTO novaImagem = imagemService.substituirImagem(funcionario.getPublicIdImagem(), imagem,
				pastaFuncionarios);

		if (novaImagem != null) {
			funcionario.setImagem(novaImagem.getUrl());
			funcionario.setPublicIdImagem(novaImagem.getPublicId());
		}

		return funcionarioRepository.save(funcionario);
	}

	// Método para excluir funcionário
	public void deletar(Long id) {
		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

		if (funcionario.getPublicIdImagem() != null) {
			imagemService.deletarImagem(funcionario.getPublicIdImagem());
		}

		funcionarioRepository.delete(funcionario);
	}
}