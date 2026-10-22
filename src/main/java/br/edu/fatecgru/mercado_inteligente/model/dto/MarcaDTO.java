package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class MarcaDTO {

	@Schema(description = "Nome da marca", example = "Nestlé", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@Schema(description = "Descrição da marca", example = "Produtos alimentícios e bebidas", requiredMode = Schema.RequiredMode.REQUIRED)
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
