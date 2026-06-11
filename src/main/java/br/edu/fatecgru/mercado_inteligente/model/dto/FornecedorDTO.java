package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de entrada para cadastro/atualização de fornecedor")
public record FornecedorDTO(
    @Schema(description = "Nome do fornecedor", example = "Fornecedor ABC", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @Schema(description = "Endereço do fornecedor", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    EnderecoDTO endereco
) {}
