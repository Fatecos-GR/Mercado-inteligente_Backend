package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de entrada para alteração de senha")
public class AlterarSenhaDTO {

	@Schema(description = "Nova senha do usuário", example = "novaSenha123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	private String senha;

	@Schema(description = "Confirmação da nova senha", example = "novaSenha123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Confirmação de senha é obrigatória")
	private String confirmarSenha;

	// Getters e Setters
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {
		this.confirmarSenha = confirmarSenha;
	}

}
