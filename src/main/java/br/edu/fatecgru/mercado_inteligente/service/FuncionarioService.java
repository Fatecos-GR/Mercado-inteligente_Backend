package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

	// Método para listar todos
	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private void validarEscalacaoDePrivilegio(TipoFuncionario tipoAlvo) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (tipoAlvo == TipoFuncionario.ADMIN && !isAdmin) {
			throw new org.springframework.security.access.AccessDeniedException("Apenas administradores podem criar ou promover outros administradores.");
		}
	}

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
		
		validarEscalacaoDePrivilegio(dto.getTipoFuncionario());

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

		String nomeImagem = imagemService.salvarImagem(imagem, "employees/");

		if (nomeImagem != null) {
			funcionario.setImagem(nomeImagem);
		}

		return funcionarioRepository.save(funcionario);
	}

	// Método para atualizar funcionário
	public Funcionario atualizar(Long id, FuncionarioCadastroDTO dto, MultipartFile imagem) throws Exception {

		validarEscalacaoDePrivilegio(dto.getTipoFuncionario());

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

		String imagemAtualizada = imagemService.substituirImagem(funcionario.getImagem(), imagem, "employees/");

		funcionario.setImagem(imagemAtualizada);

		return funcionarioRepository.save(funcionario);
	}

	// Método para excluir funcionário
	public void deletar(Long id) {
		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

		if (funcionario.getImagem() != null) {
			imagemService.deletarImagem(funcionario.getImagem(), "employees/");
		}

		funcionarioRepository.delete(funcionario);
	}
}