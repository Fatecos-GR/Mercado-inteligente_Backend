package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	// Método para fazer busca por nome
	public List<Usuario> findByNomeContains(String nome);

	// Buscar usuário por email
	Optional<Usuario> findByEmail(String email);

}
