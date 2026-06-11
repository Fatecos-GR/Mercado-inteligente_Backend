package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os dados da categoria")
public class CategoriaResponseDTO {
  @Schema(
    description = "ID da categoria",
    example = "1",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  private Long id;

  @Schema(
    description = "Nome da categoria",
    example = "Alimentos",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  private String nome;

  @Schema(
    description = "Descrição da categoria",
    example = "Produtos alimentícios em geral"
  )
  private String descricao;

  @Schema(description = "URL ou Base64 da imagem da categoria", example = "categoria.jpg")
  private String imagem;

  public CategoriaResponseDTO() {}

  public CategoriaResponseDTO(Long id, String nome, String descricao, String imagem) {
    this.id = id;
    this.nome = nome;
    this.descricao = descricao;
    this.imagem = imagem;
  }

  public static CategoriaResponseDTO fromEntity(Categoria categoria) {
    if (categoria == null) return null;
    return new CategoriaResponseDTO(
      categoria.getId(),
      categoria.getNome(),
      categoria.getDescricao(),
      categoria.getImagem()
    );
  }

  // Getters e Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getImagem() {
    return imagem;
  }

  public void setImagem(String imagem) {
    this.imagem = imagem;
  }
}
