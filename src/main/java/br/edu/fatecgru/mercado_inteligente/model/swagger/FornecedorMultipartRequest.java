package br.edu.fatecgru.mercado_inteligente.model.swagger;

import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FornecedorMultipartRequest")
public class FornecedorMultipartRequest {

	@Schema(description = "Dados do fornecedor em JSON", implementation = FornecedorDTO.class)
	private FornecedorDTO fornecedor;

	@Schema(description = "Imagem do fornecedor", type = "string", format = "binary")
	private MultipartFile imagem;

	// getters e setters

	public FornecedorDTO getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(FornecedorDTO fornecedor) {
		this.fornecedor = fornecedor;
	}

	public MultipartFile getImagem() {
		return imagem;
	}

	public void setImagem(MultipartFile imagem) {
		this.imagem = imagem;
	}
}