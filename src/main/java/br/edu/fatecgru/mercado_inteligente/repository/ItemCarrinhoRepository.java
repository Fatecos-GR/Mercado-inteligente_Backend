package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.ItemCarrinho;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    Optional<ItemCarrinho> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);
}
