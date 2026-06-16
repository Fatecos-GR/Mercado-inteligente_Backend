package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;

@Service
public class FornecedorService {

	@Autowired
	private FornecedorRepository fornecedorRepository;

	@Autowired
	private EnderecoService enderecoService;

	@Autowired
	private ImagemService imagemService;

	private final String pastaFornecedores = "suppliers/";

	// Método para listar todos
	public List<Fornecedor> listarTodos() {
		return fornecedorRepository.findAll();
	}

	// Listar pelo ID do fornecedor
	public Fornecedor getById(Long id) {
		return fornecedorRepository.findById(id).orElse(null);
	}

	// Listar produto pelo o nome "contido"
	public List<Fornecedor> getByContainsName(String nome) {
		return fornecedorRepository.findByNomeContains(nome);
	}

	// salvar
	public Fornecedor save(Fornecedor fornecedor) {
		return fornecedorRepository.save(fornecedor);
	}

	// cadastrar
	public Fornecedor cadastrar(FornecedorDTO dto, MultipartFile imagem) throws Exception {

		// Validação de Endereço Único
		enderecoService.validarEnderecoUnico(dto.endereco(), null);

		Fornecedor fornecedor = new Fornecedor();

		fornecedor.setNome(dto.nome());

		Endereco endereco = criarEndereco(dto.endereco());

		fornecedor.setEndereco(endereco);

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaFornecedores);

		if (imagemDTO != null) {
			fornecedor.setImagem(imagemDTO.getUrl());
			fornecedor.setPublicIdImagem(imagemDTO.getPublicId());
		}

		return fornecedorRepository.save(fornecedor);

	}

	// atualizar
	public Fornecedor atualizar(Long id, FornecedorDTO dto, MultipartFile imagem) throws Exception {

		Fornecedor fornecedor = fornecedorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

		// Validação de Endereço Único
		enderecoService.validarEnderecoUnico(dto.endereco(),
				fornecedor.getEndereco() != null ? fornecedor.getEndereco().getId() : null);

		fornecedor.setNome(dto.nome());

		Endereco endereco = fornecedor.getEndereco();

		if (endereco == null) {
			endereco = new Endereco();
		}

		endereco.setCep(dto.endereco().cep());
		endereco.setLogradouro(dto.endereco().logradouro());
		endereco.setNumero(dto.endereco().numero());
		endereco.setComplemento(dto.endereco().complemento());
		endereco.setBairro(dto.endereco().bairro());
		endereco.setCidade(dto.endereco().cidade());
		endereco.setEstado(dto.endereco().estado());

		fornecedor.setEndereco(endereco);

		ImagemDTO novaImagem = imagemService.substituirImagem(fornecedor.getPublicIdImagem(), imagem,
				pastaFornecedores);

		if (novaImagem != null) {
			fornecedor.setImagem(novaImagem.getUrl());
			fornecedor.setPublicIdImagem(novaImagem.getPublicId());
		}

		return fornecedorRepository.save(fornecedor);

	}

	// deletar
	public void deletar(Long id) {

		Fornecedor fornecedor = fornecedorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

		if (fornecedor.getPublicIdImagem() != null && !fornecedor.getPublicIdImagem().isBlank()) {

			imagemService.deletarImagem(fornecedor.getPublicIdImagem());
		}

		fornecedorRepository.delete(fornecedor);

	}

	// método auxiliar
	private Endereco criarEndereco(EnderecoDTO dto) {

		Endereco endereco = new Endereco();

		endereco.setCep(dto.cep());
		endereco.setLogradouro(dto.logradouro());
		endereco.setNumero(dto.numero());
		endereco.setComplemento(dto.complemento());
		endereco.setBairro(dto.bairro());
		endereco.setCidade(dto.cidade());
		endereco.setEstado(dto.estado());

		return endereco;
	}

}