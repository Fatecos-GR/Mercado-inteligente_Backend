package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "carrinhos")
@EntityListeners(AuditingEntityListener.class)
public class Carrinho {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Um usuário pode ter vários carrinhos (mas apenas um estará ativo)
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Usuario usuario;

	// Um carrinho possui vários itens
	@OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL)
	@com.fasterxml.jackson.annotation.JsonManagedReference
	private List<ItemCarrinho> itens = new ArrayList<>();

	@CreatedDate
	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@LastModifiedDate
	@Column(name = "atualizado_em", nullable = false)
	private LocalDateTime atualizadoEm;

	// Para salvar o tipo apenas como "Ativo", "Finalizado" ou "Abandonado"
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusCarrinho status;

	// Construtores
	public Carrinho() {

	}

	public Carrinho(Long id, Usuario usuario, LocalDateTime criadoEm, LocalDateTime atualizadoEm,
			StatusCarrinho status) {
		this.id = id;
		this.usuario = usuario;
		this.criadoEm = criadoEm;
		this.atualizadoEm = atualizadoEm;
		this.status = status;
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(LocalDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	public StatusCarrinho getStatus() {
		return status;
	}

	public void setStatus(StatusCarrinho status) {
		this.status = status;
	}

	public List<ItemCarrinho> getItens() {
		return itens;
	}

	public void setItens(List<ItemCarrinho> itens) {
		this.itens = itens;
	}

}
