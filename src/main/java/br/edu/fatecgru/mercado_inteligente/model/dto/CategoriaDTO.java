package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoriaDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@NotBlank(message = "Descrição é obrigatória")
	private String descricao;

	// Getters e Setters
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
