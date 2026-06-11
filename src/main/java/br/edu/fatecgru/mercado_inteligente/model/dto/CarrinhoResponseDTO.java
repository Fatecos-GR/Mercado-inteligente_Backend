package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os detalhes do carrinho de compras")
public record CarrinhoResponseDTO(
    @Schema(description = "ID do carrinho", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long id,
    
    @Schema(description = "Lista de itens no carrinho", requiredMode = Schema.RequiredMode.REQUIRED)
    List<ItemCarrinhoResponseDTO> itens,
    
    @Schema(description = "Valor total do carrinho", example = "150.50", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal valorTotal,
    
    @Schema(description = "Data e hora de criação", example = "2023-10-27T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime criadoEm,
    
    @Schema(description = "Data e hora da última atualização", example = "2023-10-27T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime atualizadoEm,
    
    @Schema(description = "Status atual do carrinho", example = "ABERTO", requiredMode = Schema.RequiredMode.REQUIRED)
    StatusCarrinho status
) {}
