package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(@NotBlank(message = "O nome é obrigatório") String nome,

		@NotBlank(message = "O sobrenome é obrigatório") String sobrenome,

		@NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido") String email,

		@NotBlank(message = "A senha é obrigatória") 
		@Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
				 message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial")
		String senha,

		@NotBlank(message = "O telefone é obrigatório") String telefone) {
}
