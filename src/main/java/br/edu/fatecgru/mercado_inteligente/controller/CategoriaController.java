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
import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
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
	private jakarta.validation.Validator validator;

	@GetMapping
	@Operation(summary = "Listar todas as categorias")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso") })
	public ResponseEntity<List<CategoriaResponseDTO>> listarTodos() {
		List<CategoriaResponseDTO> categorias = categoriaService.listarTodos().stream()
				.map(c -> new CategoriaResponseDTO(c.getId(), c.getNome(), c.getDescricao(), c.getImagem(),
						c.getPublicIdImagem()))
				.toList();
		return ResponseEntity.ok(categorias);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
			@ApiResponse(responseCode = "404", description = "Categoria não encontrada") })
	public ResponseEntity<CategoriaResponseDTO> buscarPorId(
			@Parameter(description = "ID da categoria", required = true) @PathVariable Long id) {
		Categoria categoria = categoriaService.getById(id);
		if (categoria == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Categoria não encontrada com ID: " + id);
		}
		return ResponseEntity.ok(new CategoriaResponseDTO(categoria.getId(), categoria.getNome(),
				categoria.getDescricao(), categoria.getImagem(), categoria.getPublicIdImagem()));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar categorias por nome")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Categorias encontradas") })
	public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNome(
			@Parameter(description = "Nome ou parte do nome", required = true) @RequestParam String nome) {
		List<CategoriaResponseDTO> categorias = categoriaService.getByContainsName(nome).stream()
				.map(c -> new CategoriaResponseDTO(c.getId(), c.getNome(), c.getDescricao(), c.getImagem(),
						c.getPublicIdImagem()))
				.toList();
		return ResponseEntity.ok(categorias);
	}

	String pastaCategorias = "categories/";

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Salvar Categoria", description = "Cadastra uma nova categoria no sistema. O nome da categoria deve ser único.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já existente"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<CategoriaResponseDTO> insert(
			@Parameter(description = "Dados da categoria em JSON", required = true) @RequestPart("categoria") String categoriaJson,
			@Parameter(description = "Arquivo de imagem da categoria") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<CategoriaDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Categoria categoria = categoriaService.cadastrar(dto, imagem);

		categoriaService.save(categoria);

		CategoriaResponseDTO response = new CategoriaResponseDTO(categoria.getId(), categoria.getNome(),
				categoria.getDescricao(), categoria.getImagem(), categoria.getPublicIdImagem());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar Categoria", description = "Atualiza os dados de uma categoria existente. Se o nome for alterado, ele deve continuar sendo único no sistema.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Categoria alterada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já existente"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Categoria não encontrada") })
	public ResponseEntity<CategoriaResponseDTO> update(
			@Parameter(description = "ID da categoria", required = true) @PathVariable Long id,
			@Parameter(description = "Dados atualizados da categoria em JSON", required = true) @RequestPart("categoria") String categoriaJson,
			@Parameter(description = "Novo arquivo de imagem (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		CategoriaDTO dto = mapper.readValue(categoriaJson, CategoriaDTO.class);

		// Validação Manual
		java.util.Set<jakarta.validation.ConstraintViolation<CategoriaDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Categoria categoria = categoriaService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoria));

	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deletar Categoria", description = "Remove uma categoria e seus produtos vinculados. A exclusão falhará se algum produto da categoria estiver em um carrinho ativo.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
			@ApiResponse(responseCode = "400", description = "Não é possível excluir (produtos em carrinhos ativos)"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Categoria não encontrada") })
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID da categoria", required = true) @PathVariable Long id) {

		categoriaService.delete(id);

		return ResponseEntity.noContent().build();
	}

	private org.springframework.validation.BindingResult createBindingResult(Object target,
			java.util.Set<? extends jakarta.validation.ConstraintViolation<?>> violations) {
		org.springframework.validation.BeanPropertyBindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(
				target, "dto");
		for (jakarta.validation.ConstraintViolation<?> violation : violations) {
			bindingResult.addError(new org.springframework.validation.FieldError("dto",
					violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return bindingResult;
	}

}
