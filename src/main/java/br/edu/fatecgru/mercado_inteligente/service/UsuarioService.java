package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.exception.EmailJaCadastradoException;
import br.edu.fatecgru.mercado_inteligente.model.dto.AlterarSenhaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioAtualizacaoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.CarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.EnderecoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@Service
public class UsuarioService {

	// Método para listar todos
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private EnderecoService enderecoService;

	@Autowired
	private CarrinhoRepository carrinhoRepository;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String pastaUsuarios = "users/";

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	// Listar pelo ID do usuário
	public List<Usuario> listarTodos(boolean incluirInativos) {
		if (incluirInativos) {
			return usuarioRepository.findAll();
		}
		return usuarioRepository.findAllByAtivo(true);
	}

	// Listar pelo ID do usuário
	public Usuario getById(Long id) {
		return usuarioRepository.findById(id).orElse(null);
	}

	// Listar usuário pelo nome completo (nome + sobrenome)
	public List<Usuario> getByNomeCompleto(String nomeCompleto, boolean incluirInativos) {
		if (incluirInativos) {
			return usuarioRepository.buscarPorNomeCompleto(nomeCompleto);
		}
		return usuarioRepository.buscarAtivosPorNomeCompleto(nomeCompleto);
	}

	// Listar clientes
	public List<Usuario> listarClientes(boolean incluirInativos) {
		List<Usuario> base = incluirInativos ? usuarioRepository.findAll() : usuarioRepository.findAllByAtivo(true);
		return base.stream().filter(usuario -> !(usuario instanceof Funcionario)).toList();
	}

	// Métodos para cadastrar usuário
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario cadastrar(UsuarioCadastroDTO dto, MultipartFile imagem) throws Exception {

		// Verificar se email já existe
		if (usuarioRepository.existsByEmail(dto.getEmail())) {
			throw new EmailJaCadastradoException(dto.getEmail());
		}

		Usuario usuario = new Usuario();

		usuario.setNome(dto.getNome());
		usuario.setSobrenome(dto.getSobrenome());
		usuario.setTelefone(dto.getTelefone());
		usuario.setEmail(dto.getEmail());

		// senha criptografada
		usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaUsuarios);

		if (imagemDTO != null) {
			usuario.setImagem(imagemDTO.getUrl());
			usuario.setPublicIdImagem(imagemDTO.getPublicId());
		}

		return usuarioRepository.save(usuario);
	}

	// Método para atualizar usuário
	public Usuario atualizar(Long id, UsuarioAtualizacaoDTO dto, MultipartFile imagem) throws Exception {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// Verifica se outro usuário já possui esse email
		Usuario usuarioComMesmoEmail = usuarioRepository.findByEmail(dto.getEmail()).orElse(null);

		if (usuarioComMesmoEmail != null && !usuarioComMesmoEmail.getId().equals(id)) {

			throw new EmailJaCadastradoException(dto.getEmail());
		}

		usuario.setNome(dto.getNome());
		usuario.setSobrenome(dto.getSobrenome());
		usuario.setTelefone(dto.getTelefone());
		usuario.setEmail(dto.getEmail());

		ImagemDTO novaImagem = imagemService.substituirImagem(usuario.getPublicIdImagem(), imagem, pastaUsuarios);

		if (novaImagem != null) {
			usuario.setImagem(novaImagem.getUrl());
			usuario.setPublicIdImagem(novaImagem.getPublicId());
		}

		return usuarioRepository.save(usuario);
	}

	// Método para desativar usuário (Soft Delete)
	public void deletar(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// Valida se o usuário possui carrinho ativo
		if (carrinhoRepository.findByUsuarioIdAndStatus(id, StatusCarrinho.ATIVO).isPresent()) {
			throw new IllegalStateException("O usuário não pode ser desativado pois possui um carrinho ativo.");
		}

		// Em vez de deletar fisicamente, desativamos o usuário para preservar o histórico
		usuario.setAtivo(false);

		usuarioRepository.save(usuario);
	}

	// Método para alterar senha
	public void alterarSenha(Long id, AlterarSenhaDTO dto) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// Valida senha atual
		if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
			throw new IllegalArgumentException("A senha atual está incorreta.");
		}

		if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
			throw new RuntimeException("As senhas não coincidem");
		}

		usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

		usuarioRepository.save(usuario);
	}

	// Listar endereços por usuário
	public List<Endereco> listarEnderecosUsuario(Long usuarioId) {

		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		return usuario.getEnderecos();
	}

	// Adicionar endereço a um usuário
	public Endereco adicionarEndereco(Long usuarioId, EnderecoDTO dto) {

		// Validação de Endereço Único
		enderecoService.validarEnderecoUnico(dto, null);

		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		Endereco endereco = new Endereco();

		endereco.setCep(dto.cep());
		endereco.setLogradouro(dto.logradouro());
		endereco.setNumero(dto.numero());
		endereco.setComplemento(dto.complemento());
		endereco.setBairro(dto.bairro());
		endereco.setCidade(dto.cidade());
		endereco.setEstado(dto.estado());

		endereco.setUsuario(usuario);

		enderecoRepository.save(endereco);

		return endereco;
	}

}