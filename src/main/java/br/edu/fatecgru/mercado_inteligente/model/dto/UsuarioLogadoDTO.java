package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;

public record UsuarioLogadoDTO(

		Long id,

		String nome,

		String sobrenome,

		String email,

		String telefone,

		String imagem,

		String perfil,

		EnderecoLoginDTO endereco

) {

	public static UsuarioLogadoDTO fromEntity(Usuario usuario) {

		String perfil = "CLIENTE";

		if (usuario instanceof Funcionario funcionario) {
			perfil = funcionario.getTipoFuncionario().name();
		}

		return new UsuarioLogadoDTO(

				usuario.getId(),

				usuario.getNome(),

				usuario.getSobrenome(),

				usuario.getEmail(),

				usuario.getTelefone(),

				usuario.getImagem(),

				perfil,

				EnderecoLoginDTO.fromEntity(usuario.getEndereco()));

	}
}