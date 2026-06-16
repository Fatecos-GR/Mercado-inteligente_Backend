package br.edu.fatecgru.mercado_inteligente.model.swagger;

import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FuncionarioMultipartRequest")
public class FuncionarioCadastroMultipartRequest {

	@Schema(description = "Dados do funcionário", implementation = FuncionarioCadastroDTO.class)
	private FuncionarioCadastroDTO funcionario;

	@Schema(description = "Imagem de perfil", type = "string", format = "binary")
	private MultipartFile imagem;

	public FuncionarioCadastroDTO getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(FuncionarioCadastroDTO funcionario) {
		this.funcionario = funcionario;
	}

	public MultipartFile getImagem() {
		return imagem;
	}

	public void setImagem(MultipartFile imagem) {
		this.imagem = imagem;
	}

}