package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ViaCepDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
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

	// Listar todos os endereços com informação de dono
	public List<EnderecoResponseDTO> listarTodos() {
		return enderecoRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
	}

	// Buscar um endereço por ID com informação de dono
	public EnderecoResponseDTO buscarPorId(Long id) {
		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
		return toResponseDTO(endereco);
	}

	// Atualizar endereço
	public Endereco atualizar(Long id, EnderecoDTO dto, Usuario usuarioLogado) {

		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

		// Validação de Propriedade
		validarPropriedade(endereco, usuarioLogado);

		// Validação de Endereço Único
		validarEnderecoUnico(dto, id);

		endereco.setCep(dto.cep());
		endereco.setLogradouro(dto.logradouro());
		endereco.setNumero(dto.numero());
		endereco.setComplemento(dto.complemento());
		endereco.setBairro(dto.bairro());
		endereco.setCidade(dto.cidade());
		endereco.setEstado(dto.estado());

		return enderecoRepository.save(endereco);
	}

	public void validarEnderecoUnico(EnderecoDTO dto, Long currentId) {
		enderecoRepository.findIdenticalAddress(dto.cep(), dto.logradouro(), dto.numero(), dto.bairro(), dto.cidade(),
				dto.estado(), dto.complemento()).ifPresent(enderecoExistente -> {
					if (currentId == null || !enderecoExistente.getId().equals(currentId)) {
						throw new IllegalArgumentException("Este endereço já está cadastrado no sistema.");
					}
				});
	}

	public void deletar(Long id, Usuario usuarioLogado) {

		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

		// Validação de Propriedade
		validarPropriedade(endereco, usuarioLogado);

		enderecoRepository.delete(endereco);
	}

	private void validarPropriedade(Endereco endereco, Usuario usuarioLogado) {
		boolean isOwner = usuarioLogado.getEndereco() != null
				&& usuarioLogado.getEndereco().getId().equals(endereco.getId());
		boolean isAdmin = usuarioLogado.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (!isOwner && !isAdmin) {
			throw new RuntimeException("Acesso negado: você não tem permissão para alterar este endereço.");
		}
	}

	private EnderecoResponseDTO toResponseDTO(Endereco endereco) {
		String tipoDono = "DESCONHECIDO";
		Long donoId = null;
		String nomeDono = null;

		if (endereco.getUsuario() != null) {
			Usuario u = endereco.getUsuario();
			donoId = u.getId();
			nomeDono = u.getNome() + " " + u.getSobrenome();
			tipoDono = (u instanceof Funcionario) ? "FUNCIONARIO" : "CLIENTE";
		} else if (endereco.getFornecedor() != null) {
			tipoDono = "FORNECEDOR";
			donoId = endereco.getFornecedor().getId();
			nomeDono = endereco.getFornecedor().getNome();
		}

		return new EnderecoResponseDTO(endereco.getId(), endereco.getCep(), endereco.getLogradouro(),
				endereco.getNumero(), endereco.getComplemento(), endereco.getBairro(), endereco.getCidade(),
				endereco.getEstado(), tipoDono, donoId, nomeDono);
	}

}
