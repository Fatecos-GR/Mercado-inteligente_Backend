package br.edu.fatecgru.mercado_inteligente.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.ErrorResponse;
import br.edu.fatecgru.mercado_inteligente.model.dto.LoginRequest;
import br.edu.fatecgru.mercado_inteligente.model.dto.LoginResponse;
import br.edu.fatecgru.mercado_inteligente.model.dto.RegistroRequest;
import br.edu.fatecgru.mercado_inteligente.model.dto.RegistroResponse;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;
import br.edu.fatecgru.mercado_inteligente.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e registro de usuários")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@PostMapping("/login")
	@Operation(summary = "Realiza o login de um usuário e retorna o token JWT")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Credenciais inválidas") })
	public ResponseEntity<LoginResponse> login(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciais de login", required = true) @RequestBody @Valid LoginRequest request,
			HttpServletRequest servletRequest) {

		LoginResponse response = authService.login(request, servletRequest.getRemoteAddr());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	@Operation(summary = "Registra um novo usuário no sistema")
	@ApiResponses(value = {

			@ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),

			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),

			@ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<RegistroResponse> register(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para registro de novo usuário", required = true) @RequestBody @Valid RegistroRequest request) {

		RegistroResponse response = authService.registrar(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
