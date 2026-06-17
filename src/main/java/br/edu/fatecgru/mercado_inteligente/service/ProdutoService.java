package br.edu.fatecgru.mercado_inteligente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ProdutoRequestDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.model.entity.Estoque;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.model.entity.Produto;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;
import br.edu.fatecgru.mercado_inteligente.repository.CategoriaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.EstoqueRepository;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ItemCarrinhoRepository;
import br.edu.fatecgru.mercado_inteligente.repository.MarcaRepository;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private MarcaRepository marcaRepository;

	@Autowired
	private FornecedorRepository fornecedorRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ItemCarrinhoRepository itemCarrinhoRepository;

	@Autowired
	private EstoqueRepository estoqueRepository;

	@Autowired
	private ImagemService imagemService;

	private final String pastaProdutos = "products/";

	public long contar() {
		return produtoRepository.count();
	}

	// Listar todos
	public List<Produto> listarTodos() {
		return produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
	}

	// Listar pelo ID do Produto
	public Produto getById(Long id) {
		return produtoRepository.findById(id).orElse(null);
	}

	// Listar produto pelo nome em ordem alfabética
	public List<Produto> getByContainsName(String nome) {
		return produtoRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome);
	}

	// Listar por ID da categoria
	public List<Produto> getByCategoryId(Long categoriaId) {

		categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + categoriaId));

		return produtoRepository.findByCategoriaIdOrderByNomeAsc(categoriaId);
	}

	// Listar por ID da marca
	public List<Produto> getByBrandId(Long marcaId) {

		marcaRepository.findById(marcaId)
				.orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada com ID: " + marcaId));

		return produtoRepository.findByMarcaIdOrderByNomeAsc(marcaId);
	}

	// Listar por ID do fornecedor
	public List<Produto> getBySupplierId(Long fornecedorId) {

		fornecedorRepository.findById(fornecedorId)
				.orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + fornecedorId));

		return produtoRepository.findByFornecedorIdOrderByNomeAsc(fornecedorId);
	}

	public List<Produto> listarPorMenorPreco() {

		return produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "preco"));
	}

	public List<Produto> listarPorMaiorPreco() {

		return produtoRepository.findAll(Sort.by(Sort.Direction.DESC, "preco"));
	}

	// Método para salvar
	public Produto saveProduto(Produto produto) {
		return produtoRepository.save(produto);
	}

	@Transactional
	public Produto cadastrar(ProdutoRequestDTO dto, MultipartFile imagem) throws Exception {

		Produto produto = new Produto();

		produto.setNome(dto.nome());
		produto.setDescricao(dto.descricao());
		produto.setPreco(dto.preco());
		produto.setValidade(dto.validade());

		Categoria categoria = categoriaRepository.findById(dto.categoriaId())
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

		Marca marca = marcaRepository.findById(dto.marcaId())
				.orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));

		Fornecedor fornecedor = fornecedorRepository.findById(dto.fornecedorId())
				.orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));

		produto.setCategoria(categoria);
		produto.setMarca(marca);
		produto.setFornecedor(fornecedor);

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaProdutos);

		if (imagemDTO != null) {
			produto.setImagem(imagemDTO.getUrl());
			produto.setPublicIdImagem(imagemDTO.getPublicId());
		}

		// Salva o produto
		Produto produtoSalvo = produtoRepository.save(produto);

		// Cria o estoque automaticamente
		Estoque estoque = new Estoque();
		estoque.setProduto(produtoSalvo);
		estoque.setQuantidadeDisponivel(0);
		estoque.setQuantidadeReservada(0);

		estoqueRepository.save(estoque);

		return produtoSalvo;

	}

	public Produto atualizar(Long id, ProdutoRequestDTO dto, MultipartFile imagem) throws Exception {

		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

		produto.setNome(dto.nome());
		produto.setDescricao(dto.descricao());
		produto.setPreco(dto.preco());
		produto.setValidade(dto.validade());

		Categoria categoria = categoriaRepository.findById(dto.categoriaId())
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

		Marca marca = marcaRepository.findById(dto.marcaId())
				.orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));

		Fornecedor fornecedor = fornecedorRepository.findById(dto.fornecedorId())
				.orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));

		produto.setCategoria(categoria);
		produto.setMarca(marca);
		produto.setFornecedor(fornecedor);

		ImagemDTO novaImagem = imagemService.substituirImagem(produto.getPublicIdImagem(), imagem, pastaProdutos);

		if (novaImagem != null) {
			produto.setImagem(novaImagem.getUrl());
			produto.setPublicIdImagem(novaImagem.getPublicId());
		}

		return produtoRepository.save(produto);
	}

	public void delete(Long id) {

		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

		if (itemCarrinhoRepository.existsByProdutoIdAndCarrinhoStatus(id, StatusCarrinho.ATIVO)) {

			throw new IllegalStateException("O produto não pode ser excluído pois está presente em carrinhos ativos.");
		}

		if (produto.getPublicIdImagem() != null && !produto.getPublicIdImagem().isBlank()) {

			imagemService.deletarImagem(produto.getPublicIdImagem());
		}

		produtoRepository.delete(produto);
	}

}