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

	// Método para listar todas
	public List<Categoria> listarTodos() {
		return categoriaRepository.findAll();
	}

	// Listar pelo ID
	public Categoria getById(Long id) {
		return categoriaRepository.findById(id).orElse(null);
	}

	// Listar pelo nome
	public List<Categoria> getByContainsName(String nome) {
		return categoriaRepository.findByNomeContainingIgnoreCase(nome);
	}

	// Método para cadastrar categoria
	public Categoria cadastrar(CategoriaDTO dto, MultipartFile imagem) throws Exception {

		if (categoriaRepository.existsByNomeIgnoreCase(dto.getNome())) {
			throw new IllegalArgumentException("Já existe uma categoria cadastrada com este nome.");
		}

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

	public Categoria save(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	// Método para atualizar categoria
	public Categoria atualizar(Long id, CategoriaDTO dto, MultipartFile imagem) throws Exception {

		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

		// Valida unicidade do nome se alterado
		if (!categoria.getNome().equalsIgnoreCase(dto.getNome())
				&& categoriaRepository.existsByNomeIgnoreCase(dto.getNome())) {
			throw new IllegalArgumentException("Já existe outra categoria cadastrada com este nome.");
		}

		categoria.setNome(dto.getNome());
		categoria.setDescricao(dto.getDescricao());

		ImagemDTO novaImagem = imagemService.substituirImagem(categoria.getPublicIdImagem(), imagem, pastaCategorias);

		if (novaImagem != null) {
			categoria.setImagem(novaImagem.getUrl());
			categoria.setPublicIdImagem(novaImagem.getPublicId());
		}

		return categoriaRepository.save(categoria);
	}

	// Método para excluir categoria
	@Transactional
	public void delete(Long id) {

		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

		List<Produto> produtos = produtoRepository.findByCategoriaId(id);

		// 1. Validar integridade e deletar produtos em cascata
		for (Produto produto : produtos) {
			produtoService.delete(produto.getId());
		}

		// Deleta a imagem da categoria no Cloudinary
		if (categoria.getPublicIdImagem() != null && !categoria.getPublicIdImagem().isBlank()) {

			imagemService.deletarImagem(categoria.getPublicIdImagem());
		}

		// 2. Se todos os produtos foram removidos, deleta a categoria
		categoriaRepository.deleteById(id);
	}

}
