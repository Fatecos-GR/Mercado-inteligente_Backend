# Códigos Finais para Atualização no GitHub Web (VERSÃO CORRIGIDA)

Este arquivo contém o código **limpo e sem conflitos** para as classes principais. Use estes códigos para substituir o conteúdo no GitHub Web e resolver o erro de `application/octet-stream`.

---

## 1. Controller de Funcionário (RESOLVE ERRO OCTET-STREAM)
**Arquivo:** `src/main/java/br/edu/fatecgru/mercado_inteligente/controller/FuncionarioController.java`

```java
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioCadastroDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FuncionarioResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.service.FuncionarioService;
import br.edu.fatecgru.mercado_inteligente.service.ImagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "Endpoints relacionados aos Funcionários")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class FuncionarioController {

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private jakarta.validation.Validator validator;

	@GetMapping
	@Operation(summary = "Listar todos os funcionários", description = "Retorna uma lista de todos os funcionários cadastrados. Por padrão, retorna apenas os ativos. Requer permissão de ADMIN ou ESTOQUISTA.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Funcionários listados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos(
			@Parameter(description = "Se true, inclui funcionários desativados na lista") @RequestParam(defaultValue = "false") boolean incluirInativos) {
		List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos(incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar funcionário por ID", description = "Busca os detalhes de um funcionário específico através do seu ID único.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") 
	})
	public ResponseEntity<FuncionarioResponseDTO> buscarPorId(
			@Parameter(description = "ID do funcionário", required = true, example = "1") @PathVariable Long id) {
		Funcionario funcionario = funcionarioService.getById(id);
		if (funcionario == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Funcionário não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@GetMapping("/buscar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Buscar funcionário por nome (Apenas ADMIN)", description = "Realiza uma busca por funcionários ativos cujo nome contenha o termo pesquisado.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorNome(
			@Parameter(description = "Parte do nome ou nome completo para busca", required = true, example = "Carlos") @RequestParam String nome,
			@Parameter(description = "Se true, inclui funcionários desativados na busca") @RequestParam(defaultValue = "false") boolean incluirInativos) {

		List<FuncionarioResponseDTO> funcionarios = funcionarioService.getByNomeCompleto(nome, incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();

		return ResponseEntity.ok(funcionarios);
	}

	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar administradores (Apenas ADMIN)", description = "Retorna uma lista contendo apenas os administradores. Por padrão, apenas os ativos.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Administradores listados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> listarAdmins(
			@Parameter(description = "Se true, inclui administradores desativados na lista") @RequestParam(defaultValue = "false") boolean incluirInativos) {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarAdministradores(incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)", description = "Retorna uma lista contendo apenas os estoquistas. Por padrão, apenas os ativos.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Estoquistas listados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<List<FuncionarioResponseDTO>> listarEstoquistas(
			@Parameter(description = "Se true, inclui estoquistas desativados na lista") @RequestParam(defaultValue = "false") boolean incluirInativos) {
		List<FuncionarioResponseDTO> dtos = funcionarioService.listarEstoquistas(incluirInativos).stream()
				.map(FuncionarioResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar novo funcionário (Apenas ADMIN)", description = "Cadastra um novo funcionário no sistema, definindo obrigatoriamente se é ADMIN ou ESTOQUISTA. Requer privilégios de administrador.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") 
	})
	public ResponseEntity<FuncionarioResponseDTO> insert(
			@Parameter(description = "Dados do funcionário em formato JSON", required = true) @RequestPart("funcionario") String funcionarioJson,
			@Parameter(description = "Arquivo de imagem de perfil (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<FuncionarioCadastroDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Funcionario funcionario = funcionarioService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Atualizar funcionário", description = "Atualiza os dados de um funcionário existente. Um ADMIN pode atualizar qualquer funcionário, enquanto um funcionário só pode atualizar o seu próprio perfil.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") 
	})
	public ResponseEntity<FuncionarioResponseDTO> atualizar(
			@Parameter(description = "ID do funcionário a ser atualizado", required = true, example = "1") @PathVariable Long id,
			@Parameter(description = "Novos dados do funcionário em formato JSON", required = true) @RequestPart("funcionario") String funcionarioJson,
			@Parameter(description = "Nova imagem de perfil (opcional)") @RequestPart(value = "imagem", required = false) MultipartFile imagem)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FuncionarioCadastroDTO dto = mapper.readValue(funcionarioJson, FuncionarioCadastroDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<FuncionarioCadastroDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Funcionario funcionario = funcionarioService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Desativar funcionário (Apenas ADMIN)", description = "Desativa um funcionário do sistema. Isso bloqueia seu acesso mas mantém o histórico de suas movimentações de estoque para fins de auditoria.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "204", description = "Funcionário desativado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Funcionário não encontrado") 
	})
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID do funcionário a ser removido", required = true, example = "1") @PathVariable Long id) {
		funcionarioService.deletar(id);
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
```

