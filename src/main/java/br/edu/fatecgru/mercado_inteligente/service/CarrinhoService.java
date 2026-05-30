package br.edu.fatecgru.mercado_inteligente.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Transactional(readOnly = true)
    public Carrinho obterCarrinhoAtivo(Long usuarioId) {
        return carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum carrinho ativo encontrado para o usuário."));
    }

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
            item.setPrecoUnidade(produto.getPreco()); // Congelamento direto
        }
        itemCarrinhoRepository.save(item);

        // 7. Atualizar Carrinho
        carrinho.setAtualizadoEm(LocalDateTime.now());
        
        // 8. Registrar Movimentação
        registrarMovimentacao(estoque, request.quantidade(), TipoMovimentacao.RESERVA, carrinho.getId());

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho atualizarQuantidade(Long usuarioId, ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho ativo não encontrado para o usuário: " + usuarioId));

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado no carrinho"));

        if (request.quantidade() <= 0) {
            return removerItem(usuarioId, request.produtoId());
        }

        Estoque estoque = estoqueRepository.findByProdutoId(request.produtoId())
                .orElseThrow(() -> new EstoqueInsuficienteException("Produto não possui registro de estoque"));

        int diferenca = request.quantidade() - item.getQuantidade();

        if (diferenca > 0) {
            // Aumentando quantidade: Validar e reservar mais
            if (estoque.getQuantidadeDisponivel() < diferenca) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para aumento. Disponível: " + estoque.getQuantidadeDisponivel());
            }
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - diferenca);
            estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() + diferenca);
            registrarMovimentacao(estoque, diferenca, TipoMovimentacao.RESERVA, carrinho.getId());
        } else if (diferenca < 0) {
            // Diminuindo quantidade: Liberar estoque
            int valorParaLiberar = Math.abs(diferenca);
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + valorParaLiberar);
            estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - valorParaLiberar);
            registrarMovimentacao(estoque, valorParaLiberar, TipoMovimentacao.LIBERACAO, carrinho.getId());
        }

        item.setQuantidade(request.quantidade());
        itemCarrinhoRepository.save(item);
        estoqueRepository.save(estoque);

        carrinho.setAtualizadoEm(LocalDateTime.now());
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho removerItem(Long usuarioId, Long produtoId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho ativo não encontrado para o usuário: " + usuarioId));

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado no carrinho"));

        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueInsuficienteException("Produto não possui registro de estoque"));

        // Liberar toda a reserva deste item
        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + item.getQuantidade());
        estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - item.getQuantidade());
        estoqueRepository.save(estoque);

        registrarMovimentacao(estoque, item.getQuantidade(), TipoMovimentacao.LIBERACAO, carrinho.getId());

        itemCarrinhoRepository.delete(item);

        carrinho.setAtualizadoEm(LocalDateTime.now());
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho abandonarCarrinho(Long usuarioId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho ativo não encontrado para o usuário: " + usuarioId));

        return liberarEstoqueEFecharCarrinho(carrinho, StatusCarrinho.ABANDONADO);
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 1 minuto
    @Transactional
    public void verificarCarrinhosExpirados() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(3);
        List<Carrinho> carrinhosExpirados = carrinhoRepository.findAllByStatusAndAtualizadoEmBefore(StatusCarrinho.ATIVO, limite);
        
        for (Carrinho carrinho : carrinhosExpirados) {
            liberarEstoqueEFecharCarrinho(carrinho, StatusCarrinho.FINALIZADO);
        }
    }

    private Carrinho liberarEstoqueEFecharCarrinho(Carrinho carrinho, StatusCarrinho novoStatus) {
        List<ItemCarrinho> itens = carrinho.getItens();
        
        for (ItemCarrinho item : itens) {
            Estoque estoque = estoqueRepository.findByProdutoId(item.getProduto().getId())
                    .orElseThrow(() -> new EstoqueInsuficienteException("Produto não possui registro de estoque: " + item.getProduto().getId()));

            // Devolver quantidade reservada para disponível
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + item.getQuantidade());
            estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - item.getQuantidade());
            estoqueRepository.save(estoque);

            registrarMovimentacao(estoque, item.getQuantidade(), TipoMovimentacao.LIBERACAO, carrinho.getId());
        }

        carrinho.setStatus(novoStatus);
        carrinho.setAtualizadoEm(LocalDateTime.now());
        return carrinhoRepository.save(carrinho);
    }

    private void registrarMovimentacao(Estoque estoque, Integer quantidade, TipoMovimentacao tipo, Long referenciaId) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEstoque(estoque);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setCriadoEm(LocalDateTime.now());
        movimentacao.setTipo(tipo);
        movimentacao.setOrigem(OrigemMovimentacao.CARRINHO);
        movimentacao.setReferenciaId(referenciaId);
        movimentacaoEstoqueRepository.save(movimentacao);
    }
}
