package br.edu.fatecgru.mercado_inteligente.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.LoginRequest;
import br.edu.fatecgru.mercado_inteligente.model.dto.LoginResponse;
import br.edu.fatecgru.mercado_inteligente.model.dto.RegistroRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoUsuario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;
import br.edu.fatecgru.mercado_inteligente.security.service.TokenService;
import br.edu.fatecgru.mercado_inteligente.security.service.RateLimitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.bucket4j.Bucket;

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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RateLimitingService rateLimitingService;

    @PostMapping("/login")
    @Operation(summary = "Realiza o login de um usuário e retorna o token JWT")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request, HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        Bucket bucket = rateLimitingService.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            try {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                String token = tokenService.gerarToken((Usuario) authentication.getPrincipal());

                return ResponseEntity.ok(new LoginResponse(token));
            } catch (AuthenticationException e) {
                log.warn("Tentativa de login falhou para o email: {} vindo do IP: {} em {}", request.email(), ip, LocalDateTime.now());
                throw e; 
            }
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Muitas tentativas de login. Tente novamente em 1 minuto.");
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário no sistema")
    public ResponseEntity<?> register(@RequestBody @Valid RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Email já está em uso!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.nome());
        novoUsuario.setSobrenome(request.sobrenome());
        novoUsuario.setEmail(request.email());
        novoUsuario.setTelefone(request.telefone());
        novoUsuario.setSenha(passwordEncoder.encode(request.senha()));
        novoUsuario.setTipo(TipoUsuario.CLIENTE); 

        usuarioRepository.save(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }
}