---

## 2. Controller de Fornecedor (RESOLVE ERRO OCTET-STREAM)
**Arquivo:** `src/main/java/br/edu/fatecgru/mercado_inteligente/controller/FornecedorController.java`

```java
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fatecgru.mercado_inteligente.model.dto.ErrorResponse;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.model.swagger.FornecedorMultipartRequest;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Endpoints relacionados aos Fornecedores")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class FornecedorController {

	@Autowired
	private FornecedorService fornecedorService;

	@Autowired
	private jakarta.validation.Validator validator;

	@GetMapping
	@Operation(summary = "Listar todos os fornecedores")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Fornecedores listados com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<FornecedorResponseDTO>> listarTodos() {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.listarTodos().stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado") })
	public ResponseEntity<FornecedorResponseDTO> buscarPorId(@PathVariable Long id) {
		Fornecedor fornecedor = fornecedorService.getById(id);
		if (fornecedor == null) {
			throw new br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException(
					"Fornecedor não encontrado com ID: " + id);
		}
		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@GetMapping("/search")
	@Operation(summary = "Buscar fornecedor por nome")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Fornecedores encontrados"),
			@ApiResponse(responseCode = "403", description = "Acesso negado") })
	public ResponseEntity<List<FornecedorResponseDTO>> buscarPorNome(
			@RequestParam String nome) {
		List<FornecedorResponseDTO> fornecedores = fornecedorService.getByContainsName(nome).stream()
				.map(FornecedorResponseDTO::fromEntity).toList();
		return ResponseEntity.ok(fornecedores);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Criar fornecedor (Apenas ADMIN)", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = FornecedorMultipartRequest.class))))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<FornecedorResponseDTO> insert(@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<FornecedorDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Fornecedor fornecedor = fornecedorService.cadastrar(dto, imagem);

		return ResponseEntity.status(HttpStatus.CREATED).body(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Alterar fornecedor (Apenas ADMIN)", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = FornecedorMultipartRequest.class))))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Fornecedor alterado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<FornecedorResponseDTO> atualizar(@PathVariable Long id,
			@RequestPart("fornecedor") String fornecedorJson,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		FornecedorDTO dto = mapper.readValue(fornecedorJson, FornecedorDTO.class);

		java.util.Set<jakarta.validation.ConstraintViolation<FornecedorDTO>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new org.springframework.web.bind.MethodArgumentNotValidException(null,
					createBindingResult(dto, violations));
		}

		Fornecedor fornecedor = fornecedorService.atualizar(id, dto, imagem);

		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedor));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Excluir fornecedor (Apenas ADMIN)")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Fornecedor excluído com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado"),
			@ApiResponse(responseCode = "404", description = "Fornecedor não encontrado") })
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		fornecedorService.deletar(id);
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
```

---

## 3. Entidade Usuário (1 para 1)
**Arquivo:** `src/main/java/br/edu/fatecgru/mercado_inteligente/model/entity/Usuario.java`

```java
package br.edu.fatecgru.mercado_inteligente.model.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements UserDetails {

	// Atributos
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Campos obrigatórios
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String sobrenome;

	@Column(nullable = false)
	private String telefone;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	private String senha;

	// Email único no sistema
	@Column(nullable = false, unique = true)
	private String email;

	// Pode ser opcional
	@Column(length = 500)
	private String imagem;

	@Column(length = 255)
	private String publicIdImagem;

	// Relacionamento 1 para 1 com Endereço
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_id", unique = true)
	private Endereco endereco;

	@Column(nullable = false)
	private boolean ativo = true;

	// Construtores
	public Usuario() {

	}

	public Usuario(Long id, String nome, String sobrenome, String telefone, String senha, String email, String imagem,
			String publicIdImagem, Endereco endereco, boolean ativo) {
		this.id = id;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.telefone = telefone;
		this.senha = senha;
		this.email = email;
		this.imagem = imagem;
		this.publicIdImagem = publicIdImagem;
		this.endereco = endereco;
		this.ativo = ativo;
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public String getPublicIdImagem() {
		return publicIdImagem;
	}

	public void setPublicIdImagem(String publicIdImagem) {
		this.publicIdImagem = publicIdImagem;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		if (this instanceof Funcionario funcionario) {

			if (funcionario.getTipoFuncionario() == TipoFuncionario.ADMIN) {
				return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}

			return List.of(new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
		}

		return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.ativo;
	}

}
```

*(Consulte o arquivo CODIGOS_FINAIS_PARA_GITHUB.md para ver os outros arquivos: Endereço, UsuarioService, EnderecoController, CarrinhoService e Migration SQL)*
