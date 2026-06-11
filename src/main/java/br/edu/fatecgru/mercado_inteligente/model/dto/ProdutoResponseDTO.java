package br.edu.fatecgru.mercado_inteligente.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os detalhes do produto")
public record ProdutoResponseDTO(
    @Schema(description = "ID do produto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long id,

    @Schema(description = "Nome do produto", example = "Arroz 5kg", requiredMode = Schema.RequiredMode.REQUIRED)
    String nome,

    @Schema(description = "Descrição detalhada do produto", example = "Arroz agulhinha tipo 1")
    String descricao,

    @Schema(description = "Preço unitário do produto", example = "25.50", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal preco,

    @Schema(description = "Data de validade do produto", example = "2025-12-31")
    LocalDate validade,

    @Schema(description = "URL ou Base64 da imagem do produto", example = "imagem.jpg")
    String imagem,

    @Schema(description = "Nome da marca", example = "Marca X", requiredMode = Schema.RequiredMode.REQUIRED)
    String marcaNome,

    @Schema(description = "Nome da categoria", example = "Alimentos", requiredMode = Schema.RequiredMode.REQUIRED)
    String categoriaNome,

    @Schema(description = "Nome do fornecedor", example = "Fornecedor ABC", requiredMode = Schema.RequiredMode.REQUIRED)
    String fornecedorNome,

    @Schema(description = "Quantidade total disponível para venda", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer estoqueDisponivel
) {}
