package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.mapper.EnderecoMapper;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;

public class FornecedorResponseDTO {

	private Long id;
	private String nome;
	private String imagem;
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