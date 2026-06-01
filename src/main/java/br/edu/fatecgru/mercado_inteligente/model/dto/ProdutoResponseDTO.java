package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    LocalDate validade,
    String imagem,
    String marcaNome,
    String categoriaNome,
    String fornecedorNome
) {}