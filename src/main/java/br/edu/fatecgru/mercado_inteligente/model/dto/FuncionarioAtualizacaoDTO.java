package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FuncionarioAtualizacaoDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@NotBlank(message = "Sobrenome é obrigatório")
	private String sobrenome;

	@NotBlank(message = "Telefone é obrigatório")
	private String telefone;

	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	private String senha;

	@NotNull(message = "Tipo do funcionário é obrigatório")
	private TipoFuncionario tipoFuncionario;

	// Construtores
	public FuncionarioAtualizacaoDTO() {

	}

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

	public TipoFuncionario getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

}
