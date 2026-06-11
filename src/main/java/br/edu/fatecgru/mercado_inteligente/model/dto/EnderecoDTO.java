package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "DTO para representação de endereço")
public record EnderecoDTO(
    @Schema(description = "CEP do endereço", example = "01001-000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "CEP é obrigatório") 
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido") 
    String cep,

    @Schema(description = "Logradouro (Rua, Avenida, etc.)", example = "Praça da Sé", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Logradouro é obrigatório") 
    String logradouro,

    @Schema(description = "Número do imóvel", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Número é obrigatório") 
    String numero,

    @Schema(description = "Complemento do endereço", example = "Apto 45")
    String complemento,

    @Schema(description = "Bairro", example = "Sé", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Bairro é obrigatório") 
    String bairro,

    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Cidade é obrigatória") 
    String cidade,

    @Schema(description = "Estado (UF)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Estado é obrigatório") 
    String estado
) {}
