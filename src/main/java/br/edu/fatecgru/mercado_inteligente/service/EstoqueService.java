package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.fatecgru.mercado_inteligente.exception.EstoqueInsuficienteException;
import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
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

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }

    public Estoque buscarPorProdutoId(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o produto ID: " + produtoId));
    }

    public List<MovimentacaoEstoque> buscarMovimentacoes(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return movimentacaoEstoqueRepository.findByEstoqueIdOrderByCriadoEmDesc(estoque.getId());
    }

    @Transactional
    public void executarAjuste(Long produtoId, Integer quantidade, TipoMovimentacao tipo, Long usuarioId) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o produto ID: " + produtoId));

        if (tipo == TipoMovimentacao.SAIDA) {
            if (estoque.getQuantidadeDisponivel() < quantidade) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para a saída. Disponível: " + estoque.getQuantidadeDisponivel());
            }
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - quantidade);
        } else if (tipo == TipoMovimentacao.ENTRADA) {
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + quantidade);
        } else {
            throw new IllegalArgumentException("Tipo de movimentação inválido para ajuste manual: " + tipo);
        }

        estoqueRepository.save(estoque);
        registrarMovimentacao(estoque, quantidade, tipo, OrigemMovimentacao.AJUSTE, usuarioId);
    }

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

        if (estoque.getQuantidadeReservada() < quantidade) {
            // Se tentar liberar mais do que o reservado, apenas zeramos para evitar estoque negativo
            quantidade = estoque.getQuantidadeReservada();
        }

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + quantidade);
        estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - quantidade);
        
        estoqueRepository.save(estoque);

        if (quantidade > 0) {
            registrarMovimentacao(estoque, quantidade, TipoMovimentacao.LIBERACAO, OrigemMovimentacao.CARRINHO, carrinhoId);
        }
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
