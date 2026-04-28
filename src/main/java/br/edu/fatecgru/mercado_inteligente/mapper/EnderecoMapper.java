package br.edu.fatecgru.mercado_inteligente.mapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;

public class EnderecoMapper {
	public static Endereco toEntity(EnderecoDTO dto) {
		if (dto == null)
			return null;

		Endereco e = new Endereco();
		e.setCep(dto.getCep());
		e.setLogradouro(dto.getLogradouro());
		e.setBairro(dto.getBairro());
		e.setNumero(dto.getNumero());
		e.setCidade(dto.getCidade());
		e.setEstado(dto.getEstado());

		return e;
	}
}
