package br.edu.fatecgru.mercado_inteligente.mapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.EstoqueResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.MovimentacaoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.MovimentacaoEstoque;

public class EstoqueMapper {

    public static EstoqueResponseDTO toDTO(Estoque estoque) {
        if (estoque == null) return null;

        return new EstoqueResponseDTO(
            estoque.getId(),
            estoque.getProduto().getId(),
            estoque.getProduto().getNome(),
            estoque.getQuantidadeDisponivel(),
            estoque.getQuantidadeReservada(),
            estoque.getAtualizadoEm()
        );
    }

    public static MovimentacaoResponseDTO toDTO(MovimentacaoEstoque movimentacao) {
        if (movimentacao == null) return null;

        return new MovimentacaoResponseDTO(
            movimentacao.getId(),
            movimentacao.getTipo(),
            movimentacao.getQuantidade(),
            movimentacao.getOrigem(),
            movimentacao.getReferenciaId(),
            movimentacao.getCriadoEm()
        );
    }
}
