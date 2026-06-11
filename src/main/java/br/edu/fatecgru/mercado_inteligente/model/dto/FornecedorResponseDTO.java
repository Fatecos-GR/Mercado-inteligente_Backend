package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.mapper.EnderecoMapper;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os dados do fornecedor")
public class FornecedorResponseDTO {

	@Schema(description = "ID do fornecedor", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long id;

	@Schema(description = "Nome do fornecedor", example = "Fornecedor ABC", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nome;

	@Schema(description = "URL ou Base64 da imagem do fornecedor", example = "fornecedor.jpg")
	private String imagem;

	@Schema(description = "Endereço do fornecedor", requiredMode = Schema.RequiredMode.REQUIRED)
	private EnderecoDTO endereco;

	// Construtores
	public FornecedorResponseDTO() {

	}

	public FornecedorResponseDTO(Long id, String nome, String imagem, EnderecoDTO endereco) {
		this.id = id;
		this.nome = nome;
		this.imagem = imagem;
		this.endereco = endereco;
	}

	public static FornecedorResponseDTO fromEntity(Fornecedor fornecedor) {
		return new FornecedorResponseDTO(fornecedor.getId(), fornecedor.getNome(), fornecedor.getImagem(),
				EnderecoMapper.toDTO(fornecedor.getEndereco()));
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

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public EnderecoDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}

}
