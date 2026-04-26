package br.edu.fatecgru.mercado_inteligente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {

}
