package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Toda movimentação deve possuir um produto
	@ManyToOne
	@JoinColumn(name = "produto_id", nullable = false)
	private Produto produto;

	// Para salvar o tipo de movimentação
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoMovimentacao tipo;

	// Quantidade movimentada
	@Column(nullable = false)
	private Integer quantidade;

	// Origem da alteração: carrinho ou ajuste
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrigemMovimentacao origem;

	/*
	 * Referência da operação: Ex.: carrinho_id ou admin_id Pode ser null quando
	 * necessário
	 */
	@Column(name = "referencia_id")
	private Long referenciaId;

	// Data da movimentação
	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm;

	// Construtores
	public MovimentacaoEstoque() {

	}

	public MovimentacaoEstoque(Long id, Produto produto, TipoMovimentacao tipo, Integer quantidade,
			OrigemMovimentacao origem, Long referenciaId, LocalDateTime criadoEm) {
		this.id = id;
		this.produto = produto;
		this.tipo = tipo;
		this.quantidade = quantidade;
		this.origem = origem;
		this.referenciaId = referenciaId;
		this.criadoEm = criadoEm;
	}

	// Getter e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public TipoMovimentacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimentacao tipo) {
		this.tipo = tipo;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public OrigemMovimentacao getOrigem() {
		return origem;
	}

	public void setOrigem(OrigemMovimentacao origem) {
		this.origem = origem;
	}

	public Long getReferenciaId() {
		return referenciaId;
	}

	public void setReferenciaId(Long referenciaId) {
		this.referenciaId = referenciaId;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(LocalDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

}
