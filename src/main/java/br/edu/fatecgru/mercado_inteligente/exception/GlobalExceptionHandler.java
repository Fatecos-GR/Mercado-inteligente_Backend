package br.edu.fatecgru.mercado_inteligente.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.edu.fatecgru.mercado_inteligente.model.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<ErrorResponse.ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(f -> new ErrorResponse.ValidationError(f.getField(), f.getDefaultMessage()))
				.collect(Collectors.toList());

		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
				"Erro de validação nos campos enviados", LocalDateTime.now(), errors);

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now(),
				null);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(EstoqueInsuficienteException.class)
	public ResponseEntity<ErrorResponse> handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now(),
				null);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationError(AuthenticationException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
				"Falha na autenticação: " + ex.getMessage(), LocalDateTime.now(), null);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedError(AccessDeniedException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
				"Acesso negado: você não tem permissão para acessar este recurso.", LocalDateTime.now(), null);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now(),
				null);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now(),
				null);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {

		ex.printStackTrace(); // imprime o erro completo no console

		ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
				LocalDateTime.now(), null);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ErrorResponse> handleEmailJaCadastrado(EmailJaCadastradoException ex) {

		ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now(),
				null);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

}
