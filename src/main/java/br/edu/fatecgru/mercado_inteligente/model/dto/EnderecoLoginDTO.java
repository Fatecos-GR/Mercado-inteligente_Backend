package br.edu.fatecgru.mercado_inteligente.model.dto;

import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;

public record EnderecoLoginDTO(

		Long id,

		String cep,

		String logradouro,

		String numero,

		String complemento,

		String bairro,

		String cidade,

		String estado

) {

	public static EnderecoLoginDTO fromEntity(Endereco endereco) {

		if (endereco == null) {
			return null;
		}

		return new EnderecoLoginDTO(

				endereco.getId(),

				endereco.getCep(),

				endereco.getLogradouro(),

				endereco.getNumero(),

				endereco.getComplemento(),

				endereco.getBairro(),

				endereco.getCidade(),

				endereco.getEstado());
	}
}