package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoMovimentacao;
import br.edu.fatecgru.mercado_inteligente.model.entity.OrigemMovimentacao;

@Schema(description = "Registro de uma movimentação de estoque (histórico)")
public record MovimentacaoResponseDTO(
    @Schema(description = "ID da movimentação", example = "1")
    Long id,
    
    @Schema(description = "Tipo da operação (ENTRADA, SAIDA, RESERVA, LIBERACAO)", example = "ENTRADA")
    TipoMovimentacao tipo,
    
    @Schema(description = "Quantidade movimentada", example = "10")
    Integer quantidade,
    
    @Schema(description = "Origem da movimentação (CARRINHO ou AJUSTE)", example = "AJUSTE")
    OrigemMovimentacao origem,
    
    @Schema(description = "ID de referência (ID do carrinho ou do usuário que fez o ajuste)", example = "100")
    Long referenciaId,
    
    @Schema(description = "Data e hora em que a movimentação ocorreu")
    LocalDateTime criadoEm
) {}
