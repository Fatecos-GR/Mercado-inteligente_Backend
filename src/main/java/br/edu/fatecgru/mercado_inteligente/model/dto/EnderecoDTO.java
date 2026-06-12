package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoDTO(
		@Schema(description = "CEP formatado (xxxxx-xxx)", example = "07123-000", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "CEP é obrigatório") @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido") String cep,

		@Schema(description = "Logradouro (Rua, Avenida, etc)", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Logradouro é obrigatório") String logradouro,

		@Schema(description = "Número da residência", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Número é obrigatório") String numero,

		@Schema(description = "Complemento (Apto, Bloco, etc)", example = "Apto 12")
		String complemento,

		@Schema(description = "Bairro", example = "Bela Vista", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Bairro é obrigatório") String bairro,

		@Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Cidade é obrigatória") String cidade,

		@Schema(description = "Estado (Sigla)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Estado é obrigatório") String estado) {
}