package br.edu.fatecgru.mercado_inteligente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

	@Query("SELECT e FROM Endereco e WHERE " + "e.cep = :cep AND " + "e.logradouro = :logradouro AND "
			+ "e.numero = :numero AND " + "e.bairro = :bairro AND " + "e.cidade = :cidade AND " + "e.estado = :estado AND "
			+ "((e.complemento IS NULL AND :complemento IS NULL) OR (e.complemento = :complemento))")
	Optional<Endereco> findIdenticalAddress(@Param("cep") String cep, @Param("logradouro") String logradouro,
			@Param("numero") String numero, @Param("bairro") String bairro, @Param("cidade") String cidade,
			@Param("estado") String estado, @Param("complemento") String complemento);

}
