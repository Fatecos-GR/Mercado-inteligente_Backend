package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioCadastroDTO {

	@Schema(description = "Primeiro nome do usuário", example = "Maria", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@Schema(description = "Sobrenome do usuário", example = "Oliveira", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Sobrenome é obrigatório")
	private String sobrenome;

	@Schema(description = "Telefone celular (10 ou 11 dígitos)", example = "11912345678", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Telefone é obrigatório")
	@Pattern(regexp = "\\d{10,11}", message = "Telefone inválido")
	private String telefone;

	@Schema(description = "Email para acesso", example = "maria@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@Schema(description = "Senha com no mínimo 8 caracteres, contendo letra maiúscula, minúscula, número e caractere especial", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._#-]).*$", message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial")
	private String senha;

	// Getters e Setters
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}