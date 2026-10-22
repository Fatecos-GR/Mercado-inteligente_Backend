package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
		@Schema(description = "Nome do usuário", example = "João", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "O nome é obrigatório") String nome,

		@Schema(description = "Sobrenome do usuário", example = "Silva", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "O sobrenome é obrigatório") String sobrenome,

		@Schema(description = "Email do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido") String email,

		@Schema(description = "Senha do usuário (mínimo 8 caracteres, uma maiúscula, uma minúscula, um número e um símbolo)", 
				example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "A senha é obrigatória") 
		@Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
				 message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial")
		String senha,

		@Schema(description = "Telefone de contato", example = "11988887777", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "O telefone é obrigatório") String telefone) {
}
