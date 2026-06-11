package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de entrada para registro de novos usuários")
public record RegistroRequest(
    @Schema(description = "Nome do usuário", example = "Ana", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O nome é obrigatório") 
    String nome,

    @Schema(description = "Sobrenome do usuário", example = "Souza", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O sobrenome é obrigatório") 
    String sobrenome,

    @Schema(description = "E-mail de cadastro", example = "ana@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O email é obrigatório") 
    @Email(message = "Formato de email inválido") 
    String email,

    @Schema(description = "Senha (mínimo 8 caracteres, maiúsculas, minúsculas, números e especiais)", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "A senha é obrigatória") 
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial")
    String senha,

    @Schema(description = "Telefone de contato", example = "11966665555", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O telefone é obrigatório") 
    String telefone
) {}
