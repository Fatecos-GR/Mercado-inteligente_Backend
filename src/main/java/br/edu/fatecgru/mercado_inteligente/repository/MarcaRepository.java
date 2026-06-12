package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

	// Método para buscar marca insensível a maiúsculas/minúsculas
	public List<Marca> findByNomeContainingIgnoreCase(String nome);

}
