package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

	@Query("""
			SELECT f
			FROM Funcionario f
			WHERE LOWER(CONCAT(f.nome, ' ', f.sobrenome))
			LIKE LOWER(CONCAT('%', :nomeCompleto, '%'))
			""")
	List<Funcionario> findByNomeCompleto(@Param("nomeCompleto") String nomeCompleto);

	// Listar funcionário por tipo e status
	List<Funcionario> findByTipoFuncionarioAndAtivo(TipoFuncionario tipoFuncionario, boolean ativo);

	List<Funcionario> findByTipoFuncionario(TipoFuncionario tipoFuncionario);

	List<Funcionario> findAllByAtivo(boolean ativo);

	// Buscar funcionário por email
	Optional<Funcionario> findByEmail(String email);

}
