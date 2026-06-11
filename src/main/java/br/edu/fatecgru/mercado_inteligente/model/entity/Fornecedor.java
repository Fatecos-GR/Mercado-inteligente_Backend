package br.edu.fatecgru.mercado_inteligente.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Campos obrigatórios
	@Column(nullable = false)
	private String nome;

	// Pode ser opcional
	@Column(length = 500)
	private String imagem;

	@Column(length = 255)
	private String publicIdImagem;

	// Um fornecedo tem um endereço principal (sede)
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_id", nullable = false)
	private Endereco endereco;

	// Construtores
	public Fornecedor() {

	}

	public Fornecedor(Long id, String nome, String imagem, String publicIdImagem, Endereco endereco) {
		this.id = id;
		this.nome = nome;
		this.imagem = imagem;
		this.publicIdImagem = publicIdImagem;
		this.endereco = endereco;
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

	public String getPublicIdImagem() {
		return publicIdImagem;
	}

	public void setPublicIdImagem(String publicIdImagem) {
		this.publicIdImagem = publicIdImagem;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

}
