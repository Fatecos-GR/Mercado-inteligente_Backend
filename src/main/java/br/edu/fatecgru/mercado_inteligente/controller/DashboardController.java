package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.DashboardStatsDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ErrorResponse;
import br.edu.fatecgru.mercado_inteligente.model.dto.ItemQuantidadeDTO;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.EstoqueService;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;
import br.edu.fatecgru.mercado_inteligente.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints administrativos para estatísticas e indicadores do sistema")
public class DashboardController {

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private FornecedorService fornecedorService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private EstoqueService estoqueService;

	@GetMapping("/marcas/contagem")
	@PreAuthorize("hasRole('ADMIN')")
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
	@PreAuthorize("hasRole('ADMIN')")
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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Contar fornecedores (Apenas ADMIN)", description = "Retorna a quantidade total de fornecedores cadastrados no sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado - Requer privilégios de ADMIN", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Long> contarFornecedores() {
		return ResponseEntity.ok(fornecedorService.contar());
	}

	@GetMapping("/produtos/contagem")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Contar tipos de produtos (ADMIN/ESTOQUISTA)", description = "Retorna a quantidade de tipos de produtos (SKUs) cadastrados no sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<Long> contarProdutos() {
		return ResponseEntity.ok(produtoService.contar());
	}

	@GetMapping("/produtos/estoque-total")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Quantidade total de produtos em estoque (ADMIN/ESTOQUISTA)", description = "Retorna a soma de todas as unidades de todos os produtos disponíveis em estoque.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<Long> contarTotalEstoque() {
		return ResponseEntity.ok(estoqueService.contarTotalItens());
	}

	@GetMapping("/produtos/quantidade-geral")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Listar quantidade de todos os produtos (ADMIN/ESTOQUISTA)", description = "Retorna a lista de todos os produtos cadastrados e suas respectivas quantidades em estoque.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso", 
				content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemQuantidadeDTO.class)))),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<ItemQuantidadeDTO>> listarQuantidadeGeral() {
		return ResponseEntity.ok(estoqueService.obterEstatisticasProdutos());
	}

	@GetMapping("/estatisticas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obter estatísticas resumidas (Apenas ADMIN)", description = "Retorna um resumo contendo a contagem total de marcas, categorias, fornecedores e produtos.")
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
				fornecedorService.contar(),
				produtoService.contar()
		);
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/produtos/baixo-estoque")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Produtos com Baixo Estoque (ADMIN/ESTOQUISTA)", description = "Retorna a lista de produtos com estoque abaixo de 10 unidades.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso", 
				content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemQuantidadeDTO.class)))),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<ItemQuantidadeDTO>> listarProdutosBaixoEstoque() {
		return ResponseEntity.ok(estoqueService.obterProdutosBaixoEstoque());
	}

	@GetMapping("/produtos/por-marca")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Produtos por Marca (ADMIN/ESTOQUISTA)", description = "Retorna a lista de todas as marcas e a quantidade de produtos vinculada a cada uma.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso", 
				content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemQuantidadeDTO.class)))),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<ItemQuantidadeDTO>> produtosPorMarca() {
		return ResponseEntity.ok(marcaService.obterEstatisticasProdutos());
	}

	@GetMapping("/produtos/por-categoria")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Produtos por Categoria (ADMIN/ESTOQUISTA)", description = "Retorna a lista de todas as categorias e a quantidade de produtos vinculada a cada uma.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso", 
				content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemQuantidadeDTO.class)))),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<ItemQuantidadeDTO>> produtosPorCategoria() {
		return ResponseEntity.ok(categoriaService.obterEstatisticasProdutos());
	}

	@GetMapping("/produtos/por-fornecedor")
	@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
	@Operation(summary = "Produtos por Fornecedor (ADMIN/ESTOQUISTA)", description = "Retorna a lista de todos os fornecedores e a quantidade de produtos vinculada a cada um.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso", 
				content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemQuantidadeDTO.class)))),
			@ApiResponse(responseCode = "403", description = "Acesso negado")
	})
	public ResponseEntity<List<ItemQuantidadeDTO>> produtosPorFornecedor() {
		return ResponseEntity.ok(fornecedorService.obterEstatisticasProdutos());
	}
}
