package br.edu.fatecgru.mercado_inteligente.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record FornecedorDTO(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @Valid
    EnderecoDTO endereco
) {}