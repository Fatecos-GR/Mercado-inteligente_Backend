package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record FornecedorDTO(
    @Schema(description = "Nome do fornecedor", example = "Distribuidora de Alimentos S.A.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @Schema(description = "Endereço completo do fornecedor", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    EnderecoDTO endereco
) {}