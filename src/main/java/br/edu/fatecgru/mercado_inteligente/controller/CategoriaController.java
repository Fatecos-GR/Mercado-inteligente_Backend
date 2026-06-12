package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints relacionados a categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private ImagemService imagemService;

	private final String pastaCategorias = "categories/";

	@GetMapping
	@Operation(summary = "Listar todas as categorias")
	public ResponseEntity<List<CategoriaResponseDTO>> listarTodos() {
		List<CategoriaResponseDTO> categorias = categoriaService.listarTodos().stream()
				.map(CategoriaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(categorias);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por ID")
	public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
		Categoria categoria = categoriaService.getById(id);
		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Categoria não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoria));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar categorias por nome")
	public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNome(@RequestParam String nome) {
		List<CategoriaResponseDTO> categorias = categoriaService.getByContainingName(nome).stream()
				.map(CategoriaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(categorias);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Categoria")
	public ResponseEntity<CategoriaResponseDTO> insert(@RequestPart("categoria") String categoriaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		Categoria categoria = categoriaService.cadastrar(dto);

		String nomeImagem = imagemService.salvarImagem(imagem, pastaCategorias);

		if (nomeImagem != null) {
			categoria.setImagem(nomeImagem);
			categoriaService.save(categoria);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponseDTO.fromEntity(categoria));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Categoria")
	public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Long id, @RequestPart("categoria") String categoriaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		Categoria atual = categoriaService.getById(id);

		if (atual == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Categoria não encontrada com ID: " + id);
		}

		atual.setNome(dto.getNome());
		atual.setDescricao(dto.getDescricao());

		String imagemAntiga = atual.getImagem();
		String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaCategorias);
		atual.setImagem(imagemAtualizada);

		Categoria salvo = categoriaService.save(atual);
		return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(salvo));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Categoria")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Categoria categoria = categoriaService.getById(id);

		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Categoria não encontrada com ID: " + id);
		}

		if (categoria.getImagem() != null) {
			imagemService.deletarImagem(categoria.getImagem(), pastaCategorias);
		}

		categoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
