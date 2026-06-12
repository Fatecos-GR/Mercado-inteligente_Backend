package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@Schema(description = "Email do usuário", example = "admin@mercado.com", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido") String email,

		@Schema(description = "Senha do usuário", example = "Senha123!", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "A senha é obrigatória") String senha) {
}
