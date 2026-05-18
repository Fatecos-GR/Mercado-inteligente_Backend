package br.edu.fatecgru.mercado_inteligente.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@DisplayName("Deve retornar 201 ao registrar usuário válido")
	void registrarUsuarioValido() throws Exception {
		String json = "{\"nome\": \"João\", \"sobrenome\": \"Silva\", \"email\": \"joao.unico@email.com\", \"senha\": \"Senha@123\", \"telefone\": \"11999999999\"}";

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Deve retornar 400 ao registrar com email duplicado")
	void registrarEmailDuplicado() throws Exception {
		String json = "{\"nome\": \"Maria\", \"sobrenome\": \"Teste\", \"email\": \"duplicado@email.com\", \"senha\": \"Senha@123\", \"telefone\": \"11999999999\"}";

		// Primeiro registro
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json));

		// Segundo registro com mesmo email
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Deve retornar 401 ao fazer login com senha errada")
	void loginSenhaErrada() throws Exception {
		String registroJson = "{\"nome\": \"Jose\", \"sobrenome\": \"Silva\", \"email\": \"jose@email.com\", \"senha\": \"Senha@123\", \"telefone\": \"11999999999\"}";
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registroJson));

		String loginErradoJson = "{\"email\": \"jose@email.com\", \"senha\": \"senha-errada\"}";

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginErradoJson))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Deve retornar 400 ao registrar com senha fraca")
	void registrarSenhaFraca() throws Exception {
		// Senha sem caractere especial
		String json = "{\"nome\": \"Maria\", \"sobrenome\": \"Teste\", \"email\": \"fraca@email.com\", \"senha\": \"Senha123\", \"telefone\": \"11999999999\"}";

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Segurança: Deve forçar ROLE_CLIENTE mesmo que o usuário tente enviar ADMIN")
	void forcarRoleCliente() throws Exception {
		// Tentativa maliciosa de se registrar como ADMIN
		String jsonHacker = "{\"nome\": \"Hacker\", \"sobrenome\": \"Malicioso\", \"email\": \"hacker@email.com\", \"senha\": \"Senha@123\", \"telefone\": \"11999999999\", \"tipo\": \"ADMIN\"}";

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(jsonHacker))
				.andExpect(status().isCreated());

		// Agora tentamos fazer login e ver se o token gerado permite acesso de Admin
		// (deve falhar)
		String loginJson = "{\"email\": \"hacker@email.com\", \"senha\": \"Senha@123\"}";
		String response = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson)).andReturn()
				.getResponse().getContentAsString();

		// Extrai o token (simples split para evitar jackson nos testes se preferir)
		String token = response.split("\"")[3];

		// Tenta acessar rota de admin com esse token - Deve dar 403 Forbidden!
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/usuarios")
				.header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
	}
}
