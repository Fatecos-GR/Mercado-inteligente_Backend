package br.edu.fatecgru.mercado_inteligente.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.edu.fatecgru.mercado_inteligente.model.dto.ViaCepDTO;

@Service
public class ViaCepService {

	private final RestTemplate restTemplate = new RestTemplate();

	public ViaCepDTO buscarCep(String cep) {

		String url = "https://viacep.com.br/ws/" + cep + "/json/";

		ViaCepDTO resposta = restTemplate.getForObject(url, ViaCepDTO.class);

		if (resposta == null || resposta.isErro()) {
			throw new RuntimeException("CEP não encontrado");
		}

		return resposta;
	}

}
