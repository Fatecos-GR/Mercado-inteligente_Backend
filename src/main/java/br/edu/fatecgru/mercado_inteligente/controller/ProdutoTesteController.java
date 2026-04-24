package br.edu.fatecgru.mercado_inteligente.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.ProdutoTeste;
import br.edu.fatecgru.mercado_inteligente.repository.ProdutoTesteRepository;

@RestController
@RequestMapping("/produtos")
public class ProdutoTesteController {

	private final ProdutoTesteRepository repository;

	public ProdutoTesteController(ProdutoTesteRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public ProdutoTeste criar(@RequestBody ProdutoTeste produto) {
		return repository.save(produto);
	}
}
