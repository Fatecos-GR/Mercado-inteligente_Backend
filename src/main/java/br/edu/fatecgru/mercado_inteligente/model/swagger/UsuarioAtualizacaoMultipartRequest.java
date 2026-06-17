package br.edu.fatecgru.mercado_inteligente.model.swagger;

import br.edu.fatecgru.mercado_inteligente.model.dto.UsuarioAtualizacaoDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UsuarioAtualizacaoMultipartRequest")
public class UsuarioAtualizacaoMultipartRequest {

	@Schema(description = "Dados atualizados do usuário", implementation = UsuarioAtualizacaoDTO.class)
	private UsuarioAtualizacaoDTO usuario;

	@Schema(description = "Arquivo da imagem do usuário", type = "string", format = "binary")
	private String imagem;

	public UsuarioAtualizacaoDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioAtualizacaoDTO usuario) {
		this.usuario = usuario;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
}