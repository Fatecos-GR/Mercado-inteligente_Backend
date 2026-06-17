package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import br.edu.fatecgru.mercado_inteligente.repository.MarcaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class MarcaServiceIntegridadeTest {

	@InjectMocks
	private MarcaService marcaService;

	@Mock
	private MarcaRepository marcaRepository;

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private ProdutoService produtoService;

	@Test
	void delete_Sucesso_QuandoNaoHaProdutos() {
		Long marcaId = 1L;
		when(produtoRepository.findByMarcaIdOrderByNomeAsc(marcaId)).thenReturn(Collections.emptyList());

		marcaService.delete(marcaId);

		verify(marcaRepository).deleteById(marcaId);
	}

	@Test
	void delete_Sucesso_ComCascata_QuandoProdutosNaoEstaoAtivos() {
		Long marcaId = 1L;
		Produto p1 = new Produto();
		p1.setId(10L);

		when(produtoRepository.findByMarcaIdOrderByNomeAsc(marcaId)).thenReturn(List.of(p1));

		marcaService.delete(marcaId);

		verify(produtoService).delete(10L);
		verify(marcaRepository).deleteById(marcaId);
	}

	@Test
	void delete_Erro_QuandoAlgumProdutoEstaEmCarrinhoAtivo() {
		Long marcaId = 1L;
		Produto p1 = new Produto();
		p1.setId(10L);

		when(produtoRepository.findByMarcaIdOrderByNomeAsc(marcaId)).thenReturn(List.of(p1));

		// Simula que o produtoService bloqueou a exclusão do produto por estar em
		// carrinho ativo
		doThrow(new IllegalStateException("Erro de integridade")).when(produtoService).delete(10L);

		assertThrows(IllegalStateException.class, () -> {
			marcaService.delete(marcaId);
		});

		verify(marcaRepository, never()).deleteById(marcaId);
	}
}