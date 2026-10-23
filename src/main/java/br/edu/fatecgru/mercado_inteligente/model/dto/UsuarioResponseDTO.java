package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;

public class UsuarioResponseDTO {

	private Long id;
	private String nome;
	private String sobrenome;
	private String telefone;
	private String email;
	private String imagem;
	private String publicIdImagem;

	// Construtores
	public UsuarioResponseDTO() {

	}

	public UsuarioResponseDTO(Long id, String nome, String sobrenome, String telefone, String email, String imagem,
			String publicIdImagem) {
		this.id = id;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.telefone = telefone;
		this.email = email;
		this.imagem = imagem;
		this.publicIdImagem = publicIdImagem;
	}

	public static UsuarioResponseDTO fromEntity(Usuario usuario) {
		return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getSobrenome(), usuario.getTelefone(),
				usuario.getEmail(), usuario.getImagem(), usuario.getPublicIdImagem());
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

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public String getPublicIdImagem() {
		return publicIdImagem;
	}

	public void setPublicIdImagem(String publicIdImagem) {
		this.publicIdImagem = publicIdImagem;
	}

}