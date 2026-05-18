package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@Service
public class ProdutoService {

	// Método de listar todos
	@Autowired
	private ProdutoRepository produtoRepository;

	public List<Produto> listarTodos() {
		return produtoRepository.findAll();
	}

	// Listar pelo ID do Produto
	public Produto getById(Long id) {
		return produtoRepository.findById(id).orElse(null);
	}

	// Listar produto pelo o nome "contido"
	public List<Produto> getByContainsName(String nome) {
		return produtoRepository.findByNomeContains(nome);
	}

	// Listar por ID da categoria
	public List<Produto> getByCategoryId(Long categoriaId) {
		return produtoRepository.findByCategoriaId(categoriaId);
	}

	// Listar por ID da marca
	public List<Produto> getByBrandId(Long marcaId) {
		return produtoRepository.findByMarcaId(marcaId);
	}

	// Método para salvar ou alterar produto
	public Produto saveProduto(Produto produto) {
		return produtoRepository.save(produto);
	}

	// Método para excluir produto
	public void deleteProduto(Long id) {
		produtoRepository.deleteById(id);
	}

}
