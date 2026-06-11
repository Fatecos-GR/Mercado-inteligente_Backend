package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de entrada para dados do funcionário")
public record FuncionarioDTO(
    @Schema(description = "Nome do funcionário", example = "Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @Schema(description = "Sobrenome do funcionário", example = "Oliveira", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Sobrenome é obrigatório")
    String sobrenome,

    @Schema(description = "Telefone de contato", example = "11977776666", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Telefone é obrigatório")
    String telefone,

    @Schema(description = "E-mail institucional", example = "carlos@mercado.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @Schema(description = "Senha de acesso", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String senha,

    @Schema(description = "Tipo/Cargo do funcionário", example = "ADMIN")
    TipoFuncionario tipoFuncionario
) {}
