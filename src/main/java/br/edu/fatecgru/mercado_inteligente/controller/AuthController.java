package br.edu.fatecgru.mercado_inteligente.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.LoginRequest;
import br.edu.fatecgru.mercado_inteligente.model.dto.LoginResponse;
import br.edu.fatecgru.mercado_inteligente.model.dto.RegistroRequest;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;
import br.edu.fatecgru.mercado_inteligente.security.service.RateLimitingService;
import br.edu.fatecgru.mercado_inteligente.security.service.TokenService;
import br.edu.fatecgru.mercado_inteligente.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para login e registro de usuários")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private AuthService authService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RateLimitingService rateLimitingService;

	@PostMapping("/login")
	@Operation(summary = "Realiza o login de um usuário e retorna o token JWT")
	public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request, HttpServletRequest servletRequest) {

		String token = authService.login(request, servletRequest.getRemoteAddr());

		return ResponseEntity.ok(new LoginResponse(token));
	}

	@PostMapping("/register")
	@Operation(summary = "Registra um novo usuário no sistema")
	public ResponseEntity<?> register(@RequestBody @Valid RegistroRequest request) {

		authService.registrar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
	}
}
