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
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "movimentacoes_estoque")
@EntityListeners(AuditingEntityListener.class)
public class MovimentacaoEstoque {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Toda movimentação deve possuir um estoque vinculado
	@ManyToOne
	@JoinColumn(name = "estoque_id", nullable = false)
	private Estoque estoque;

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
	@CreatedDate
	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	// Construtores
	public MovimentacaoEstoque() {

	}

	public MovimentacaoEstoque(Long id, Estoque estoque, TipoMovimentacao tipo, Integer quantidade,
			OrigemMovimentacao origem, Long referenciaId, LocalDateTime criadoEm) {
		this.id = id;
		this.estoque = estoque;
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

	public Estoque getEstoque() {
		return estoque;
	}

	public void setEstoque(Estoque estoque) {
		this.estoque = estoque;
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
