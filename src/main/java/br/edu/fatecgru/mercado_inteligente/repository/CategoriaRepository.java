package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	// Listar todos por ordem alfabética
	List<Categoria> findAllByOrderByNomeAsc();

	// Método para buscar categoria insensível a maiúsculas/minúsculas, em ordem
	// alfabética
	List<Categoria> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

	boolean existsByNomeIgnoreCase(String nome);

}
