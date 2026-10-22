package br.edu.fatecgru.mercado_inteligente.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que representa as estatísticas resumidas do sistema para o Dashboard")
public record DashboardStatsDTO(

		@Schema(description = "Quantidade total de marcas cadastradas", example = "10")
		long quantidadeMarcas,

		@Schema(description = "Quantidade total de categorias cadastradas", example = "5")
		long quantidadeCategorias,

		@Schema(description = "Quantidade total de fornecedores cadastrados", example = "3")
		long quantidadeFornecedores,

		@Schema(description = "Quantidade total de produtos cadastrados", example = "50")
		long quantidadeProdutos
) {
}
