package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de sucesso da autenticação")
public record LoginResponse(
    @Schema(description = "Token JWT para autorização nas requisições", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    String token
) {}
