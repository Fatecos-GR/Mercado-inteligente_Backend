package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import jakarta.persistence.LockModeType;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Estoque> findByProdutoId(Long produtoId);
}
