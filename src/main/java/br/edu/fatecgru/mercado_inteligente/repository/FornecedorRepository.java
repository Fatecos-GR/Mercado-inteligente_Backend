package br.edu.fatecgru.mercado_inteligente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

}
