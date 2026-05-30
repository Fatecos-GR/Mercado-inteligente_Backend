package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;

public record CarrinhoResponseDTO(
    Integer id,
    List<ItemCarrinhoResponseDTO> itens,
    BigDecimal valorTotal,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm,
    StatusCarrinho status
) {}
