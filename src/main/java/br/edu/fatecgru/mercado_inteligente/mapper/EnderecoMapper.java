package br.edu.fatecgru.mercado_inteligente.mapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;

public class EnderecoMapper {

	// DTO -> Entity
	public static Endereco toEntity(EnderecoDTO dto) {

		if (dto == null)
			return null;

		Endereco e = new Endereco();

		e.setCep(dto.cep());
		e.setLogradouro(dto.logradouro());
		e.setBairro(dto.bairro());
		e.setNumero(dto.numero());
		e.setCidade(dto.cidade());
		e.setEstado(dto.estado());
		e.setComplemento(dto.complemento());

		return e;
	}

	// Entity -> DTO
	public static EnderecoDTO toDTO(Endereco endereco) {

		if (endereco == null)
			return null;

		return new EnderecoDTO(
				endereco.getCep(),
				endereco.getLogradouro(),
				endereco.getNumero(),
				endereco.getComplemento(),
				endereco.getBairro(),
				endereco.getCidade(),
				endereco.getEstado()
		);
	}
}