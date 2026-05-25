package br.edu.fatecgru.mercado_inteligente.model.dto;

public class UsuarioResponseDTO {

	private Long id;
	private String nome;
	private String sobrenome;
	private String telefone;
	private String email;

	public UsuarioResponseDTO() {

	}

	public UsuarioResponseDTO(Long id, String nome, String sobrenome, String telefone, String email) {

		this.id = id;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.telefone = telefone;
		this.email = email;
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