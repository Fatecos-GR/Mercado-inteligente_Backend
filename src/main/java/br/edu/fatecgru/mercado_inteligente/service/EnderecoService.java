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

	public EnderecoDTO buscarPorCep(String cep) {

		String url = "https://viacep.com.br/ws/" + cep + "/json/";

		ViaCepDTO viaCep = restTemplate.getForObject(url, ViaCepDTO.class);

		return new EnderecoDTO(viaCep.getCep(), viaCep.getLogradouro(), null, // numero (não vem do ViaCEP)
				null, // complemento
				viaCep.getBairro(), viaCep.getLocalidade(), viaCep.getUf());
	}

	// Atualizar endereço
	public Endereco atualizar(Long id, EnderecoDTO dto) {

		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

		endereco.setCep(dto.cep());
		endereco.setLogradouro(dto.logradouro());
		endereco.setNumero(dto.numero());
		endereco.setComplemento(dto.complemento());
		endereco.setBairro(dto.bairro());
		endereco.setCidade(dto.cidade());
		endereco.setEstado(dto.estado());

		return enderecoRepository.save(endereco);
	}

	// Deletar endereço
	public void deletar(Long id) {

		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

		enderecoRepository.delete(endereco);
	}

}