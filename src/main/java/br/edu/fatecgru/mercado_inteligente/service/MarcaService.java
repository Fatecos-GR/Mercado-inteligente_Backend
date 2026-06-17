package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.repository.MarcaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@Service
public class MarcaService {

	@Autowired
	private MarcaRepository marcaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ImagemService imagemService;

	private final String pastaMarcas = "brands/";

	public long contar() {
		return marcaRepository.count();
	}

	public List<br.edu.fatecgru.mercado_inteligente.model.dto.ItemQuantidadeDTO> obterEstatisticasProdutos() {
		return marcaRepository.findAll().stream()
				.map(marca -> new br.edu.fatecgru.mercado_inteligente.model.dto.ItemQuantidadeDTO(marca.getNome(),
						produtoRepository.countByMarcaId(marca.getId())))
				.toList();
	}

	public List<Marca> listarTodos() {
		return marcaRepository.findAllByOrderByNomeAsc();
	}

	public List<Marca> getByContainsName(String nome) {
		return marcaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome);
	}

	public Marca getById(Long id) {
		return marcaRepository.findById(id).orElse(null);
	}

	public Marca save(Marca marca) {
		return marcaRepository.save(marca);
	}

	public Marca cadastrar(MarcaDTO dto, MultipartFile imagem) throws Exception {
		if (marcaRepository.existsByNomeIgnoreCase(dto.getNome())) {
			throw new IllegalArgumentException("Já existe uma marca cadastrada com este nome.");
		}
		Marca marca = new Marca();

		marca.setNome(dto.getNome());
		marca.setDescricao(dto.getDescricao());

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaMarcas);

		if (imagemDTO != null) {
			marca.setImagem(imagemDTO.getUrl());
			marca.setPublicIdImagem(imagemDTO.getPublicId());
		}

		return marcaRepository.save(marca);
	}

	public Marca atualizar(Long id, MarcaDTO dto, MultipartFile imagem) throws Exception {

		Marca marca = marcaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada com ID: " + id));

		// Valida unicidade do nome se alterado
		if (!marca.getNome().equalsIgnoreCase(dto.getNome()) && marcaRepository.existsByNomeIgnoreCase(dto.getNome())) {
			throw new IllegalArgumentException("Já existe outra marca cadastrada com este nome.");
		}

		marca.setNome(dto.getNome());
		marca.setDescricao(dto.getDescricao());

		ImagemDTO novaImagem = imagemService.substituirImagem(marca.getPublicIdImagem(), imagem, pastaMarcas);

		if (novaImagem != null) {
			marca.setImagem(novaImagem.getUrl());
			marca.setPublicIdImagem(novaImagem.getPublicId());
		}

		return marcaRepository.save(marca);
	}

	@Transactional
	public void delete(Long id) {

		Marca marca = marcaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada com ID: " + id));

		List<Produto> produtos = produtoRepository.findByMarcaIdOrderByNomeAsc(id);

		// 1. Validar se algum produto da marca está em carrinho ativo
		// O método deleteProduto já faz essa validação interna e lança
		// IllegalStateException
		for (Produto produto : produtos) {
			produtoService.delete(produto.getId());
		}

		// Deleta a imagem da categoria no Cloudinary
		if (marca.getPublicIdImagem() != null && !marca.getPublicIdImagem().isBlank()) {

			imagemService.deletarImagem(marca.getPublicIdImagem());
		}

		// 2. Se passou por todos os produtos sem erro, deleta a marca
		marcaRepository.deleteById(id);
	}

}