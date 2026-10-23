package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.repository.EstoqueRepository;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceIntegrityTest {

    @InjectMocks
    private EstoqueService estoqueService;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Test
    void liberarEstoqueDeCarrinho_DeveLancarIllegalStateException_QuandoQuantidadeLiberadaMaiorQueReservada() {
        // Arrange
        Long produtoId = 1L;
        Estoque estoque = new Estoque();
        estoque.setQuantidadeReservada(5);
        
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            estoqueService.liberarEstoqueDeCarrinho(produtoId, 10, 1L);
        });
    }
}
