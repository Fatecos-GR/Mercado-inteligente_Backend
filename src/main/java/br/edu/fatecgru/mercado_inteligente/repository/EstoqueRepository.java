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

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT e FROM Estoque e JOIN FETCH e.produto WHERE e.produto.id = :produtoId")
	Optional<Estoque> findByProdutoId(@Param("produtoId") Long produtoId);
}
