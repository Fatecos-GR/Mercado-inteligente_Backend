package br.edu.fatecgru.mercado_inteligente.security.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.fatecgru.mercado_inteligente.security.service.AutenticacaoService;
import br.edu.fatecgru.mercado_inteligente.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private AutenticacaoService autenticacaoService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String tokenJWT = recuperarToken(request);

		if (tokenJWT != null) {
			try {
				String subject = tokenService.getSubject(tokenJWT);
				UserDetails usuario = autenticacaoService.loadUserByUsername(subject);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario,
						null, usuario.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (RuntimeException e) {
				// Token inválido ou expirado, não setamos o contexto de autenticação.
				// O Spring Security retornará 401 automaticamente para rotas protegidas.
			}
		}

		filterChain.doFilter(request, response);
	}

	private String recuperarToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null) {
			return authorizationHeader.replace("Bearer ", "");
		}
		return null;
	}

}
