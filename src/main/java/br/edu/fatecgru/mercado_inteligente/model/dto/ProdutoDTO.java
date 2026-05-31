package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoDTO(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Descrição é obrigatória")
    String descricao,

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    BigDecimal preco,

    @NotNull(message = "Validade é obrigatória")
    @Future(message = "Validade deve ser futura")
    LocalDate validade,

    @NotNull(message = "Marca é obrigatória")
    Long marcaId,

    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId,

    @NotNull(message = "Fornecedor é obrigatório")
    Long fornecedorId
) {}