package br.edu.fatecgru.mercado_inteligente.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AutenticacaoService service;

    @Test
    void deveCarregarUsuarioPorEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        
        when(repository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("teste@email.com");

        assertEquals("teste@email.com", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(repository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("naoexiste@email.com"));
    }
}
