package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity

// Impedir produto duplicado no mesmo carrinho
@Table(name = "itens_carrinho", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "carrinho_id", "produto_id" }) })

public class ItemCarrinho {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Muitos itens pertencem a um carrinho
	@ManyToOne
	@JoinColumn(name = "carrinho_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference
	private Carrinho carrinho;

	// Muitos itens podem apontar para um produto
	@ManyToOne
	@JoinColumn(name = "produto_id", nullable = false)
	private Produto produto;

	// Campos obrigatórios
	@Column(nullable = false)
	private Integer quantidade;

	// Preço congelado no momento da adição
	@Column(name = "preco_unidade", nullable = false, precision = 10, scale = 2)
	private BigDecimal precoUnidade;

	// Construtores
	public ItemCarrinho() {

	}

	public ItemCarrinho(Long id, Carrinho carrinho, Produto produto, @NotNull @Min(1) Integer quantidade,
			BigDecimal precoUnidade) {
		this.id = id;
		this.carrinho = carrinho;
		this.produto = produto;
		this.quantidade = quantidade;
		this.precoUnidade = precoUnidade;
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Carrinho getCarrinho() {
		return carrinho;
	}

	public void setCarrinho(Carrinho carrinho) {
		this.carrinho = carrinho;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getPrecoUnidade() {
		return precoUnidade;
	}

	public void setPrecoUnidade(BigDecimal precoUnidade) {
		this.precoUnidade = precoUnidade;
	}

}
