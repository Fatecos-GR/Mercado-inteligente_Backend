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

import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.ItemCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.repository.CarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
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
    private EstoqueService estoqueService;
    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Produto produto;
    private Carrinho carrinho;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        produto = new Produto();
        produto.setId(1L);
        produto.setPreco(new BigDecimal("10.0"));
        produto.setNome("Produto Teste");

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
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.empty());
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        Carrinho resultado = carrinhoService.adicionarItem(1L, request);

        assertNotNull(resultado);
        verify(estoqueService).reservarEstoqueParaCarrinho(1L, 2, 1L);
        verify(itemCarrinhoRepository).save(any(ItemCarrinho.class));
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
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.adicionarItem(1L, request);

        assertEquals(5, itemExistente.getQuantidade());
        verify(estoqueService).reservarEstoqueParaCarrinho(1L, 3, 1L);
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
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.atualizarQuantidade(1L, request);

        assertEquals(5, itemExistente.getQuantidade());
        verify(estoqueService).reservarEstoqueParaCarrinho(1L, 3, 1L);
    }

    @Test
    void atualizarQuantidade_Sucesso_Diminuir() {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest(1L, 1);
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(4);
        itemExistente.setCarrinho(carrinho);
        
        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.atualizarQuantidade(1L, request);

        assertEquals(1, itemExistente.getQuantidade());
        verify(estoqueService).liberarEstoqueDeCarrinho(1L, 3, 1L);
    }

    @Test
    void removerItem_Sucesso() {
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(3);
        itemExistente.setCarrinho(carrinho);

        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.removerItem(1L, 1L);

        verify(estoqueService).liberarEstoqueDeCarrinho(1L, 3, 1L);
        verify(itemCarrinhoRepository).delete(itemExistente);
    }

    @Test
    void abandonarCarrinho_Sucesso() {
        ItemCarrinho item1 = new ItemCarrinho();
        item1.setProduto(produto);
        item1.setQuantidade(2);
        item1.setCarrinho(carrinho);
        carrinho.getItens().add(item1);

        when(carrinhoRepository.findByUsuarioIdAndStatus(1L, StatusCarrinho.ATIVO)).thenReturn(Optional.of(carrinho));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        Carrinho resultado = carrinhoService.abandonarCarrinho(1L);

        assertEquals(StatusCarrinho.ABANDONADO, resultado.getStatus());
        verify(estoqueService).liberarEstoqueDeCarrinho(1L, 2, 1L);
    }

    @Test
    void verificarCarrinhosExpirados_Sucesso() {
        ItemCarrinho item1 = new ItemCarrinho();
        item1.setProduto(produto);
        item1.setQuantidade(2);
        item1.setCarrinho(carrinho);
        carrinho.getItens().add(item1);

        when(carrinhoRepository.findAllByStatusAndAtualizadoEmBefore(eq(StatusCarrinho.ATIVO), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(carrinho));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.verificarCarrinhosExpirados();

        assertEquals(StatusCarrinho.FINALIZADO, carrinho.getStatus());
        verify(estoqueService).liberarEstoqueDeCarrinho(1L, 2, 1L);
        verify(carrinhoRepository, atLeastOnce()).save(any(Carrinho.class));
    }
}