package br.edu.fatecgru.mercado_inteligente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
@SecurityRequirement(name = "bearer-key")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Operation(summary = "Obtém o carrinho ativo do usuário", 
               description = "Retorna o carrinho atual com status ATIVO e todos os seus itens. Se o carrinho expirou ou não existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrinho encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nenhum carrinho ativo encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @GetMapping
    public ResponseEntity<Carrinho> obterCarrinhoAtivo(@AuthenticationPrincipal Usuario usuario) {
        Carrinho carrinho = carrinhoService.obterCarrinhoAtivo(usuario.getId());
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Adiciona um item ao carrinho", 
               description = "Adiciona um produto ao carrinho ativo do usuário autenticado. Se o produto já existir, a quantidade é somada. O estoque é reservado imediatamente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente ou dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @PostMapping("/itens")
    public ResponseEntity<Carrinho> adicionarItem(
            @AuthenticationPrincipal Usuario usuario, 
            @RequestBody @Valid ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.adicionarItem(usuario.getId(), request);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Atualiza a quantidade de um item no carrinho", 
               description = "Altera a quantidade de um produto já presente no carrinho do usuário autenticado. Ajusta a reserva de estoque proporcionalmente. Se a quantidade for 0, o item é removido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @PutMapping("/itens")
    public ResponseEntity<Carrinho> atualizarQuantidade(
            @AuthenticationPrincipal Usuario usuario, 
            @RequestBody @Valid ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.atualizarQuantidade(usuario.getId(), request);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Remove um item do carrinho", 
               description = "Remove completamente um produto do carrinho do usuário autenticado e libera a quantidade reservada de volta para o estoque disponível.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @DeleteMapping("/itens/{produtoId}")
    public ResponseEntity<Carrinho> removerItem(
            @AuthenticationPrincipal Usuario usuario, 
            @PathVariable Long produtoId) {
        Carrinho carrinho = carrinhoService.removerItem(usuario.getId(), produtoId);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Abandona o carrinho ativo", 
               description = "Muda o status do carrinho ativo do usuário autenticado para ABANDONADO e devolve todos os itens reservados para o estoque disponível.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrinho abandonado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ativo não encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @DeleteMapping
    public ResponseEntity<Carrinho> abandonarCarrinho(@AuthenticationPrincipal Usuario usuario) {
        Carrinho carrinho = carrinhoService.abandonarCarrinho(usuario.getId());
        return ResponseEntity.ok(carrinho);
    }
}
