package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.repository.CategoriaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ImagemService imagemService;

	private final String pastaCategorias = "categories/";

	public List<Categoria> listarTodos() {
		return categoriaRepository.findAll();
	}

	public List<Categoria> getByContainsName(String nome) {
		return categoriaRepository.findByNomeContainingIgnoreCase(nome);
	}

	public Categoria getById(Long id) {
		return categoriaRepository.findById(id).orElse(null);
	}

	public Categoria save(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	public Categoria cadastrar(CategoriaDTO dto, MultipartFile imagem) throws Exception {

		Categoria categoria = new Categoria();

		categoria.setNome(dto.getNome());
		categoria.setDescricao(dto.getDescricao());

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaCategorias);

		if (imagemDTO != null) {
			categoria.setImagem(imagemDTO.getUrl());
			categoria.setPublicIdImagem(imagemDTO.getPublicId());
		}

		return categoriaRepository.save(categoria);
	}

	public Categoria atualizar(Long id, CategoriaDTO dto, MultipartFile imagem) throws Exception {

		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

		categoria.setNome(dto.getNome());
		categoria.setDescricao(dto.getDescricao());

		ImagemDTO novaImagem = imagemService.substituirImagem(categoria.getPublicIdImagem(), imagem, pastaCategorias);

		if (novaImagem != null) {
			categoria.setImagem(novaImagem.getUrl());
			categoria.setPublicIdImagem(novaImagem.getPublicId());
		}

		return categoriaRepository.save(categoria);
	}

	@Transactional
	public void delete(Long id) {

		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

		List<Produto> produtos = produtoRepository.findByCategoriaId(id);

		// 1. Validar integridade e deletar produtos em cascata
		for (Produto produto : produtos) {
			produtoService.deleteProduto(produto.getId());
		}

		// Deleta a imagem da categoria no Cloudinary
		if (categoria.getPublicIdImagem() != null && !categoria.getPublicIdImagem().isBlank()) {

			imagemService.deletarImagem(categoria.getPublicIdImagem());
		}

		// 2. Se todos os produtos foram removidos, deleta a categoria
		categoriaRepository.deleteById(id);
	}

}