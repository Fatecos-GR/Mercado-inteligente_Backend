package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.MovimentacaoEstoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoMovimentacao;
import br.edu.fatecgru.mercado_inteligente.repository.EstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MovimentacaoEstoqueRepository;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceTest {

    @InjectMocks
    private EstoqueService estoqueService;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setProduto(produto);
        estoque.setQuantidadeDisponivel(10);
        estoque.setQuantidadeReservada(0);
    }

    @Test
    void listarTodos_Sucesso() {
        when(estoqueRepository.findAll()).thenReturn(List.of(estoque));
        List<Estoque> resultado = estoqueService.listarTodos();
        assertEquals(1, resultado.size());
        verify(estoqueRepository).findAll();
    }

    @Test
    void buscarPorProdutoId_Sucesso() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        Estoque resultado = estoqueService.buscarPorProdutoId(1L);
        assertEquals(10, resultado.getQuantidadeDisponivel());
    }

    @Test
    void buscarPorProdutoId_Erro_NaoEncontrado() {
        when(estoqueRepository.findByProdutoId(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> estoqueService.buscarPorProdutoId(2L));
    }

    @Test
    void executarAjuste_Sucesso_Entrada() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        
        estoqueService.executarAjuste(1L, 5, TipoMovimentacao.ENTRADA, 100L);
        
        assertEquals(15, estoque.getQuantidadeDisponivel());
        verify(estoqueRepository).save(estoque);
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void executarAjuste_Sucesso_Saida() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        
        estoqueService.executarAjuste(1L, 4, TipoMovimentacao.SAIDA, 100L);
        
        assertEquals(6, estoque.getQuantidadeDisponivel());
        verify(estoqueRepository).save(estoque);
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void executarAjuste_Erro_SaidaInsuficiente() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        
        assertThrows(EstoqueInsuficienteException.class, () -> 
            estoqueService.executarAjuste(1L, 20, TipoMovimentacao.SAIDA, 100L)
        );
    }

    @Test
    void reservarEstoqueParaCarrinho_Sucesso() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        
        estoqueService.reservarEstoqueParaCarrinho(1L, 3, 50L);
        
        assertEquals(7, estoque.getQuantidadeDisponivel());
        assertEquals(3, estoque.getQuantidadeReservada());
        verify(estoqueRepository).save(estoque);
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void liberarEstoqueDeCarrinho_Sucesso() {
        estoque.setQuantidadeDisponivel(7);
        estoque.setQuantidadeReservada(3);
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        
        estoqueService.liberarEstoqueDeCarrinho(1L, 3, 50L);
        
        assertEquals(10, estoque.getQuantidadeDisponivel());
        assertEquals(0, estoque.getQuantidadeReservada());
        verify(estoqueRepository).save(estoque);
        verify(movimentacaoEstoqueRepository).save(any(MovimentacaoEstoque.class));
    }
}
