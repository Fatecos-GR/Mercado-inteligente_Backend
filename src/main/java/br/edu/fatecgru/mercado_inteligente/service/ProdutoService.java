package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ItemCarrinhoRepository itemCarrinhoRepository;

	public List<Produto> listarTodos() {
		return produtoRepository.findAll();
	}

	// Listar pelo ID do Produto
	public Produto getById(Long id) {
		return produtoRepository.findById(id).orElse(null);
	}

	// Listar produto pelo o nome "contido" insensível a maiúsculas/minúsculas
	public List<Produto> getByContainsName(String nome) {
		return produtoRepository.findByNomeContainingIgnoreCase(nome);
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
		if (itemCarrinhoRepository.existsByProdutoIdAndCarrinhoStatus(id, StatusCarrinho.ATIVO)) {
			throw new IllegalStateException("O produto não pode ser excluído pois está presente em carrinhos ativos.");
		}
		produtoRepository.deleteById(id);
	}

}