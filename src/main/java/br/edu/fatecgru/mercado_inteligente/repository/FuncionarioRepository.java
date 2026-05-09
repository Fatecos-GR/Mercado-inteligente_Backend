package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
	List<Funcionario> findByTipoFuncionario(TipoFuncionario tipoFuncionario);

}
