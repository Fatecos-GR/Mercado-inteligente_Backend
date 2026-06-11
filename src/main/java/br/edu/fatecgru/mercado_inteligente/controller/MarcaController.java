package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/marcas")
@Tag(name = "Marcas", description = "Endpoints relacionados as Marcas")
public class MarcaController {

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private ImagemService imagemService;

	private final String pastaMarcas = "brands/";

	@GetMapping
	@Operation(summary = "Listar todas as Marcas")
	public ResponseEntity<List<MarcaResponseDTO>> listarTodos() {
		List<MarcaResponseDTO> marcas = marcaService.listarTodos().stream().map(MarcaResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(marcas);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar marca por ID")
	public ResponseEntity<MarcaResponseDTO> buscarPorId(@PathVariable Long id) {
		Marca marca = marcaService.getById(id);
		if (marca == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(MarcaResponseDTO.fromEntity(marca));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar marcas por nome")
	public ResponseEntity<List<MarcaResponseDTO>> buscarPorNome(
			@org.springframework.web.bind.annotation.RequestParam String nome) {
		List<MarcaResponseDTO> marcas = marcaService.getByContainsName(nome).stream().map(MarcaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(marcas);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Marca")
	public ResponseEntity<MarcaResponseDTO> insert(@RequestPart("marca") String marcaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		MarcaDTO dto = mapper.readValue(marcaJson, MarcaDTO.class);

		Marca marca = marcaService.cadastrar(dto);

		String nomeImagem = imagemService.salvarImagem(imagem, pastaMarcas);

		if (nomeImagem != null) {
			marca.setImagem(nomeImagem);
			marcaService.save(marca);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(MarcaResponseDTO.fromEntity(marca));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Marca")
	public ResponseEntity<MarcaResponseDTO> update(@PathVariable Long id, @RequestPart("marca") String marcaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		MarcaDTO dto = mapper.readValue(marcaJson, MarcaDTO.class);

		Marca atual = marcaService.getById(id);

		if (atual == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}

		atual.setNome(dto.getNome());
		atual.setDescricao(dto.getDescricao());

		String imagemAntiga = atual.getImagem();
		String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaMarcas);
		atual.setImagem(imagemAtualizada);

		Marca salvo = marcaService.save(atual);
		return ResponseEntity.ok(MarcaResponseDTO.fromEntity(salvo));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Marca")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Marca marca = marcaService.getById(id);

		if (marca == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Marca não encontrada com ID: " + id);
		}

		if (marca.getImagem() != null) {
			imagemService.deletarImagem(marca.getImagem(), pastaMarcas);
		}

		marcaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}