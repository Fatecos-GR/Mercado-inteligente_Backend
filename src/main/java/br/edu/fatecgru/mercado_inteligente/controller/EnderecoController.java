package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.EnderecoService;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;
import br.edu.fatecgru.mercado_inteligente.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

	@Autowired
	private ViaCepService viaCepService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EnderecoService enderecoService;

	// Busca por CEP
	@GetMapping("/cep/{cep}")
	public ResponseEntity<?> buscarPorCep(@PathVariable String cep) {

		return ResponseEntity.ok(enderecoService.buscarPorCep(cep));
	}

	// Listar endereços de um usuário
	@GetMapping("/{id}/enderecos")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Listar endereços do usuário")
	public ResponseEntity<List<Endereco>> listarEnderecos(@PathVariable Long id) {

		return ResponseEntity.ok(usuarioService.listarEnderecosUsuario(id));
	}

	// Adicionar endereço a um usuário
	@PostMapping("/{id}/enderecos")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Adicionar endereço ao usuário")
	public ResponseEntity<Endereco> adicionarEndereco(@PathVariable Long id, @Valid @RequestBody EnderecoDTO dto) {

		Endereco endereco = usuarioService.adicionarEndereco(id, dto);

		return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
	}

	// Alterar endereço
	@PutMapping("/{id}")
	@Operation(summary = "Atualizar endereço")
	public ResponseEntity<Endereco> atualizar(@PathVariable Long id, @Valid @RequestBody EnderecoDTO dto,
			@AuthenticationPrincipal Usuario usuarioLogado) {

		return ResponseEntity.ok(enderecoService.atualizar(id, dto, usuarioLogado));
	}

	// Deletar endereço
	@DeleteMapping("/{id}")
	@Operation(summary = "Excluir endereço")
	public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {

		enderecoService.deletar(id, usuarioLogado);

		return ResponseEntity.noContent().build();
	}

}
