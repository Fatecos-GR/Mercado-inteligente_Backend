package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class FornecedorDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@Valid
	private EnderecoDTO endereco;

	// Getters e Setters
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public EnderecoDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}

}
