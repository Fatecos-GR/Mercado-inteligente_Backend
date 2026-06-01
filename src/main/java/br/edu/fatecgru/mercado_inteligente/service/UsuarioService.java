package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.AlterarSenhaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioAtualizacaoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
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
	private ImagemService imagemService;

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

	// Métodos para cadastrar usuário
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario cadastrar(UsuarioCadastroDTO dto, MultipartFile imagem) throws Exception {

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

		String nomeImagem = imagemService.salvarImagem(imagem, "users/");

		if (nomeImagem != null) {
			usuario.setImagem(nomeImagem);
		}

		return usuarioRepository.save(usuario);
	}

	// Método para atualizar usuário
	public Usuario atualizar(Long id, UsuarioAtualizacaoDTO dto) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		usuario.setNome(dto.getNome());
		usuario.setSobrenome(dto.getSobrenome());
		usuario.setTelefone(dto.getTelefone());
		usuario.setEmail(dto.getEmail());

		return usuarioRepository.save(usuario);
	}

	// Método para excluir usuário
	public void deletar(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		if (usuario.getImagem() != null) {
			imagemService.deletarImagem(usuario.getImagem(), "users/");
		}

		usuarioRepository.delete(usuario);
	}

	// Método para alterar senha
	public void alterarSenha(Long id, AlterarSenhaDTO dto) {

		if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
			throw new RuntimeException("As senhas não coincidem");
		}

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

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