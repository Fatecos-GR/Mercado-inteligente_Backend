package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.TipoFuncionario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os dados do funcionário")
public class FuncionarioResponseDTO {

	@Schema(description = "ID do funcionário", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long id;

	@Schema(description = "Nome do funcionário", example = "Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
	private String nome;

	@Schema(description = "Sobrenome do funcionário", example = "Oliveira", requiredMode = Schema.RequiredMode.REQUIRED)
	private String sobrenome;

	@Schema(description = "Telefone de contato", example = "11977776666")
	private String telefone;

	@Schema(description = "E-mail do funcionário", example = "carlos@mercado.com", requiredMode = Schema.RequiredMode.REQUIRED)
	private String email;

	@Schema(description = "URL ou Base64 da imagem de perfil", example = "perfil.jpg")
	private String imagem;

	@Schema(description = "Tipo/Cargo do funcionário", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
	private TipoFuncionario tipoFuncionario;

	// Construtores
	public FuncionarioResponseDTO() {

	}

	public FuncionarioResponseDTO(Long id, String nome, String sobrenome, String telefone, String email, String imagem,
			TipoFuncionario tipoFuncionario) {
		this.id = id;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.telefone = telefone;
		this.email = email;
		this.imagem = imagem;
		this.tipoFuncionario = tipoFuncionario;
	}

	public static FuncionarioResponseDTO fromEntity(Funcionario funcionario) {
		return new FuncionarioResponseDTO(funcionario.getId(), funcionario.getNome(), funcionario.getSobrenome(),
				funcionario.getTelefone(), funcionario.getEmail(), funcionario.getImagem(),
				funcionario.getTipoFuncionario());
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

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public TipoFuncionario getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

}
