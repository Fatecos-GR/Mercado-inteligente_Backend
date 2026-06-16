package br.edu.fatecgru.mercado_inteligente.model.swagger;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioAtualizacaoDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FuncionarioAtualizacaoMultipartRequest")
public class FuncionarioAtualizacaoMultipartRequest {

	@Schema(description = "Dados atualizados do funcionário", implementation = FuncionarioAtualizacaoDTO.class)
	private FuncionarioAtualizacaoDTO funcionario;

	@Schema(description = "Nova imagem de perfil", type = "string", format = "binary")
	private String imagem;

	public FuncionarioAtualizacaoDTO getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(FuncionarioAtualizacaoDTO funcionario) {
		this.funcionario = funcionario;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
}