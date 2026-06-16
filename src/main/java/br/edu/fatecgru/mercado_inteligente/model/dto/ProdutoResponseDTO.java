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

		String publicIdImagem,

		Long marcaId, String marcaNome,

		Long categoriaId, String categoriaNome,

		Long fornecedorId, String fornecedorNome,

		Integer estoqueDisponivel

) {
}