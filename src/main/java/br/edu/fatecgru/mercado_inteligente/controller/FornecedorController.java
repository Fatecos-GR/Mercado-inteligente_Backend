package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.mapper.EnderecoMapper;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Endpoints relacionados aos Fornecedores")
public class FornecedorController {

	@Autowired
	private FornecedorService fornecedorService;

	@Autowired
	private ImagemService imagemService;

	// Lista todas os fornecedores
	@GetMapping
	@Operation(summary = "Listar todos os fornecedores")
	public List<Fornecedor> listarTodos() {
		return fornecedorService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
	public Fornecedor buscarPorId(@PathVariable Long id) {
		return fornecedorService.getById(id);
	}

	// Busca de fornecedor pelo nome
	@GetMapping("/contem-nome/{nome}")
	@Operation(summary = "Buscar fornecedor por nome")
	public List<Fornecedor> buscarPorContemNome(@PathVariable String nome) {
		return fornecedorService.getByContainsName(nome);
	}

	// Pasta dos fornecedores para salvar as imagens
	String pastaFornecedores = "suppliers/";

	// Criar fornecedor
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar fornecedor (Apenas ADMIN)")
	public ResponseEntity<?> insert(@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

			// cadastra fornecedor
			Fornecedor fornecedor = fornecedorService.cadastrar(dto, imagem);

			return ResponseEntity.status(HttpStatus.CREATED).body(FornecedorResponseDTO.fromEntity(fornecedor));

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Atualizar fornecedor
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar fornecedor (Apenas ADMIN)")
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

			// atualiza fornecedor
			Fornecedor fornecedor = fornecedorService.atualizar(id, dto, imagem);

			// substitui imagem
			String imagemAntiga = fornecedor.getImagem();

			String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaFornecedores);

			fornecedor.setImagem(imagemAtualizada);

			fornecedorService.save(fornecedor);

			// response
			FornecedorResponseDTO response = new FornecedorResponseDTO(fornecedor.getId(), fornecedor.getNome(),
					fornecedor.getImagem(), EnderecoMapper.toDTO(fornecedor.getEndereco()));

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Excluir fornecedor
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir fornecedor (Apenas ADMIN)")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		try {

			Fornecedor fornecedor = fornecedorService.getById(id);

			if (fornecedor == null) {
				return ResponseEntity.notFound().build();
			}

			// deleta imagem
			if (fornecedor.getImagem() != null) {

				imagemService.deletarImagem(fornecedor.getImagem(), pastaFornecedores);
			}

			// deleta fornecedor
			fornecedorService.deletar(id);

			return ResponseEntity.noContent().build();

		} catch (Exception e) {

			return ResponseEntity.status(500).body("Erro ao deletar: " + e.getMessage());
		}
	}

}
