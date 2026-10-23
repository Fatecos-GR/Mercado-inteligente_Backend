package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta detalhada do estado atual do estoque")
public record EstoqueResponseDTO(
    @Schema(description = "ID do registro de estoque", example = "1")
    Long id,
    
    @Schema(description = "ID do produto vinculado", example = "10")
    Long produtoId,
    
    @Schema(description = "Nome do produto", example = "Arroz 5kg")
    String produtoNome,
    
    @Schema(description = "Quantidade disponível para venda imediata", example = "50")
    Integer quantidadeDisponivel,
    
    @Schema(description = "Quantidade reservada em carrinhos ativos", example = "5")
    Integer quantidadeReservada,
    
    @Schema(description = "Data e hora da última atualização de saldo")
    LocalDateTime atualizadoEm
) {}
