package br.edu.fatecgru.mercado_inteligente.security.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.edu.fatecgru.mercado_inteligente.security.filter.SecurityFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {

	@Autowired
	private SecurityFilter securityFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(req -> {
					req.requestMatchers("/api/auth/**").permitAll();
					req.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
					req.requestMatchers("/error").permitAll();
					
					// Recursos Estáticos (Imagens)
					req.requestMatchers(HttpMethod.GET, "/uploads/**").permitAll();
					
					// Vitrine Pública (Apenas Leitura)
					req.requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll();
					req.requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll();
					req.requestMatchers(HttpMethod.GET, "/api/marcas/**").permitAll();
					req.requestMatchers(HttpMethod.GET, "/api/enderecos/cep/**").permitAll();
					
					req.anyRequest().authenticated();
				})
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(ex -> ex
					.authenticationEntryPoint((request, response, authException) -> {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						response.getWriter().write(
								"{\"status\": 401, \"erro\": \"Não autorizado: você precisa de um token válido.\", \"timestamp\": \""
										+ java.time.LocalDateTime.now() + "\"}");
					})
					.accessDeniedHandler((request, response, accessDeniedException) -> {
						// Se o usuário não está autenticado, manda 401 em vez de 403
						org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
						if (auth == null || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							response.setCharacterEncoding("UTF-8");
							response.getWriter().write(
									"{\"status\": 401, \"erro\": \"Não autorizado: você precisa de um token válido.\", \"timestamp\": \""
											+ java.time.LocalDateTime.now() + "\"}");
						} else {
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.setContentType("application/json");
							response.setCharacterEncoding("UTF-8");
							response.getWriter().write(
									"{\"status\": 403, \"erro\": \"Acesso negado: você não tem permissão para este recurso.\", \"timestamp\": \""
											+ java.time.LocalDateTime.now() + "\"}");
						}
					})
				).build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*")); // Em produção, mude "*" para os domínios reais
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
