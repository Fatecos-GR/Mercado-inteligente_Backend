package br.edu.fatecgru.mercado_inteligente.security.filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import br.edu.fatecgru.mercado_inteligente.security.service.AutenticacaoService;
import br.edu.fatecgru.mercado_inteligente.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

	@Mock
	private TokenService tokenService;

	@Mock
	private AutenticacaoService autenticacaoService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private UserDetails userDetails;

	@InjectMocks
	private SecurityFilter securityFilter;

	@Test
	void deveAutenticarQuandoTokenForValido() throws ServletException, IOException {
		// Arrange
		SecurityContextHolder.clearContext();
		String token = "token-valido";
		String email = "teste@email.com";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(tokenService.getSubject(token)).thenReturn(email);
		when(autenticacaoService.loadUserByUsername(email)).thenReturn(userDetails);

		// Act
		securityFilter.doFilterInternal(request, response, filterChain);

		// Assert
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void naoDeveAutenticarQuandoTokenForInexistente() throws ServletException, IOException {
		// Arrange
		SecurityContextHolder.clearContext();
		when(request.getHeader("Authorization")).thenReturn(null);

		// Act
		securityFilter.doFilterInternal(request, response, filterChain);

		// Assert
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(filterChain).doFilter(request, response);
	}
}
