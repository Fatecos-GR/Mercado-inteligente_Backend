package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProdutoDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@NotBlank(message = "Descrição é obrigatória")
	private String descricao;

	@NotNull(message = "Preço é obrigatório")
	@DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
	private BigDecimal preco;

	@NotNull(message = "Validade é obrigatória")
	@Future(message = "Validade deve ser futura")
	private LocalDate validade;

	@NotNull(message = "Marca é obrigatória")
	private Long marcaId;

	@NotNull(message = "Categoria é obrigatória")
	private Long categoriaId;

	@NotNull(message = "Fornecedor é obrigatório")
	private Long fornecedorId;

	// Getters e Setters

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

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public LocalDate getValidade() {
		return validade;
	}

	public void setValidade(LocalDate validade) {
		this.validade = validade;
	}

	public Long getMarcaId() {
		return marcaId;
	}

	public void setMarcaId(Long marcaId) {
		this.marcaId = marcaId;
	}

	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}

	public Long getFornecedorId() {
		return fornecedorId;
	}

	public void setFornecedorId(Long fornecedorId) {
		this.fornecedorId = fornecedorId;
	}
}
