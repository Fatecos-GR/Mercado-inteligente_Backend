package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Integer> {

	// Método para fazer buscar por categoria
	public List<Marca> findByNomeContains(String nome);

}
