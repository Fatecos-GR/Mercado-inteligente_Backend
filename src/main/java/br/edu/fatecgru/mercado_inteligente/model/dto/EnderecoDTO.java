package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoDTO(
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido")
    String cep,

	// Atributos

	// Para fazer as validações do endereço
	@NotBlank(message = "CEP é obrigatório")
	@Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
	private String cep;

	@NotBlank(message = "Logradouro é obrigatório")
	private String logradouro;

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

    @NotBlank(message = "Bairro é obrigatório")
    String bairro,

    @NotBlank(message = "Cidade é obrigatória")
    String cidade,

    @NotBlank(message = "Estado é obrigatório")
    String estado
) {}