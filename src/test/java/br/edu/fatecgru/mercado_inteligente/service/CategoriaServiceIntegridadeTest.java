package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.repository.CategoriaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceIntegridadeTest {

    @InjectMocks
    private CategoriaService categoriaService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ProdutoService produtoService;

    @Test
    void delete_Sucesso_QuandoNaoHaProdutos() {
        Long categoriaId = 1L;
        when(produtoRepository.findByCategoriaId(categoriaId)).thenReturn(Collections.emptyList());

        categoriaService.delete(categoriaId);

        verify(categoriaRepository).deleteById(categoriaId);
    }

    @Test
    void delete_Sucesso_ComCascata_QuandoProdutosNaoEstaoAtivos() {
        Long categoriaId = 1L;
        Produto p1 = new Produto();
        p1.setId(20L);
        p1.setNome("Produto Teste");
        
        when(produtoRepository.findByCategoriaId(categoriaId)).thenReturn(List.of(p1));
        when(itemCarrinhoRepository.existsByProdutoIdAndCarrinhoStatus(20L, StatusCarrinho.ATIVO)).thenReturn(false);

        categoriaService.delete(categoriaId);

        verify(produtoService).deleteProduto(20L);
        verify(categoriaRepository).deleteById(categoriaId);
    }

    @Test
    void delete_Erro_QuandoAlgumProdutoEstaEmCarrinhoAtivo() {
        Long categoriaId = 1L;
        Produto p1 = new Produto();
        p1.setId(20L);
        p1.setNome("Produto Ativo");
        
        when(produtoRepository.findByCategoriaId(categoriaId)).thenReturn(List.of(p1));
        
        // Agora o bloqueio acontece antes, no check do repositório
        when(itemCarrinhoRepository.existsByProdutoIdAndCarrinhoStatus(20L, StatusCarrinho.ATIVO)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            categoriaService.delete(categoriaId);
        });

        verify(produtoService, never()).deleteProduto(anyLong());
        verify(categoriaRepository, never()).deleteById(categoriaId);
    }
}