package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os dados do usuário")
public class UsuarioResponseDTO {

	@Schema(description = "ID do usuário", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long id;

	@Schema(description = "Nome do usuário", example = "João", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nome;

	@Schema(description = "Sobrenome do usuário", example = "Silva", requiredMode = Schema.RequiredMode.REQUIRED)
	private String sobrenome;

	@Schema(description = "Telefone de contato", example = "11988887777")
	private String telefone;

	@Schema(description = "E-mail do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
	private String email;

	// Construtores
	public UsuarioResponseDTO() {

	}

	public UsuarioResponseDTO(Long id, String nome, String sobrenome, String telefone, String email) {
		this.id = id;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.telefone = telefone;
		this.email = email;
	}

	public static UsuarioResponseDTO fromEntity(Usuario usuario) {
		return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getSobrenome(), usuario.getTelefone(),
				usuario.getEmail());
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

}
