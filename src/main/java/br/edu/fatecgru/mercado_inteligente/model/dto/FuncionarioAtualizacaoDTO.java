package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de entrada para atualização de dados do funcionário")
public class FuncionarioAtualizacaoDTO {

	@Schema(description = "Nome do funcionário", example = "Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@Schema(description = "Sobrenome do funcionário", example = "Oliveira", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Sobrenome é obrigatório")
	private String sobrenome;

	@Schema(description = "Telefone de contato", example = "11977776666", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Telefone é obrigatório")
	private String telefone;

	@Schema(description = "E-mail institucional", example = "carlos@mercado.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@Schema(description = "Tipo/Cargo do funcionário", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
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
