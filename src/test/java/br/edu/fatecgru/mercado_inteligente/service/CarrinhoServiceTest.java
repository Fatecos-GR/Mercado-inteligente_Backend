package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.fatecgru.mercado_inteligente.exception.EstoqueInsuficienteException;
import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.ItemCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.MovimentacaoEstoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.CarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.EstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MovimentacaoEstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class CarrinhoServiceTest {

    @InjectMocks
    private CarrinhoService carrinhoService;

    @Mock
    private CarrinhoRepository carrinhoRepository;
    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    private Usuario usuario;
    private Produto produto;
    private Estoque estoque;
    private Carrinho carrinho;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        produto = new Produto();
        produto.setId(1L);
        produto.setPreco(new BigDecimal("10.0"));
        produto.setNome("Produto Teste");

        estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidadeDisponivel(10);
        estoque.setQuantidadeReservada(0);

        carrinho = new Carrinho();
        carrinho.setId(1L);
        carrinho.setUsuario(usuario);
        carrinho.setStatus(StatusCarrinho.ATIVO);
    }

    @Test
    void adicionarItem_Sucesso_NovoItem() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.empty());
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        Carrinho resultado = carrinhoService.adicionarItem(1L, request);

        assertNotNull(resultado);
        assertEquals(8, estoque.getQuantidadeDisponivel());
        assertEquals(2, estoque.getQuantidadeReservada());
        verify(itemCarrinhoRepository).save(any(ItemCarrinho.class));
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void adicionarItem_Sucesso_SomaQuantidade() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 3);
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.adicionarItem(1L, request);

        assertEquals(5, itemExistente.getQuantidade());
        assertEquals(7, estoque.getQuantidadeDisponivel());
        assertEquals(3, estoque.getQuantidadeReservada());
    }

    @Test
    void adicionarItem_Erro_EstoqueInsuficiente() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 15);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));

        assertThrows(EstoqueInsuficienteException.class, () -> {
            carrinhoService.adicionarItem(1L, request);
        });
    }

    @Test
    void adicionarItem_Erro_UsuarioNaoEncontrado() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 1);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            carrinhoService.adicionarItem(1L, request);
        });
    }

    @Test
    void atualizarQuantidade_Sucesso_Aumentar() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 5);
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(2);
        itemExistente.setCarrinho(carrinho);

        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.atualizarQuantidade(1L, request);

        assertEquals(5, itemExistente.getQuantidade());
        assertEquals(7, estoque.getQuantidadeDisponivel()); // 10 - (5-2) = 7
        assertEquals(3, estoque.getQuantidadeReservada());   // 0 + (5-2) = 3
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void atualizarQuantidade_Sucesso_Diminuir() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 1);
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(4);
        itemExistente.setCarrinho(carrinho);
        
        estoque.setQuantidadeReservada(4);
        estoque.setQuantidadeDisponivel(6);

        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.atualizarQuantidade(1L, request);

        assertEquals(1, itemExistente.getQuantidade());
        assertEquals(9, estoque.getQuantidadeDisponivel()); // 6 + (4-1) = 9
        assertEquals(1, estoque.getQuantidadeReservada());   // 4 - (4-1) = 1
    }

    @Test
    void removerItem_Sucesso() {
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(3);
        itemExistente.setCarrinho(carrinho);

        estoque.setQuantidadeReservada(3);
        estoque.setQuantidadeDisponivel(7);

        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.removerItem(1L, 1L);

        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
        verify(itemCarrinhoRepository).delete(itemExistente);
    }

    @Test
    void abandonarCarrinho_Sucesso() {
        ItemCarrinho item1 = new ItemCarrinho();
        item1.setProduto(produto);
        item1.setQuantidade(2);
        item1.setCarrinho(carrinho);
        carrinho.getItens().add(item1);

        estoque.setQuantidadeReservada(2);
        estoque.setQuantidadeDisponivel(8);

        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        Carrinho resultado = carrinhoService.abandonarCarrinho(1L);

        assertEquals(StatusCarrinho.ABANDONADO, resultado.getStatus());
        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void verificarCarrinhosExpirados_Sucesso() {
        ItemCarrinho item1 = new ItemCarrinho();
        item1.setProduto(produto);
        item1.setQuantidade(2);
        item1.setCarrinho(carrinho);
        carrinho.getItens().add(item1);

        estoque.setQuantidadeReservada(2);
        estoque.setQuantidadeDisponivel(8);

        when(carrinhoRepository.findAllByStatusAndAtualizadoEmBefore(eq(StatusCarrinho.ATIVO), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(carrinho));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.verificarCarrinhosExpirados();

        assertEquals(StatusCarrinho.FINALIZADO, carrinho.getStatus());
        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
        verify(carrinhoRepository, atLeastOnce()).save(any(Carrinho.class));
    }
}
