package br.edu.fatecgru.mercado_inteligente.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;

@SpringBootTest
@ActiveProfiles("test")
class TesteManualToken {

    @Autowired
    private TokenService tokenService;

    @Test
    void gerarTokenParaValidarNoSite() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@mercado.com");

        String token = tokenService.gerarToken(usuario);
        
        System.out.println("\n==================================================================");
        System.out.println("COPIE O TOKEN ABAIXO E COLE EM HTTPS://JWT.IO");
        System.out.println("==================================================================");
        System.out.println(token);
        System.out.println("==================================================================\n");
    }
}
