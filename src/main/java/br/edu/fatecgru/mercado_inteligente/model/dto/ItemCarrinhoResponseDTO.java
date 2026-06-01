package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;

public record ItemCarrinhoResponseDTO(
    Long produtoId,
    String produtoNome,
    Integer quantidade,
    BigDecimal precoUnidade,
    BigDecimal subtotal
) {}
