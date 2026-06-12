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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.CategoriaResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints relacionados a categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	private final String pastaCategorias = "categories/";

	@GetMapping
	@Operation(summary = "Listar todas as categorias", description = "Retorna uma lista de todas as categorias cadastradas no sistema.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
	})
	public ResponseEntity<List<CategoriaResponseDTO>> listarTodos() {
		List<CategoriaResponseDTO> categorias = categoriaService.listarTodos().stream()
				.map(CategoriaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(categorias);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por ID", description = "Busca os detalhes de uma categoria específica pelo seu identificador único.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Categoria encontrada"),
		@ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	})
	public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable @Parameter(description = "ID da categoria") Long id) {
		Categoria categoria = categoriaService.getById(id);
		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Categoria não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoria));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar categorias por nome", description = "Filtra categorias cujo nome contenha o termo pesquisado (case-insensitive).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
	})
	public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNome(@RequestParam @Parameter(description = "Nome ou parte do nome para busca") String nome) {
		List<CategoriaResponseDTO> categorias = categoriaService.getByContainingName(nome).stream()
				.map(CategoriaResponseDTO::fromEntity)
				.toList();
		return ResponseEntity.ok(categorias);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Categoria", description = "Cria uma nova categoria com suporte a upload de imagem para o Cloudinary (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
		@ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de validação"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<CategoriaResponseDTO> insert(@RequestPart("categoria") @Parameter(description = "Dados da categoria em formato JSON") String categoriaJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Arquivo de imagem") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Categoria categoria = categoriaService.cadastrar(dto);

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaCategorias);

		if (imagemDTO != null) {
			categoria.setImagem(imagemDTO.getUrl());
			categoria.setPublicIdImagem(imagemDTO.getPublicId());
		}

		categoriaService.save(categoria);

		return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponseDTO.fromEntity(categoria));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Categoria", description = "Atualiza os dados de uma categoria existente e permite substituir a imagem (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
		@ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
		@ApiResponse(responseCode = "400", description = "Dados inválidos"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<CategoriaResponseDTO> update(@PathVariable @Parameter(description = "ID da categoria") Long id, @RequestPart("categoria") @Parameter(description = "Dados da categoria em formato JSON") String categoriaJson,
			@RequestPart(value = "imagem", required = false) @Parameter(description = "Nova imagem (opcional)") MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		// Validação Manual do DTO
		var violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new jakarta.validation.ConstraintViolationException(violations);
		}

		Categoria atual = categoriaService.getById(id);

		if (atual == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Categoria não encontrada com ID: " + id);
		}

		atual.setNome(dto.getNome());
		atual.setDescricao(dto.getDescricao());

		ImagemDTO novaImagem = imagemService.substituirImagem(atual.getPublicIdImagem(), imagem, pastaCategorias);

		if (novaImagem != null) {
			atual.setImagem(novaImagem.getUrl());
			atual.setPublicIdImagem(novaImagem.getPublicId());
		}

		Categoria salvo = categoriaService.save(atual);
		return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(salvo));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Categoria", description = "Exclui uma categoria e seus produtos em cascata, se não houver vínculos ativos em carrinhos (Apenas ADMIN).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
		@ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
		@ApiResponse(responseCode = "400", description = "Erro de integridade (presença em carrinhos ativos)"),
		@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "ID da categoria") Long id) {
		Categoria categoria = categoriaService.getById(id);

		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException("Categoria não encontrada com ID: " + id);
		}

		if (categoria.getPublicIdImagem() != null) {
			imagemService.deletarImagem(categoria.getPublicIdImagem());
		}

		categoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
