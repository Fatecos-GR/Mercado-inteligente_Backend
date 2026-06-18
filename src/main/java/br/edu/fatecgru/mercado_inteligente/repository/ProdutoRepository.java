package br.edu.fatecgru.mercado_inteligente.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	// Método para buscar produto insensível a maiúsculas/minúsculas, em ordem
	// alfabética
	List<Produto> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

	// Buscar todos os produtos de uma determinada categoria
	List<Produto> findByCategoriaIdOrderByNomeAsc(Long categoriaId);

	// Buscar todos os produtos de uma determinada marca
	List<Produto> findByMarcaIdOrderByNomeAsc(Long marcaId);

	// Buscar todos os produtos de uma determinada fornecedor
	List<Produto> findByFornecedorIdOrderByNomeAsc(Long fornecedorId);

	long countByCategoriaId(Long categoriaId);

	long countByMarcaId(Long marcaId);

	long countByFornecedorId(Long fornecedorId);

	long countByValidadeBefore(LocalDate data);

	long countByValidadeBetween(LocalDate dataInicio, LocalDate dataFim);

}
