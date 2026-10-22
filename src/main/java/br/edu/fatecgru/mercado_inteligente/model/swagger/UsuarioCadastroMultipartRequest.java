package br.edu.fatecgru.mercado_inteligente.model.swagger;

import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioCadastroDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UsuarioCadastroMultipartRequest")
public class UsuarioCadastroMultipartRequest {

	@Schema(description = "Dados do usuário", implementation = UsuarioCadastroDTO.class)
	private UsuarioCadastroDTO usuario;

	@Schema(description = "Arquivo de imagem", type = "string", format = "binary")
	private String imagem;

	public UsuarioCadastroDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioCadastroDTO usuario) {
		this.usuario = usuario;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
}