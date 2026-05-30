package br.edu.fatecgru.mercado_inteligente.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;
import br.edu.fatecgru.mercado_inteligente.service.CarrinhoService;

@SpringBootTest
@ActiveProfiles("test")
public class CarrinhoControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CarrinhoService carrinhoService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        usuario.setNome("Test");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    void deveRetornar401AoTentarAcessarSemToken() throws Exception {
        mockMvc.perform(post("/api/carrinhos/itens"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirObterCarrinhoAtivoComUsuarioAutenticado() throws Exception {
        when(carrinhoService.obterCarrinhoAtivo(anyLong())).thenReturn(new Carrinho());

        mockMvc.perform(get("/api/carrinhos")
                .with(user(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirAdicionarItemComUsuarioAutenticado() throws Exception {
        when(carrinhoService.adicionarItem(anyLong(), any(ItemCarrinhoRequest.class))).thenReturn(new Carrinho());

        String json = "{\"produtoId\": 1, \"quantidade\": 2}";

        mockMvc.perform(post("/api/carrinhos/itens")
                .with(user(usuario)) // Injeta o usuário diretamente no contexto do MockMvc
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirAtualizarQuantidadeComUsuarioAutenticado() throws Exception {
        when(carrinhoService.atualizarQuantidade(anyLong(), any(ItemCarrinhoRequest.class))).thenReturn(new Carrinho());

        String json = "{\"produtoId\": 1, \"quantidade\": 3}";

        mockMvc.perform(put("/api/carrinhos/itens")
                .with(user(usuario))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirRemoverItemComUsuarioAutenticado() throws Exception {
        when(carrinhoService.removerItem(anyLong(), anyLong())).thenReturn(new Carrinho());

        mockMvc.perform(delete("/api/carrinhos/itens/1")
                .with(user(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirAbandonarCarrinhoComUsuarioAutenticado() throws Exception {
        when(carrinhoService.abandonarCarrinho(anyLong())).thenReturn(new Carrinho());

        mockMvc.perform(delete("/api/carrinhos")
                .with(user(usuario)))
                .andExpect(status().isOk());
    }
}
