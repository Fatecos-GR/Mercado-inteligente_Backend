package br.edu.fatecgru.mercado_inteligente.mapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;

public class EnderecoMapper {

	// DTO -> Entity
	public static Endereco toEntity(EnderecoDTO dto) {

		if (dto == null)
			return null;

		Endereco e = new Endereco();

		e.setCep(dto.getCep());
		e.setRua(dto.getRua());
		e.setBairro(dto.getBairro());
		e.setNumero(dto.getNumero());
		e.setCidade(dto.getCidade());
		e.setEstado(dto.getEstado());
		e.setComplemento(dto.getComplemento());

		return e;
	}

	// Entity -> DTO
	public static EnderecoDTO toDTO(Endereco endereco) {

		if (endereco == null)
			return null;

		EnderecoDTO dto = new EnderecoDTO();

		dto.setCep(endereco.getCep());
		dto.setRua(endereco.getRua());
		dto.setBairro(endereco.getBairro());
		dto.setNumero(endereco.getNumero());
		dto.setCidade(endereco.getCidade());
		dto.setEstado(endereco.getEstado());
		dto.setComplemento(endereco.getComplemento());

		return dto;
	}
}