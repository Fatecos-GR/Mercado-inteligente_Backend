package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.exception.EmailJaCadastradoException;
import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioAtualizacaoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MovimentacaoEstoqueRepository;

@Service
public class FuncionarioService {

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String pastaFuncionarios = "employees/";

	/**
	 * Valida se o tipo do funcionário foi informado e se o usuário atual tem
	 * permissão para atribuir o cargo de ADMIN. Apenas ADMINs podem criar ou
	 * promover outros para ADMIN.
	 */
	private void validarEscalacaoDePrivilegio(TipoFuncionario tipoPretendido) {
		if (tipoPretendido == null) {
			throw new IllegalArgumentException("O tipo de funcionário (ADMIN ou ESTOQUISTA) é obrigatório.");
		}

		if (tipoPretendido == TipoFuncionario.ADMIN) {
			org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
					.getContext().getAuthentication();
			if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
				throw new org.springframework.security.access.AccessDeniedException(
						"Apenas administradores podem atribuir o cargo de ADMIN.");
			}
		}
	}

	// Método para listar todos
	public List<Funcionario> listarTodos(boolean incluirInativos) {

		if (incluirInativos) {
			return funcionarioRepository.findAllByOrderByNomeAsc();
		}

		return funcionarioRepository.findAllByAtivoOrderByNomeAsc(true);
	}

	// Listar pelo ID do funcionário
	public Funcionario getById(Long id) {
		return funcionarioRepository.findById(id).orElse(null);
	}

	// Listar funcionário pelo o nome completo
	public List<Funcionario> getByNomeCompleto(String nomeCompleto, boolean incluirInativos) {
		return funcionarioRepository.findByNomeCompleto(nomeCompleto).stream()
				.filter(f -> incluirInativos || f.isAtivo()).toList();
	}

	// Listar administradores
	public List<Funcionario> listarAdministradores(boolean incluirInativos) {

		if (incluirInativos) {
			return funcionarioRepository.findByTipoFuncionarioOrderByNomeAsc(TipoFuncionario.ADMIN);
		}

		return funcionarioRepository.findByTipoFuncionarioAndAtivoOrderByNomeAsc(TipoFuncionario.ADMIN, true);
	}

	// Listar estoquistas
	public List<Funcionario> listarEstoquistas(boolean incluirInativos) {

		if (incluirInativos) {
			return funcionarioRepository.findByTipoFuncionarioOrderByNomeAsc(TipoFuncionario.ESTOQUISTA);
		}

		return funcionarioRepository.findByTipoFuncionarioAndAtivoOrderByNomeAsc(TipoFuncionario.ESTOQUISTA, true);
	}

	// Métodos para cadastrar funcionário
	public Funcionario cadastrar(FuncionarioCadastroDTO dto, MultipartFile imagem) throws Exception {
		validarEscalacaoDePrivilegio(dto.getTipoFuncionario());

		if (funcionarioRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new EmailJaCadastradoException(dto.getEmail());
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
	public Funcionario atualizar(Long id, FuncionarioAtualizacaoDTO dto, MultipartFile imagem) throws Exception {
		validarEscalacaoDePrivilegio(dto.getTipoFuncionario());

		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + id));

		// Validação de email duplicado
		Funcionario funcionarioComMesmoEmail = funcionarioRepository.findByEmail(dto.getEmail()).orElse(null);

		if (funcionarioComMesmoEmail != null && !funcionarioComMesmoEmail.getId().equals(id)) {

			throw new EmailJaCadastradoException(dto.getEmail());
		}

		funcionario.setNome(dto.getNome());
		funcionario.setSobrenome(dto.getSobrenome());
		funcionario.setTelefone(dto.getTelefone());
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

	// Método para desativar funcionário (Soft Delete)
	public void deletar(Long id) {
		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com ID: " + id));

		// Valida se o funcionário possui movimentações de estoque vinculadas
		// Nota: Com a desativação (soft delete), poderíamos até permitir desativar
		// mesmo com movimentações, mas manteremos a trava se houver carrinhos ativos
		// (via UsuarioService)

		funcionario.setAtivo(false);

		funcionarioRepository.save(funcionario);
	}
}