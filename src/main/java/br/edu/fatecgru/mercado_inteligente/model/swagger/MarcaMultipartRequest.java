package br.edu.fatecgru.mercado_inteligente.model.swagger;

import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

public class MarcaMultipartRequest {

	@Schema(description = "Dados da marca", implementation = MarcaDTO.class)
	private MarcaDTO marca;

	@Schema(description = "Arquivo de imagem", type = "string", format = "binary")
	private String imagem;

	public MarcaDTO getMarca() {
		return marca;
	}

	public void setMarca(MarcaDTO marca) {
		this.marca = marca;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
}