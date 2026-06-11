package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os dados da marca")
public class MarcaResponseDTO {

	@Schema(description = "ID da marca", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long id;

	@Schema(description = "Nome da marca", example = "Marca X", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nome;

	@Schema(description = "Descrição da marca", example = "Produtos de alta qualidade")
	private String descricao;

	@Schema(description = "URL ou Base64 da imagem da marca", example = "marca.jpg")
	private String imagem;

	public MarcaResponseDTO() {

	}

	public MarcaResponseDTO(Long id, String nome, String descricao, String imagem) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.imagem = imagem;
	}

	public static MarcaResponseDTO fromEntity(Marca marca) {
		if (marca == null)
			return null;
		return new MarcaResponseDTO(marca.getId(), marca.getNome(), marca.getDescricao(), marca.getImagem());
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
