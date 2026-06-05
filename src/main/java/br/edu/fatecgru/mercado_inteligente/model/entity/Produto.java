package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "produtos")
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Campos obrigatórios
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String descricao;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal preco;

	@Column(nullable = false)
	private LocalDate validade;

	// Pode ser opcional
	private String imagem;

	// Muitos produtos podem pertencer à mesma marca
	@ManyToOne
	@JoinColumn(name = "marca_id", nullable = false)
	private Marca marca;

	// Muitos produtos podem pertencer à mesma categoria
	@ManyToOne
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	// Muitos produtos podem pertencer ao mesmo fornecedor
	@ManyToOne
	@JoinColumn(name = "fornecedor_id", nullable = false)
	private Fornecedor fornecedor;

	@jakarta.persistence.OneToOne(mappedBy = "produto")
	private Estoque estoque;

	// Construtores
	public Produto() {

	}

	public Produto(Long id, String nome, String descricao, BigDecimal preco, LocalDate validade, String imagem,
			Marca marca, Categoria categoria, Fornecedor fornecedor) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.validade = validade;
		this.imagem = imagem;
		this.marca = marca;
		this.categoria = categoria;
		this.fornecedor = fornecedor;
	}

	// Getters e Setters
    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

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

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

}
