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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            String token = tokenService.gerarToken((Usuario) authentication.getPrincipal());

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException e) {
            log.warn("Tentativa de login falhou para o email: {} em {}", request.email(), LocalDateTime.now());
            throw e; // Permitir que o GlobalExceptionHandler ou Spring Security trate
        }
    }

    @PostMapping("/register")
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
        novoUsuario.setTipo(TipoUsuario.CLIENTE); // TEMPORÁRIO PARA TESTE

        usuarioRepository.save(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }
}