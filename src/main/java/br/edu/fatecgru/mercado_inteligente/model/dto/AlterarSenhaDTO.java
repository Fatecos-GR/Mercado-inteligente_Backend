package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AlterarSenhaDTO {

	@NotBlank(message = "Senha atual é obrigatória")
	private String senhaAtual;

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._#-]).*$", message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial")
	private String senha;

	@NotBlank(message = "Confirmação de senha é obrigatória")
	private String confirmarSenha;

	// Getters e Setters
	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

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