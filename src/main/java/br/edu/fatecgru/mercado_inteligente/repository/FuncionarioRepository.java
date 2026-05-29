package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

	// Listar funcionário por tipo
	List<Funcionario> findByTipoFuncionario(TipoFuncionario tipoFuncionario);

	// Buscar funcionário por email
	Optional<Funcionario> findByEmail(String email);

}
