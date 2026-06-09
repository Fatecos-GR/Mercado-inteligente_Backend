package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.mapper.EstoqueMapper;
import br.edu.fatecgru.mercado_inteligente.model.dto.EstoqueAjusteDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.EstoqueResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.MovimentacaoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estoques")
@Tag(name = "Estoque", description = "Endpoints para gestão de estoque e inventário")
@PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @GetMapping
    @Operation(summary = "Listar todo o estoque", description = "Retorna uma lista de todos os registros de estoque no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<EstoqueResponseDTO>> listarTodos() {
        List<EstoqueResponseDTO> estoque = estoqueService.listarTodos().stream()
                .map(EstoqueMapper::toDTO)
                .toList();
        return ResponseEntity.ok(estoque);
    }

    @GetMapping("/produto/{id}")
    @Operation(summary = "Buscar estoque por ID do produto", description = "Busca o saldo atual e reservas de um produto específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque encontrado"),
        @ApiResponse(responseCode = "404", description = "Estoque não encontrado para o produto"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<EstoqueResponseDTO> buscarPorProdutoId(@PathVariable Long id) {
        return ResponseEntity.ok(EstoqueMapper.toDTO(estoqueService.buscarPorProdutoId(id)));
    }

    @GetMapping("/movimentacoes/{produtoId}")
    @Operation(summary = "Buscar histórico de movimentações por ID do produto", description = "Retorna todas as entradas, saídas e reservas registradas para o produto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto ou estoque não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarMovimentacoes(@PathVariable Long produtoId) {
        List<MovimentacaoResponseDTO> movimentacoes = estoqueService.buscarMovimentacoes(produtoId).stream()
                .map(EstoqueMapper::toDTO)
                .toList();
        return ResponseEntity.ok(movimentacoes);
    }

    @PostMapping("/ajuste")
    @Operation(summary = "Realizar ajuste manual de estoque", description = "Permite realizar entradas ou saídas manuais (correções ou novos lotes) registrando o motivo como AJUSTE.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ajuste realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente ou dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> ajustarEstoque(
            @RequestBody @Valid EstoqueAjusteDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        
        estoqueService.executarAjuste(dto.produtoId(), dto.quantidade(), dto.tipo(), usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
