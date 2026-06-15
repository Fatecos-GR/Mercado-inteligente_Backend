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

	public List<Marca> listarTodos() {
		return marcaRepository.findAll();
	}

	public List<Marca> getByContainsName(String nome) {
		return marcaRepository.findByNomeContainingIgnoreCase(nome);
	}

	public Marca getById(Long id) {
		return marcaRepository.findById(id).orElse(null);
	}

	public Marca save(Marca marca) {
		return marcaRepository.save(marca);
	}

	public Marca cadastrar(MarcaDTO dto, MultipartFile imagem) throws Exception {
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

		List<Produto> produtos = produtoRepository.findByMarcaId(id);

		// 1. Validar se algum produto da marca está em carrinho ativo
		// O método deleteProduto já faz essa validação interna e lança
		// IllegalStateException
		for (Produto produto : produtos) {
			produtoService.deleteProduto(produto.getId());
		}

		// Deleta a imagem da categoria no Cloudinary
		if (marca.getPublicIdImagem() != null && !marca.getPublicIdImagem().isBlank()) {

			imagemService.deletarImagem(marca.getPublicIdImagem());
		}

		// 2. Se passou por todos os produtos sem erro, deleta a marca
		marcaRepository.deleteById(id);
	}

}