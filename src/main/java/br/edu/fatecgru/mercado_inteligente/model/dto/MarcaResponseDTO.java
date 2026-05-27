package br.edu.fatecgru.mercado_inteligente.model.dto;

public class MarcaResponseDTO {

	private Long id;
	private String nome;
	private String descricao;
	private String imagem;

	public MarcaResponseDTO() {

	}

	public MarcaResponseDTO(Long id, String nome, String descricao, String imagem) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.imagem = imagem;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

}
