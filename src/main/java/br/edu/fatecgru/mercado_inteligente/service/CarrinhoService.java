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
    private EstoqueService estoqueService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private br.edu.fatecgru.mercado_inteligente.repository.EnderecoRepository enderecoRepository;

    @Transactional(readOnly = true)
    public Carrinho obterCarrinhoAtivo(Long usuarioId) {
        return carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum carrinho ativo encontrado para o usuário."));
    }

    @Transactional
    public Carrinho adicionarItem(Long usuarioId, ItemCarrinhoRequest request) {
        // 1. Buscar Usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        // 2. Buscar ou Criar Carrinho Ativo
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseGet(() -> {
                    Carrinho novo = new Carrinho();
                    novo.setUsuario(usuario);
                    novo.setStatus(StatusCarrinho.ATIVO);
                    return carrinhoRepository.save(novo);
                });

        // 3. Buscar Produto 
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.produtoId()));

        // 4. Reserva no Estoque (Delegado ao EstoqueService)
        estoqueService.reservarEstoqueParaCarrinho(request.produtoId(), request.quantidade(), carrinho.getId());

        // 5. Manipular Item no Carrinho
        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produto.getId())
                .orElse(null);

        if (item != null) {
            item.setQuantidade(item.getQuantidade() + request.quantidade());
        } else {
            item = new ItemCarrinho();
            item.setCarrinho(carrinho);
            item.setProduto(produto);
            item.setQuantidade(request.quantidade());
            item.setPrecoUnidade(produto.getPreco());
        }
        itemCarrinhoRepository.save(item);

        // 6. Atualizar Carrinho
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

        int diferenca = request.quantidade() - item.getQuantidade();

        if (diferenca > 0) {
            estoqueService.reservarEstoqueParaCarrinho(request.produtoId(), diferenca, carrinho.getId());
        } else if (diferenca < 0) {
            estoqueService.liberarEstoqueDeCarrinho(request.produtoId(), Math.abs(diferenca), carrinho.getId());
        }

        item.setQuantidade(request.quantidade());
        itemCarrinhoRepository.save(item);

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho removerItem(Long usuarioId, Long produtoId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho ativo não encontrado para o usuário: " + usuarioId));

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado no carrinho"));

        // Liberar toda a reserva deste item
        estoqueService.liberarEstoqueDeCarrinho(produtoId, item.getQuantidade(), carrinho.getId());

        itemCarrinhoRepository.delete(item);

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho abandonarCarrinho(Long usuarioId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho ativo não encontrado para o usuário: " + usuarioId));

        return fecharCarrinho(carrinho, StatusCarrinho.ABANDONADO);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void verificarCarrinhosExpirados() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(3);
        List<Carrinho> carrinhosExpirados = carrinhoRepository.findAllByStatusAndAtualizadoEmBefore(StatusCarrinho.ATIVO, limite);
        
        for (Carrinho carrinho : carrinhosExpirados) {
            fecharCarrinho(carrinho, StatusCarrinho.ABANDONADO);
        }
    }

    @Transactional
    public Carrinho finalizarPedido(Long usuarioId, Long enderecoId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum carrinho ativo encontrado para checkout."));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar um pedido com o carrinho vazio.");
        }

        br.edu.fatecgru.mercado_inteligente.model.entity.Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + enderecoId));

        Usuario usuario = carrinho.getUsuario();
        if (usuario.getEndereco() == null || !usuario.getEndereco().getId().equals(enderecoId)) {
            throw new IllegalArgumentException("O endereço informado não pertence ao usuário.");
        }

        // 1. Vincular endereço de entrega
        carrinho.setEnderecoEntrega(endereco);

        // 2. Confirmar saída de estoque (RESERVA -> SAIDA)
        for (ItemCarrinho item : carrinho.getItens()) {
            estoqueService.confirmarSaidaDeCarrinho(item.getProduto().getId(), item.getQuantidade(), carrinho.getId());
        }

        // 3. Finalizar carrinho
        carrinho.setStatus(StatusCarrinho.FINALIZADO);
        return carrinhoRepository.save(carrinho);
    }

    private Carrinho fecharCarrinho(Carrinho carrinho, StatusCarrinho novoStatus) {
        List<ItemCarrinho> itens = carrinho.getItens();
        
        for (ItemCarrinho item : itens) {
            estoqueService.liberarEstoqueDeCarrinho(item.getProduto().getId(), item.getQuantidade(), carrinho.getId());
        }

        carrinho.setStatus(novoStatus);
        return carrinhoRepository.save(carrinho);
    }
}
