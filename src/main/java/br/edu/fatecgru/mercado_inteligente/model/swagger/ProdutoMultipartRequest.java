package br.edu.fatecgru.mercado_inteligente.model.swagger;

import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;

public class ProdutoMultipartRequest {
	@Schema(description = "Dados do produto")
	private ProdutoRequestDTO produto;

	@Schema(description = "Imagem do produto", type = "string", format = "binary")
	private MultipartFile imagem;

	public ProdutoRequestDTO getProduto() {
		return produto;
	}

	public void setProduto(ProdutoRequestDTO produto) {
		this.produto = produto;
	}

	public MultipartFile getImagem() {
		return imagem;
	}

	public void setImagem(MultipartFile imagem) {
		this.imagem = imagem;
	}

}
