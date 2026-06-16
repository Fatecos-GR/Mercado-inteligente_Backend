package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements UserDetails {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Campos obrigatórios
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String sobrenome;

	@Column(nullable = false)
	private String telefone;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	private String senha;

	// Email único no sistema
	@Column(nullable = false, unique = true)
	private String email;

	// Pode ser opcional
	@Column(length = 500)
	private String imagem;

	@Column(length = 255)
	private String publicIdImagem;

	// Relacionamento 1 para 1 com Endereço
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_id", unique = true)
	private Endereco endereco;

	@Column(nullable = false)
	private boolean ativo = true;

	// Construtores
	public Usuario() {

	}

	public Usuario(Long id, String nome, String sobrenome, String telefone, String senha, String email, String imagem,
			String publicIdImagem, Endereco endereco, boolean ativo) {
		this.id = id;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.telefone = telefone;
		this.senha = senha;
		this.email = email;
		this.imagem = imagem;
		this.publicIdImagem = publicIdImagem;
		this.endereco = endereco;
		this.ativo = ativo;
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

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		if (this instanceof Funcionario funcionario) {

			if (funcionario.getTipoFuncionario() == TipoFuncionario.ADMIN) {
				return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}

			return List.of(new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
		}

		return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.ativo;
	}

}