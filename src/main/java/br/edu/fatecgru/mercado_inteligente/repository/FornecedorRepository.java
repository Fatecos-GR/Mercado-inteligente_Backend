package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

	// Método para buscar fornecedores por nome (case-insensitive)
	public List<Fornecedor> findByNomeContainingIgnoreCase(String nome);

}
