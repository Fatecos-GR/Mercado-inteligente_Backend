package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("""
			SELECT u
			FROM Usuario u
			WHERE LOWER(CONCAT(u.nome, ' ', u.sobrenome))
			LIKE LOWER(CONCAT('%', :nomeCompleto, '%'))
			ORDER BY u.nome ASC
			""")
	List<Usuario> buscarPorNomeCompleto(@Param("nomeCompleto") String nomeCompleto);

	@Query("""
			SELECT u
			FROM Usuario u
			WHERE LOWER(CONCAT(u.nome, ' ', u.sobrenome))
			LIKE LOWER(CONCAT('%', :nomeCompleto, '%'))
			AND u.ativo = true
			ORDER BY u.nome ASC
			""")
	List<Usuario> buscarAtivosPorNomeCompleto(@Param("nomeCompleto") String nomeCompleto);

	List<Usuario> findAllByAtivoOrderByNomeAsc(boolean ativo);

	// Buscar usuário por email
	Optional<Usuario> findByEmail(String email);

	// Verificação da existência de um email
	boolean existsByEmail(String email);

	@Query("SELECT COUNT(u) FROM Usuario u WHERE TYPE(u) <> Funcionario")
	long countClientes();

}
