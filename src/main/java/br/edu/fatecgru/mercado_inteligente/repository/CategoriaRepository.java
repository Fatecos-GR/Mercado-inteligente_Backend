package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	// Método para buscar categorias por nome (case-insensitive)
	public List<Categoria> findByNomeContainingIgnoreCase(String nome);

}
