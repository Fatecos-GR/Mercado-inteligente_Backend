package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequestDTO(
    @Schema(description = "Nome do produto", example = "Arroz 5kg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @Schema(description = "Descrição detalhada do produto", example = "Arroz agulhinha tipo 1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Descrição é obrigatória")
    String descricao,

    @Schema(description = "Preço de venda", example = "25.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    BigDecimal preco,

    @Schema(description = "Data de validade do produto", example = "2026-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Validade é obrigatória")
    @Future(message = "Validade deve ser futura")
    LocalDate validade,

    @Schema(description = "ID da Marca associada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Marca é obrigatória")
    Long marcaId,

    @Schema(description = "ID da Categoria associada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId,

    @Schema(description = "ID do Fornecedor associado", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Fornecedor é obrigatório")
    Long fornecedorId
) {}