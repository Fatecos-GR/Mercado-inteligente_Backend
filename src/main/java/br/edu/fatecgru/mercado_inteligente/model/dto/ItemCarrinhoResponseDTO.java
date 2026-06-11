package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os detalhes de um item no carrinho")
public record ItemCarrinhoResponseDTO(
    @Schema(description = "ID do produto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long produtoId,
    
    @Schema(description = "Nome do produto", example = "Arroz 5kg", requiredMode = Schema.RequiredMode.REQUIRED)
    String produtoNome,
    
    @Schema(description = "Quantidade do item no carrinho", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer quantidade,
    
    @Schema(description = "Preço unitário no momento da adição", example = "25.50", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal precoUnidade,
    
    @Schema(description = "Subtotal do item (quantidade * preço)", example = "51.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal subtotal
) {}
