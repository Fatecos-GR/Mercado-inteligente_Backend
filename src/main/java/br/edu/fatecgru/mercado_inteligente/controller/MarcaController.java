package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.MarcaResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Marca;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@Tag(name = "Marcas", description = "Endpoints relacionados as Marcas")
@RequestMapping("/api/marcas")

public class MarcaController {

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private ImagemService imagemService;

	// Lista todas as categorias
	@GetMapping
	@Operation(summary = "Listar todas as Marcas")
	public List<Marca> listarTodos() {
		return marcaService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	@Operation(summary = "Buscar marca por ID")
	public Marca buscarPorId(@PathVariable Long id) {
		return marcaService.getById(id);
	}

	// Busca de marca por nome
	@GetMapping("/contem-nome/{nome}")
	@Operation(summary = "Buscar marca por ID")
	public List<Marca> buscarPorContemNome(@PathVariable String nome) {
		return marcaService.getByContainsName(nome);
	}

	// Pasta das marcas para salvar as imagens
	String pastaMarcas = "brands/";

	// Salvar Marca
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Marca")
	public ResponseEntity<?> insert(@RequestPart("marca") String marcaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			MarcaDTO dto = mapper.readValue(marcaJson, MarcaDTO.class);

			Marca marca = marcaService.cadastrar(dto);

			String nomeImagem = imagemService.salvarImagem(imagem, pastaMarcas);

			if (nomeImagem != null) {

				marca.setImagem(nomeImagem);

				marcaService.save(marca);
			}

			MarcaResponseDTO response = new MarcaResponseDTO(marca.getId(), marca.getNome(), marca.getDescricao(),
					marca.getImagem());

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Alterar Marca
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Marca")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestPart("marca") String marcaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			MarcaDTO dto = mapper.readValue(marcaJson, MarcaDTO.class);

			Marca atual = marcaService.getById(id);

			if (atual == null) {
				return ResponseEntity.notFound().build();
			}

			atual.setNome(dto.getNome());
			atual.setDescricao(dto.getDescricao());

			// substitui imagem
			String imagemAntiga = atual.getImagem();

			String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaMarcas);

			atual.setImagem(imagemAtualizada);

			return ResponseEntity.ok(marcaService.save(atual));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// Deletar Marca
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Marca")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			Marca marca = marcaService.getById(id);

			if (marca == null) {
				return ResponseEntity.notFound().build();
			}

			if (marca.getImagem() != null) {
				imagemService.deletarImagem(marca.getImagem(), pastaMarcas);
			}

			marcaService.delete(id);

			return ResponseEntity.noContent().build(); // 204

		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao deletar: " + e.getMessage());
		}
	}

}