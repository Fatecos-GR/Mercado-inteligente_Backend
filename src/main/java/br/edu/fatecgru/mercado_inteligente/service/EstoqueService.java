package br.edu.fatecgru.mercado_inteligente.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.fatecgru.mercado_inteligente.exception.EstoqueInsuficienteException;
import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.MovimentacaoEstoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.OrigemMovimentacao;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoMovimentacao;
import br.edu.fatecgru.mercado_inteligente.repository.EstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MovimentacaoEstoqueRepository;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Transactional
    public void reservarEstoqueParaCarrinho(Long produtoId, int quantidade, Long carrinhoId) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueInsuficienteException("Produto não possui registro de estoque: " + produtoId));

        if (estoque.getQuantidadeDisponivel() < quantidade) {
            throw new EstoqueInsuficienteException("Estoque insuficiente. Disponível: " + estoque.getQuantidadeDisponivel());
        }

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - quantidade);
        estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() + quantidade);
        
        estoqueRepository.save(estoque);
        
        registrarMovimentacao(estoque, quantidade, TipoMovimentacao.RESERVA, OrigemMovimentacao.CARRINHO, carrinhoId);
    }

    @Transactional
    public void liberarEstoqueDeCarrinho(Long produtoId, int quantidade, Long carrinhoId) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueInsuficienteException("Produto não possui registro de estoque: " + produtoId));

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + quantidade);
        estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - quantidade);
        
        estoqueRepository.save(estoque);

        registrarMovimentacao(estoque, quantidade, TipoMovimentacao.LIBERACAO, OrigemMovimentacao.CARRINHO, carrinhoId);
    }

    private void registrarMovimentacao(Estoque estoque, Integer quantidade, TipoMovimentacao tipo, OrigemMovimentacao origem, Long referenciaId) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEstoque(estoque);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setTipo(tipo);
        movimentacao.setOrigem(origem);
        movimentacao.setReferenciaId(referenciaId);
        movimentacaoEstoqueRepository.save(movimentacao);
    }
}
