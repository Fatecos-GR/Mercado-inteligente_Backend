package br.edu.fatecgru.mercado_inteligente.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class EstoqueControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar listar estoque como CLIENTE")
    @WithMockUser(roles = "CLIENTE")
    void listarEstoqueComoCliente() throws Exception {
        mockMvc.perform(get("/api/estoque")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 200 ao listar estoque como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void listarEstoqueComoAdmin() throws Exception {
        mockMvc.perform(get("/api/estoque")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 200 ao listar estoque como ESTOQUISTA")
    @WithMockUser(roles = "ESTOQUISTA")
    void listarEstoqueComoEstoquista() throws Exception {
        mockMvc.perform(get("/api/estoque")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar ajustar estoque como CLIENTE")
    @WithMockUser(roles = "CLIENTE")
    void ajustarEstoqueComoCliente() throws Exception {
        String json = "{\"produtoId\":1, \"quantidade\":10, \"tipo\":\"ENTRADA\"}";
        mockMvc.perform(post("/api/estoque/ajuste")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar acessar sem autenticação")
    void acessoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/estoque")).andExpect(status().isUnauthorized());
    }
}
