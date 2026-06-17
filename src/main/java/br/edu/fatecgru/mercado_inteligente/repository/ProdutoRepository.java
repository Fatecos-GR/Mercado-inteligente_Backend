package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	// Método para buscar produto insensível a maiúsculas/minúsculas
	public List<Produto> findByNomeContainingIgnoreCase(String nome);

	// Buscar todos os produtos de uma determinada categoria
	public List<Produto> findByCategoriaId(Long categoriaId);

	// Buscar todos os produtos de uma determinada marca
	public List<Produto> findByMarcaId(Long marcaId);

	// Buscar todos os produtos de uma determinada fornecedor
	List<Produto> findByFornecedorId(Long fornecedorid);

}
