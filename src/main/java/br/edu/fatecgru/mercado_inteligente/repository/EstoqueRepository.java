package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import jakarta.persistence.LockModeType;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

	@Override
	@Query("SELECT e FROM Estoque e JOIN FETCH e.produto")
	List<Estoque> findAll();

	@Query("""
			    SELECT e
			    FROM Estoque e
			    JOIN FETCH e.produto
			    WHERE e.produto.id = :produtoId
			""")
	Optional<Estoque> findByProdutoId(@Param("produtoId") Long produtoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			    SELECT e
			    FROM Estoque e
			    JOIN FETCH e.produto
			    WHERE e.produto.id = :produtoId
			""")
	Optional<Estoque> findByProdutoIdForUpdate(@Param("produtoId") Long produtoId);

	@Query("""
			    SELECT e
			    FROM Estoque e
			    JOIN FETCH e.produto
			    WHERE e.quantidadeDisponivel < :limite
			    ORDER BY e.quantidadeDisponivel ASC
			""")
	List<Estoque> findByQuantidadeDisponivelLessThan(@Param("limite") Integer limite);

	@Query("SELECT SUM(e.quantidadeDisponivel) FROM Estoque e")
	Long sumTotalQuantidadeDisponivel();
}
