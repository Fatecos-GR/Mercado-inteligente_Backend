package br.edu.fatecgru.mercado_inteligente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.dto.CarrinhoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoRequest;
import br.edu.fatecgru.mercado_inteligente.model.dto.ItemCarrinhoResponseDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
@SecurityRequirement(name = "bearer-key")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Operation(summary = "Obtém o carrinho ativo do usuário", 
               description = "Retorna o carrinho atual com status ATIVO, todos os seus itens e o valor total calculado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrinho encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nenhum carrinho ativo encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @GetMapping
    public ResponseEntity<CarrinhoResponseDTO> obterCarrinhoAtivo(@AuthenticationPrincipal Usuario usuario) {
        Carrinho carrinho = carrinhoService.obterCarrinhoAtivo(usuario.getId());
        return ResponseEntity.ok(converterParaDTO(carrinho));
    }

    @Operation(summary = "Adiciona um item ao carrinho", 
               description = "Adiciona um produto ao carrinho ativo do usuário. O estoque é reservado e o preço é congelado no momento da adição.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente ou dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido")
    })
    @PostMapping("/itens")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItem(
            @AuthenticationPrincipal Usuario usuario, 
            @RequestBody @Valid ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.adicionarItem(usuario.getId(), request);
        return ResponseEntity.ok(converterParaDTO(carrinho));
    }

    @Operation(summary = "Atualiza a quantidade de um item no carrinho", 
               description = "Altera a quantidade e ajusta a reserva de estoque. Se a quantidade for 0, o item é removido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    @PutMapping("/itens")
    public ResponseEntity<CarrinhoResponseDTO> atualizarQuantidade(
            @AuthenticationPrincipal Usuario usuario, 
            @RequestBody @Valid ItemCarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.atualizarQuantidade(usuario.getId(), request);
        return ResponseEntity.ok(converterParaDTO(carrinho));
    }

    @Operation(summary = "Remove um item do carrinho", 
               description = "Remove o produto e libera a quantidade reservada de volta para o estoque disponível.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    @DeleteMapping("/itens/{produtoId}")
    public ResponseEntity<CarrinhoResponseDTO> removerItem(
            @AuthenticationPrincipal Usuario usuario, 
            @Parameter(description = "ID do produto a ser removido", required = true) @PathVariable Long produtoId) {
        Carrinho carrinho = carrinhoService.removerItem(usuario.getId(), produtoId);
        return ResponseEntity.ok(converterParaDTO(carrinho));
    }

    @Operation(summary = "Abandona o carrinho ativo", 
               description = "Muda o status para ABANDONADO e devolve todos os itens ao estoque disponível.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carrinho abandonado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ativo não encontrado")
    })
    @DeleteMapping
    public ResponseEntity<CarrinhoResponseDTO> abandonarCarrinho(@AuthenticationPrincipal Usuario usuario) {
        Carrinho carrinho = carrinhoService.abandonarCarrinho(usuario.getId());
        return ResponseEntity.ok(converterParaDTO(carrinho));
    }

    @Operation(summary = "Finaliza o pedido (Checkout)", 
               description = "Converte o carrinho ativo em um pedido finalizado. Exige um endereço de entrega vinculado ao usuário.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido finalizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou carrinho vazio"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou endereço não encontrado")
    })
    @PostMapping("/checkout/{enderecoId}")
    public ResponseEntity<CarrinhoResponseDTO> finalizarPedido(
            @AuthenticationPrincipal Usuario usuario,
            @Parameter(description = "ID do endereço de entrega", required = true) @PathVariable Long enderecoId) {
        Carrinho carrinho = carrinhoService.finalizarPedido(usuario.getId(), enderecoId);
        return ResponseEntity.ok(converterParaDTO(carrinho));
    }

    private CarrinhoResponseDTO converterParaDTO(Carrinho carrinho) {
        List<ItemCarrinhoResponseDTO> itensDTO = carrinho.getItens().stream()
                .map(item -> new ItemCarrinhoResponseDTO(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnidade(),
                        item.getPrecoUnidade().multiply(BigDecimal.valueOf(item.getQuantidade()))
                ))
                .collect(Collectors.toList());

        BigDecimal valorTotal = itensDTO.stream()
                .map(ItemCarrinhoResponseDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarrinhoResponseDTO(
                carrinho.getId(),
                itensDTO,
                valorTotal,
                carrinho.getCriadoEm(),
                carrinho.getAtualizadoEm(),
                carrinho.getStatus()
        );
    }
}
