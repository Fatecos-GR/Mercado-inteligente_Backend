package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de entrada para autenticação")
public record LoginRequest(
    @Schema(description = "E-mail do usuário", example = "usuario@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O email é obrigatório") 
    @Email(message = "Formato de email inválido") 
    String email,

    @Schema(description = "Senha do usuário", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "A senha é obrigatória") 
    String senha
) {}
