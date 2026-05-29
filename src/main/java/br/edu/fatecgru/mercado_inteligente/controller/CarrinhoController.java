package br.edu.fatecgru.mercado_inteligente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Operation(summary = "Adiciona um item ao carrinho", 
               description = "Adiciona um produto ao carrinho ativo do usuário. Se o produto já existir, a quantidade é somada. O estoque é reservado imediatamente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente ou dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto ou usuário não encontrado")
    })
    @PostMapping("/{usuarioId}/itens")
    public ResponseEntity<Carrinho> adicionarItem(
            @PathVariable Long usuarioId, 
            @RequestBody @Valid ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.adicionarItem(usuarioId, request);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Atualiza a quantidade de um item no carrinho", 
               description = "Altera a quantidade de um produto já presente no carrinho. Ajusta a reserva de estoque proporcionalmente. Se a quantidade for 0, o item é removido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    @PutMapping("/{usuarioId}/itens")
    public ResponseEntity<Carrinho> atualizarQuantidade(
            @PathVariable Long usuarioId, 
            @RequestBody @Valid ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.atualizarQuantidade(usuarioId, request);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Remove um item do carrinho", 
               description = "Remove completamente um produto do carrinho e libera a quantidade reservada de volta para o estoque disponível.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    @DeleteMapping("/{usuarioId}/itens/{produtoId}")
    public ResponseEntity<Carrinho> removerItem(
            @PathVariable Long usuarioId, 
            @PathVariable Long produtoId) {
        Carrinho carrinho = carrinhoService.removerItem(usuarioId, produtoId);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Abandona o carrinho ativo", 
               description = "Muda o status do carrinho ativo do usuário para ABANDONADO e devolve todos os itens reservados para o estoque disponível.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrinho abandonado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ativo não encontrado")
    })
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Carrinho> abandonarCarrinho(@PathVariable Long usuarioId) {
        Carrinho carrinho = carrinhoService.abandonarCarrinho(usuarioId);
        return ResponseEntity.ok(carrinho);
    }
}
