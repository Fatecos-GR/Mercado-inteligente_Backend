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

import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
//Cria o geral, todos precisam desse
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints relacionados a categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private ImagemService imagemService;

	// Lista todas as categorias
	@GetMapping
	@Operation(summary = "Listar todas as categorias")
	public List<Categoria> listarTodos() {
		return categoriaService.listarTodos();
	}

	// Busca por ID
	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por ID")
	public Categoria buscarPorId(@PathVariable Long id) {
		return categoriaService.getById(id);
	}

	// Busca de categoria pelo nome
	@GetMapping("/contem-nome/{nome}")
	@Operation(summary = "Buscar por nome")
	public List<Categoria> buscarPorContemNome(@PathVariable String nome) {
		return categoriaService.getByContainsName(nome);
	}

	// Pasta das categorias para salvar as imagens
	String pastaCategorias = "categories/";

	// Salvar Categoria
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Categoria")
	public ResponseEntity<?> insert(@RequestPart("categoria") String categoriaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

			Categoria categoria = categoriaService.cadastrar(dto);

			String nomeImagem = imagemService.salvarImagem(imagem, pastaCategorias);

			if (nomeImagem != null) {

				categoria.setImagem(nomeImagem);

				categoriaService.save(categoria);
			}

			CategoriaResponseDTO response = new CategoriaResponseDTO(categoria.getId(), categoria.getNome(),
					categoria.getDescricao(), categoria.getImagem());

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Alterar Categoria
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Categoria")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestPart("categoria") String categoriaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

			Categoria atual = categoriaService.getById(id);

			if (atual == null) {
				return ResponseEntity.notFound().build();
			}

			atual.setNome(dto.getNome());
			atual.setDescricao(dto.getDescricao());

			// substitui imagem
			String imagemAntiga = atual.getImagem();

			String imagemAtualizada = imagemService.substituirImagem(imagemAntiga, imagem, pastaCategorias);

			atual.setImagem(imagemAtualizada);

			return ResponseEntity.ok(categoriaService.save(atual));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// Deletar Categoria
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Categoria")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			Categoria categoria = categoriaService.getById(id);

			if (categoria == null) {
				return ResponseEntity.notFound().build();
			}

			if (categoria.getImagem() != null) {
				imagemService.deletarImagem(categoria.getImagem(), pastaCategorias);
			}

			categoriaService.delete(id);

			return ResponseEntity.noContent().build(); // 204

		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao deletar: " + e.getMessage());
		}
	}

}
