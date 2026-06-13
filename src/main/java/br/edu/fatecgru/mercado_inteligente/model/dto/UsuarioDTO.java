package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(@NotBlank(message = "Nome é obrigatório") String nome,

		@NotBlank(message = "Sobrenome é obrigatório") String sobrenome,

		@NotBlank(message = "Telefone é obrigatório") String telefone,

		@NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email,

		@NotBlank(message = "Senha é obrigatória") @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._#-]).*$", message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial") String senha) {
}