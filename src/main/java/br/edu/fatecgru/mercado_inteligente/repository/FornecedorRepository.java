package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

	// Listar todos em ordem alfabética
	List<Fornecedor> findAllByOrderByNomeAsc();

	// Método para fazer buscar por fornecedor
	List<Fornecedor> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

}
