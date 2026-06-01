package br.edu.fatecgru.mercado_inteligente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ViaCepDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.repository.EnderecoRepository;

@Service
public class EnderecoService {

	@Autowired
	private EnderecoRepository enderecoRepository;

	private final RestTemplate restTemplate = new RestTemplate();

	// Buscar CEP
	public EnderecoDTO buscarPorCep(String cep) {

		String url = "https://viacep.com.br/ws/" + cep + "/json/";

		ViaCepDTO viaCep = restTemplate.getForObject(url, ViaCepDTO.class);

		EnderecoDTO endereco = new EnderecoDTO();

		endereco.setCep(viaCep.getCep());
		endereco.setLogradouro(viaCep.getLogradouro());
		endereco.setBairro(viaCep.getBairro());
		endereco.setCidade(viaCep.getLocalidade());
		endereco.setEstado(viaCep.getUf());

		return endereco;
	}

	// Atualizar endereço
	public Endereco atualizar(Long id, EnderecoDTO dto) {

		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

		endereco.setCep(dto.getCep());
		endereco.setLogradouro(dto.getLogradouro());
		endereco.setNumero(dto.getNumero());
		endereco.setComplemento(dto.getComplemento());
		endereco.setBairro(dto.getBairro());
		endereco.setCidade(dto.getCidade());
		endereco.setEstado(dto.getEstado());

		return enderecoRepository.save(endereco);
	}

	// Deletar endereço
	public void deletar(Long id) {

		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

		enderecoRepository.delete(endereco);
	}

}
