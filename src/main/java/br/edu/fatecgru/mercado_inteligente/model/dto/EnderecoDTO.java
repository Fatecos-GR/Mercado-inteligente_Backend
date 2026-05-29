package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EnderecoDTO {

	// Para fazer as validações do endereço
	@NotBlank(message = "CEP é obrigatório")
	@Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido")
	private String cep;

	@NotBlank(message = "Rua é obrigatória")
	private String rua;

	@NotBlank(message = "Número é obrigatório")
	private String numero;

	private String complemento;

	@NotBlank(message = "Bairro é obrigatório")
	private String bairro;

	@NotBlank(message = "Cidade é obrigatória")
	private String cidade;

	@NotBlank(message = "Estado é obrigatório")
	private String estado;

	// Getter e Setters
	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getRua() {
		return rua;
	}

	public void setRua(String rua) {
		this.rua = rua;
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
