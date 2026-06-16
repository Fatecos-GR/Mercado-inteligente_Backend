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
import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.EnderecoService;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;
import br.edu.fatecgru.mercado_inteligente.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "Endpoints relacionados aos Endereços")
public class EnderecoController {

	@Autowired
	private ViaCepService viaCepService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EnderecoService enderecoService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar todos os endereços do sistema (Admin)")
    @ApiResponse(responseCode = "200", description = "Lista de todos os endereços com informação de dono")
	public ResponseEntity<List<EnderecoResponseDTO>> listarTodos() {
		return ResponseEntity.ok(enderecoService.listarTodos());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar detalhes de um endereço por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
	public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(enderecoService.buscarPorId(id));
	}

	@GetMapping("/cep/{cep}")
	@Operation(summary = "Buscar endereço por CEP via ViaCEP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado")
    })
	public ResponseEntity<EnderecoDTO> buscarPorCep(@PathVariable String cep) {
		return ResponseEntity.ok(enderecoService.buscarPorCep(cep));
	}

	@GetMapping("/usuario/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Obter o endereço cadastrado do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço retornado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não possui endereço")
    })
	public ResponseEntity<EnderecoDTO> obterEnderecoUsuario(@PathVariable Long id) {
		Endereco endereco = usuarioService.buscarEnderecoUsuario(id);
		if (endereco == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(br.edu.fatecgru.mercado_inteligente.mapper.EnderecoMapper.toDTO(endereco));
	}

	@PostMapping("/usuario/{id}")
	@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
	@Operation(summary = "Adicionar ou atualizar endereço do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Endereço salvo com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
	public ResponseEntity<EnderecoDTO> salvarEnderecoUsuario(@PathVariable Long id, @Valid @RequestBody EnderecoDTO dto) {
		Endereco endereco = usuarioService.adicionarEndereco(id, dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(br.edu.fatecgru.mercado_inteligente.mapper.EnderecoMapper.toDTO(endereco));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar endereço por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
	public ResponseEntity<EnderecoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EnderecoDTO dto,
			@AuthenticationPrincipal Usuario usuarioLogado) {
		Endereco endereco = enderecoService.atualizar(id, dto, usuarioLogado);
		return ResponseEntity.ok(br.edu.fatecgru.mercado_inteligente.mapper.EnderecoMapper.toDTO(endereco));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Excluir endereço")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Endereço excluído com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
	public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
		enderecoService.deletar(id, usuarioLogado);
		return ResponseEntity.noContent().build();
	}

}
