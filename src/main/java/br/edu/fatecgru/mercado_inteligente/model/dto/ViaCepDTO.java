package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta da integração externa com ViaCEP")
public class ViaCepDTO {

	@Schema(description = "CEP formatado", example = "01001-000")
	private String cep;

	@Schema(description = "Logradouro do CEP", example = "Praça da Sé")
	private String logradouro;

	@Schema(description = "Complemento do logradouro", example = "lado ímpar")
	private String complemento;

	@Schema(description = "Bairro", example = "Sé")
	private String bairro;

	@Schema(description = "Cidade/Localidade", example = "São Paulo")
	private String localidade;

	@Schema(description = "Unidade Federativa (Estado)", example = "SP")
	private String uf;

	@Schema(description = "Indica se ocorreu erro na busca do CEP")
	private boolean erro;

	// Getters e Setters
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

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public boolean isErro() {
		return erro;
	}

	public void setErro(boolean erro) {
		this.erro = erro;
	}

}
