package br.edu.fatecgru.mercado_inteligente.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionarios")
public class Funcionario {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Para salvar o tipo apenas como "Estoquista" ou "Administrador"
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoFuncionario tipo;

	// Construtores
	public Funcionario() {

	}

	public Funcionario(Long id, TipoFuncionario tipo) {
		this.id = id;
		this.tipo = tipo;
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoFuncionario getTipo() {
		return tipo;
	}

	public void setTipo(TipoFuncionario tipo) {
		this.tipo = tipo;
	}

}
