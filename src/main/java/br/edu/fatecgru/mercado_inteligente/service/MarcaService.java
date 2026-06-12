package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public List<Marca> listarTodos() {
		return marcaRepository.findAll();
	}

	public List<Marca> getByContainingName(String nome) {
		return marcaRepository.findByNomeContainingIgnoreCase(nome);
	}

	public Marca getById(Long id) {
		return marcaRepository.findById(id).orElse(null);
	}

	public Marca save(Marca marca) {
		return marcaRepository.save(marca);
	}

	public Marca cadastrar(MarcaDTO dto) {
		Marca marca = new Marca();
		marca.setNome(dto.getNome());
		marca.setDescricao(dto.getDescricao());
		return marcaRepository.save(marca);
	}

	public Marca atualizar(Long id, MarcaDTO dto) {
		Marca marca = marcaRepository.findById(id).orElseThrow(() -> new RuntimeException("Marca não encontrada"));
		marca.setNome(dto.getNome());
		marca.setDescricao(dto.getDescricao());
		return marcaRepository.save(marca);
	}

	@Transactional
	public void delete(Long id) {
		List<Produto> produtos = produtoRepository.findByMarcaId(id);
		
		// 1. Validar se algum produto da marca está em carrinho ativo
		// O método deleteProduto já faz essa validação interna e lança IllegalStateException
		for (Produto produto : produtos) {
			produtoService.deleteProduto(produto.getId());
		}

		// 2. Se passou por todos os produtos sem erro, deleta a marca
		marcaRepository.deleteById(id);
	}

}