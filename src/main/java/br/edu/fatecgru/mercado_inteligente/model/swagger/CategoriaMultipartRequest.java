package br.edu.fatecgru.mercado_inteligente.model.swagger;

import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CategoriaMultipartRequest")
public class CategoriaMultipartRequest {

	@Schema(description = "Dados da categoria", implementation = CategoriaDTO.class)
	private CategoriaDTO categoria;

	@Schema(description = "Imagem da categoria", type = "string", format = "binary")
	private MultipartFile imagem;

	public CategoriaDTO getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaDTO categoria) {
		this.categoria = categoria;
	}

	public MultipartFile getImagem() {
		return imagem;
	}

	public void setImagem(MultipartFile imagem) {
		this.imagem = imagem;
	}
}