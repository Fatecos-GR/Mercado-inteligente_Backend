package br.edu.fatecgru.mercado_inteligente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.DashboardStatsDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ErrorResponse;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints administrativos para estatísticas e indicadores do sistema")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private FornecedorService fornecedorService;

	@GetMapping("/marcas/contagem")
	@Operation(summary = "Contar marcas (Apenas ADMIN)", description = "Retorna a quantidade total de marcas cadastradas no sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado - Requer privilégios de ADMIN", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Long> contarMarcas() {
		return ResponseEntity.ok(marcaService.contar());
	}

	@GetMapping("/categorias/contagem")
	@Operation(summary = "Contar categorias (Apenas ADMIN)", description = "Retorna a quantidade total de categorias cadastradas no sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado - Requer privilégios de ADMIN", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Long> contarCategorias() {
		return ResponseEntity.ok(categoriaService.contar());
	}

	@GetMapping("/fornecedores/contagem")
	@Operation(summary = "Contar fornecedores (Apenas ADMIN)", description = "Retorna a quantidade total de fornecedores cadastrados no sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado - Requer privilégios de ADMIN", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Long> contarFornecedores() {
		return ResponseEntity.ok(fornecedorService.contar());
	}

	@GetMapping("/estatisticas")
	@Operation(summary = "Obter estatísticas resumidas (Apenas ADMIN)", description = "Retorna um resumo contendo a contagem de marcas, categorias e fornecedores.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Estatísticas obtidas com sucesso", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardStatsDTO.class))),
			@ApiResponse(responseCode = "403", description = "Acesso negado - Requer privilégios de ADMIN", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<DashboardStatsDTO> obterEstatisticas() {
		DashboardStatsDTO stats = new DashboardStatsDTO(
				marcaService.contar(),
				categoriaService.contar(),
				fornecedorService.contar()
		);
		return ResponseEntity.ok(stats);
	}
}
