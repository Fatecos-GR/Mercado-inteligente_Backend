package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta detalhada do estado atual do estoque")
public record EstoqueResponseDTO(
    @Schema(description = "ID do registro de estoque", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long id,
    
    @Schema(description = "ID do produto vinculado", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    Long produtoId,
    
    @Schema(description = "Nome do produto", example = "Arroz 5kg", requiredMode = Schema.RequiredMode.REQUIRED)
    String produtoNome,
    
    @Schema(description = "Quantidade disponível para venda imediata", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer quantidadeDisponivel,
    
    @Schema(description = "Quantidade reservada em carrinhos ativos", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer quantidadeReservada,
    
    @Schema(description = "Data e hora da última atualização de saldo", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime atualizadoEm
) {}
