package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.Pattern;

public class EnderecoDTO {

	// Para fazer as validações do endereço
	@Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido")
	private String cep;

	private String logradouro;
	private String bairro;
	private String numero;
	private String estado;
	private String cidade;
	private String complemento;

	// Getter e Setters
	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

}
