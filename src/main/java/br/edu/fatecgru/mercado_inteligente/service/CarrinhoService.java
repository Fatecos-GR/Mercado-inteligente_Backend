package br.edu.fatecgru.mercado_inteligente.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.fatecgru.mercado_inteligente.exception.EstoqueInsuficienteException;
import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.ItemCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.MovimentacaoEstoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.OrigemMovimentacao;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoMovimentacao;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.CarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.EstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MovimentacaoEstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Transactional
    public Carrinho adicionarItem(Long usuarioId, ItemCarrinhoRequest request) {
        // 1. Buscar Usuário (Temporário conforme plano)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        // 2. Buscar ou Criar Carrinho Ativo
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseGet(() -> {
                    Carrinho novo = new Carrinho();
                    novo.setUsuario(usuario);
                    novo.setStatus(StatusCarrinho.ATIVO);
                    novo.setCriadoEm(LocalDateTime.now());
                    novo.setAtualizadoEm(LocalDateTime.now());
                    return carrinhoRepository.save(novo);
                });

        // 3. Buscar Produto e Estoque com Lock Pessimista
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.produtoId()));

        Estoque estoque = estoqueRepository.findByProdutoId(request.produtoId())
                .orElseThrow(() -> new EstoqueInsuficienteException("Produto não possui registro de estoque"));

        // 4. Validar Disponibilidade
        if (estoque.getQuantidadeDisponivel() < request.quantidade()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente. Disponível: " + estoque.getQuantidadeDisponivel());
        }

        // 5. Reserva no Estoque
        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - request.quantidade());
        estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() + request.quantidade());
        estoqueRepository.save(estoque);

        // 6. Manipular Item no Carrinho
        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produto.getId())
                .orElse(null);

        if (item != null) {
            // Regra: Não duplicar, somar quantidade
            item.setQuantidade(item.getQuantidade() + request.quantidade());
        } else {
            // Regra: Novo item, preço congelado
            item = new ItemCarrinho();
            item.setCarrinho(carrinho);
            item.setProduto(produto);
            item.setQuantidade(request.quantidade());
            item.setPrecoUnidade(produto.getPreco()); // Congelamento direto em BigDecimal
        }
        itemCarrinhoRepository.save(item);

        // 7. Atualizar Carrinho
        carrinho.setAtualizadoEm(LocalDateTime.now());
        
        // 8. Registrar Movimentação
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setQuantidade(request.quantidade());
        movimentacao.setCriadoEm(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.RESERVA);
        movimentacao.setOrigem(OrigemMovimentacao.CARRINHO);
        movimentacao.setReferenciaId(Long.valueOf(carrinho.getId()));
        movimentacaoEstoqueRepository.save(movimentacao);

        return carrinhoRepository.save(carrinho);
    }
}
