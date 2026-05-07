package br.edu.fatecgru.mercado_inteligente.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

// Funcionário herda os atributos do usuário
@Entity
@Table(name = "funcionarios")
public class Funcionario extends Usuario {

	// Atributos
	// Para salvar o tipo apenas como "Estoquista" ou "Administrador"
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoFuncionario tipoFuncionario;

	// Construtores
	public Funcionario() {

	}

	public Funcionario(TipoFuncionario tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

	// Getters e Setters
	public TipoFuncionario getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

}
