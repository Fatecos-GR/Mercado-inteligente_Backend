package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dados para realizar um ajuste manual de estoque")
public record EstoqueAjusteDTO(
    @Schema(description = "ID do produto a ser ajustado", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID do produto é obrigatório")
    Long produtoId,
    
    @Schema(description = "Quantidade a ser adicionada ou removida", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    Integer quantidade,
    
    @Schema(description = "Tipo do ajuste: ENTRADA (soma ao disponível) ou SAIDA (subtrai do disponível)", example = "ENTRADA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O tipo de movimentação é obrigatório (ENTRADA ou SAIDA)")
    TipoMovimentacao tipo
) {}
