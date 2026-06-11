package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padrão de resposta de erro da API")
public record ErrorResponse(
    @Schema(description = "Código HTTP do erro", example = "400", requiredMode = Schema.RequiredMode.REQUIRED)
    int status,
    
    @Schema(description = "Mensagem geral do erro", example = "Bad Request", requiredMode = Schema.RequiredMode.REQUIRED)
    String erro,
    
    @Schema(description = "Data e hora em que o erro ocorreu", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime timestamp,
    
    @Schema(description = "Lista detalhada de erros de validação (quando aplicável)")
    List<ValidationError> erros
) {
    @Schema(description = "Detalhe de um erro de validação em um campo específico")
    public record ValidationError(
        @Schema(description = "Nome do campo que falhou na validação", example = "email")
        String campo,
        
        @Schema(description = "Mensagem descrevendo o motivo da falha", example = "Email inválido")
        String mensagem
    ) {}
}
