package br.edu.fatecgru.mercado_inteligente.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.dto.LoginRequest;
import br.edu.fatecgru.mercado_inteligente.model.dto.RegistroRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoUsuario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;
import br.edu.fatecgru.mercado_inteligente.security.service.RateLimitingService;
import br.edu.fatecgru.mercado_inteligente.security.service.TokenService;
import io.github.bucket4j.Bucket;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RateLimitingService rateLimitingService;

	@Autowired
	private TokenService tokenService;

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	// Registro de novo usuário
	public void registrar(RegistroRequest request) {

		// Validação
		if (usuarioRepository.findByEmail(request.email()).isPresent()) {
			throw new IllegalArgumentException("Email já está em uso");
		}

		// Criação do Objeto Usuário
		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome(request.nome());
		novoUsuario.setSobrenome(request.sobrenome());
		novoUsuario.setEmail(request.email());
		novoUsuario.setTelefone(request.telefone());

		// Senha criptografada
		novoUsuario.setSenha(passwordEncoder.encode(request.senha()));

		// Criação como cliente --> É cliente quando vem desse endpoint
		novoUsuario.setTipo(TipoUsuario.CLIENTE);

		usuarioRepository.save(novoUsuario);
	}

	// Login do usuário
	public String login(LoginRequest request, String ip) {

		Bucket bucket = rateLimitingService.resolveBucket(ip);

		if (!bucket.tryConsume(1)) {
			throw new IllegalArgumentException("Muitas tentativas de login. Tente novamente em 1 minuto.");
		}

		try {
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					request.email(), request.senha());

			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			return tokenService.gerarToken((Usuario) authentication.getPrincipal());
		} catch (AuthenticationException e) {
			log.warn("Tentativa de login falhou para o email: {} vindo do IP: {} em {}", request.email(), ip,
					LocalDateTime.now());

			throw e;
		}
	}
}
