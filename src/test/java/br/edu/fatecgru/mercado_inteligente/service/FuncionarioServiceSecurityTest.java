package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioAtualizacaoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import br.edu.fatecgru.mercado_inteligente.repository.FuncionarioRepository;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceSecurityTest {

	@InjectMocks
	private FuncionarioService funcionarioService;

	@Mock
	private FuncionarioRepository funcionarioRepository;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	@Test
	void cadastrar_DeveLancarAccessDenied_QuandoNaoAdminTentaCriarAdmin() {
		// Arrange
		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		// Simula usuário logado como ROLE_ESTOQUISTA usando doReturn para evitar
		// problemas de tipos genéricos
		List<SimpleGrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
		doReturn(authorities).when(authentication).getAuthorities();

		FuncionarioCadastroDTO dto = new FuncionarioCadastroDTO();
		dto.setTipoFuncionario(TipoFuncionario.ADMIN);

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			funcionarioService.cadastrar(dto, null);
		});
	}

	@Test
	void atualizar_DeveLancarAccessDenied_QuandoNaoAdminTentaPromoverParaAdmin() {
		// Arrange
		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		List<SimpleGrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
		doReturn(authorities).when(authentication).getAuthorities();

		FuncionarioAtualizacaoDTO dto = new FuncionarioAtualizacaoDTO();
		dto.setTipoFuncionario(TipoFuncionario.ADMIN);

		// Act & Assert
		assertThrows(AccessDeniedException.class, () -> {
			funcionarioService.atualizar(1L, dto, null);
		});
	}
}
