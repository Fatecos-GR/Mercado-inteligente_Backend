package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO de entrada para adicionar/atualizar item no carrinho")
public record ItemCarrinhoRequest(
    @Schema(description = "ID do produto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID do produto é obrigatório")
    Long produtoId,
    
    @Schema(description = "Quantidade desejada", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima deve ser 1")
    Integer quantidade
) {}
