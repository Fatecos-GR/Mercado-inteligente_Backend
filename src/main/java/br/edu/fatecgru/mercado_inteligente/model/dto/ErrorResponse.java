package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(int status, String erro, LocalDateTime timestamp, List<ValidationError> erros) {
	public record ValidationError(String campo, String mensagem) {
	}
}
