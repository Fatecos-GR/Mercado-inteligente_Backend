package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceIntegridadeTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Test
    void deleteProduto_Sucesso_QuandoNaoEstaEmCarrinhoAtivo() {
        Long produtoId = 1L;
        when(itemCarrinhoRepository.existsByProdutoIdAndCarrinhoStatus(produtoId, StatusCarrinho.ATIVO)).thenReturn(false);

        produtoService.deleteProduto(produtoId);

        verify(produtoRepository).deleteById(produtoId);
    }

    @Test
    void deleteProduto_Erro_QuandoEstaEmCarrinhoAtivo() {
        Long produtoId = 1L;
        when(itemCarrinhoRepository.existsByProdutoIdAndCarrinhoStatus(produtoId, StatusCarrinho.ATIVO)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            produtoService.deleteProduto(produtoId);
        });

        verify(produtoRepository, never()).deleteById(produtoId);
    }
}