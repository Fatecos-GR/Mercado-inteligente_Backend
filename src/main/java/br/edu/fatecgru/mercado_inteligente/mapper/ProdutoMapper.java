package br.edu.fatecgru.mercado_inteligente.mapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;

public class ProdutoMapper {

	public static ProdutoResponseDTO toDTO(Produto produto) {

		if (produto == null) {
			return null;
		}

		return new ProdutoResponseDTO(

				produto.getId(),

				produto.getNome(),

				produto.getDescricao(),

				produto.getPreco(),

				produto.getPrecoAnterior(),

				produto.getDataReducaoPreco(),

				produto.getValidade(),

				produto.getImagem(),

				produto.getPublicIdImagem(),

				// Marca
				produto.getMarca() != null ? produto.getMarca().getId() : null,
				produto.getMarca() != null ? produto.getMarca().getNome() : null,

				// Categoria
				produto.getCategoria() != null ? produto.getCategoria().getId() : null,
				produto.getCategoria() != null ? produto.getCategoria().getNome() : null,

				// Fornecedor
				produto.getFornecedor() != null ? produto.getFornecedor().getId() : null,
				produto.getFornecedor() != null ? produto.getFornecedor().getNome() : null,

				// Estoque
				produto.getEstoque() != null ? produto.getEstoque().getQuantidadeDisponivel() : 0

		);
	}
}