package br.edu.fatecgru.mercado_inteligente.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "minha-senha-secreta");
    }

    @Test
    void deveGerarTokenValido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertEquals("teste@email.com", tokenService.getSubject(token));
    }

    @Test
    void deveLancarExcecaoParaTokenInvalido() {
        assertThrows(RuntimeException.class, () -> tokenService.getSubject("token-invalido"));
    }
}
