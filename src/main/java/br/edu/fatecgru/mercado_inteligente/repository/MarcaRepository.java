package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

	// Listar todos por ordem alfabética
	List<Marca> findAllByOrderByNomeAsc();

	// Método para buscar marca insensível a maiúsculas/minúsculas, em ordem
	// alfabética
	List<Marca> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

	boolean existsByNomeIgnoreCase(String nome);

}
