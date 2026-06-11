package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de entrada para atualização de dados do usuário")
public class UsuarioAtualizacaoDTO {

	@Schema(description = "Nome do usuário", example = "João", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@Schema(description = "Sobrenome do usuário", example = "Silva", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Sobrenome é obrigatório")
	private String sobrenome;

	@Schema(description = "Telefone de contato", example = "11988887777", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Telefone é obrigatório")
	private String telefone;

	@Schema(description = "E-mail do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@Schema(description = "URL ou Base64 da imagem de perfil", example = "perfil.jpg")
	private String imagem;

	public static UsuarioResponseDTO fromEntity(Usuario usuario) {
		return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getSobrenome(), usuario.getTelefone(),
				usuario.getEmail());
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

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

}
