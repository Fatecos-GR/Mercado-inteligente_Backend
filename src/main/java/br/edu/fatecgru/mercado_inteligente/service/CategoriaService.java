package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaDTO;
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

	public List<Categoria> listarTodos() {
		return categoriaRepository.findAll();
	}

	public List<Categoria> getByContainsName(String nome) {
		return categoriaRepository.findByNomeContains(nome);
	}

	public Categoria getById(Long id) {
		return categoriaRepository.findById(id).orElse(null);
	}

	public Categoria save(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	public Categoria cadastrar(CategoriaDTO dto) {
		Categoria categoria = new Categoria();
		categoria.setNome(dto.getNome());
		categoria.setDescricao(dto.getDescricao());
		return categoriaRepository.save(categoria);
	}

	public Categoria atualizar(Long id, CategoriaDTO dto) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
		categoria.setNome(dto.getNome());
		categoria.setDescricao(dto.getDescricao());
		return categoriaRepository.save(categoria);
	}

	@Transactional
	public void delete(Long id) {
		List<Produto> produtos = produtoRepository.findByCategoriaId(id);
		
		// 1. Validar integridade e deletar produtos em cascata
		for (Produto produto : produtos) {
			produtoService.deleteProduto(produto.getId());
		}

		// 2. Se todos os produtos foram removidos, deleta a categoria
		categoriaRepository.deleteById(id);
	}

}