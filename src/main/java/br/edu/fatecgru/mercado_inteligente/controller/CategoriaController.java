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
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
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

	@GetMapping
	@Operation(summary = "Listar todas as categorias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso")
    })
	public ResponseEntity<List<CategoriaResponseDTO>> listarTodos() {
		List<CategoriaResponseDTO> categorias = categoriaService.listarTodos().stream()
				.map(c -> new CategoriaResponseDTO(c.getId(), c.getNome(), c.getDescricao(), c.getImagem())).toList();
		return ResponseEntity.ok(categorias);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
	public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
		Categoria categoria = categoriaService.getById(id);
		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Categoria não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(new CategoriaResponseDTO(categoria.getId(), categoria.getNome(),
				categoria.getDescricao(), categoria.getImagem()));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar categorias por nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorias encontradas")
    })
	public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNome(@org.springframework.web.bind.annotation.RequestParam String nome) {
		List<CategoriaResponseDTO> categorias = categoriaService.getByContainsName(nome).stream()
				.map(c -> new CategoriaResponseDTO(c.getId(), c.getNome(), c.getDescricao(), c.getImagem())).toList();
		return ResponseEntity.ok(categorias);
	}

	String pastaCategorias = "categories/";

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
	public ResponseEntity<CategoriaResponseDTO> insert(@RequestPart("categoria") String categoriaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<CategoriaDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null, createBindingResult(dto, violations));
		}

		Categoria categoria = categoriaService.cadastrar(dto);

		ImagemDTO imagemDTO = imagemService.salvarImagem(imagem, pastaCategorias);

		if (imagemDTO != null) {
			categoria.setImagem(imagemDTO.getUrl());
			categoria.setPublicIdImagem(imagemDTO.getPublicId());
		}

		categoriaService.save(categoria);

		CategoriaResponseDTO response = new CategoriaResponseDTO(categoria.getId(), categoria.getNome(),
				categoria.getDescricao(), categoria.getImagem());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria alterada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
	public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Long id,
			@RequestPart("categoria") String categoriaJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<CategoriaDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null, createBindingResult(dto, violations));
		}

		Categoria atual = categoriaService.getById(id);

		if (atual == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Categoria não encontrada com ID: " + id);
		}

		atual.setNome(dto.getNome());
		atual.setDescricao(dto.getDescricao());

		ImagemDTO novaImagem = imagemService.substituirImagem(atual.getPublicIdImagem(), imagem, pastaCategorias);

		if (novaImagem != null) {
			atual.setImagem(novaImagem.getUrl());
			atual.setPublicIdImagem(novaImagem.getPublicId());
		}

		categoriaService.save(atual);
		return ResponseEntity.ok(new CategoriaResponseDTO(atual.getId(), atual.getNome(), atual.getDescricao(), atual.getImagem()));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Categoria categoria = categoriaService.getById(id);

		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Categoria não encontrada com ID: " + id);
		}

		if (categoria.getPublicIdImagem() != null) {
			imagemService.deletarImagem(categoria.getPublicIdImagem());
		}

		categoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}

	private org.springframework.validation.BindingResult createBindingResult(Object target, java.util.Set<? extends jakarta.validation.ConstraintViolation<?>> violations) {
		org.springframework.validation.BeanPropertyBindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(target, "dto");
		for (jakarta.validation.ConstraintViolation<?> violation : violations) {
			bindingResult.addError(new org.springframework.validation.FieldError("dto", violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return bindingResult;
	}

}
