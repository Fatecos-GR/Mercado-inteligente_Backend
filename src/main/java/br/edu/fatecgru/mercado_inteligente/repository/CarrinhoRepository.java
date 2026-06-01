package br.edu.fatecgru.mercado_inteligente.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatecgru.mercado_inteligente.model.entity.Carrinho;
import br.edu.fatecgru.mercado_inteligente.model.entity.StatusCarrinho;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioIdAndStatus(Long usuarioId, StatusCarrinho status);
    
    List<Carrinho> findAllByStatusAndAtualizadoEmBefore(StatusCarrinho status, LocalDateTime horario);
}
