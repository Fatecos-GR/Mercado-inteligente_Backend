package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoDTO(
		@NotBlank(message = "CEP é obrigatório") @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido") String cep,

		@NotBlank(message = "Logradouro é obrigatório") String logradouro,

		@NotBlank(message = "Número é obrigatório") String numero,

		String complemento,

		@NotBlank(message = "Bairro é obrigatório") String bairro,

		@NotBlank(message = "Cidade é obrigatória") String cidade,

		@NotBlank(message = "Estado é obrigatório") String estado) {
}