package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que representa a quantidade de produtos vinculada a um item específico (Marca, Categoria ou Fornecedor)")
public record ItemQuantidadeDTO(
		
		@Schema(description = "Nome do item", example = "Coca-Cola")
		String nome,
		
		@Schema(description = "Quantidade de produtos vinculados", example = "15")
		long quantidade
) {
}
