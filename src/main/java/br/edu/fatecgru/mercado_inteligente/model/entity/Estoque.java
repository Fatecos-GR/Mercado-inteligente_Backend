package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "estoques")
@EntityListeners(AuditingEntityListener.class)
public class Estoque {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Todo produto deve ter um registro de estoque e cada estoque pertence a um
	// único produto
	@OneToOne
	@JoinColumn(name = "produto_id", nullable = false, unique = true)
	private Produto produto;

	// Quantidade disponível para venda
	@Column(name = "quantidade_disponivel", nullable = false)
	private Integer quantidadeDisponivel;

	// Quantidade reservada (ex.: item adicionado ao carrinho)
	@Column(name = "quantidade_reservada", nullable = false)
	private Integer quantidadeReservada;

	// Data da última atualização
	@LastModifiedDate
	@Column(name = "atualizado_em", nullable = false)
	private LocalDateTime atualizadoEm;

	// Construtores
	public Estoque() {

	}

	public Estoque(Long id, Produto produto, Integer quantidadeDisponivel, Integer quantidadeReservada,
			LocalDateTime atualizadoEm) {
		this.id = id;
		this.produto = produto;
		this.quantidadeDisponivel = quantidadeDisponivel;
		this.quantidadeReservada = quantidadeReservada;
		this.atualizadoEm = atualizadoEm;
	}

	// Getters e Setters
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

	public Integer getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}

	public Integer getQuantidadeReservada() {
		return quantidadeReservada;
	}

	public void setQuantidadeReservada(Integer quantidadeReservada) {
		this.quantidadeReservada = quantidadeReservada;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

}
