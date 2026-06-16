package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnderecoResponseDTO(
		@Schema(description = "ID do endereço", example = "1")
		Long id,

		@Schema(description = "CEP formatado", example = "07123-000")
		String cep,

		@Schema(description = "Logradouro", example = "Avenida Paulista")
		String logradouro,

		@Schema(description = "Número", example = "1000")
		String numero,

		@Schema(description = "Complemento", example = "Apto 12")
		String complemento,

		@Schema(description = "Bairro", example = "Bela Vista")
		String bairro,

		@Schema(description = "Cidade", example = "São Paulo")
		String cidade,

		@Schema(description = "Estado", example = "SP")
		String estado,

		@Schema(description = "Tipo do dono do endereço (CLIENTE, FUNCIONARIO, FORNECEDOR)", example = "CLIENTE")
		String tipoDono,

		@Schema(description = "ID do dono", example = "1")
		Long donoId,

		@Schema(description = "Nome do dono", example = "João Silva")
		String nomeDono
) {
}
