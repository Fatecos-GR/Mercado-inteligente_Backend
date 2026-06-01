package br.edu.fatecgru.mercado_inteligente.mapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;

public class ProdutoMapper {

    public static ProdutoResponseDTO toDTO(Produto produto) {
        if (produto == null) return null;

        return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPreco(),
            produto.getValidade(),
            produto.getImagem(),
            produto.getMarca() != null ? produto.getMarca().getNome() : null,
            produto.getCategoria() != null ? produto.getCategoria().getNome() : null,
            produto.getFornecedor() != null ? produto.getFornecedor().getNome() : null
        );
    }
}