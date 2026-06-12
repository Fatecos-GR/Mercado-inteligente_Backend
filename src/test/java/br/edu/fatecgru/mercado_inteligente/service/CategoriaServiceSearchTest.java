package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.fatecgru.mercado_inteligente.model.entity.Categoria;
import br.edu.fatecgru.mercado_inteligente.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceSearchTest {

    @InjectMocks
    private CategoriaService categoriaService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    void buscarPorNome_DeveRetornarLista_QuandoEncontrado() {
        String nome = "Alimentos";
        Categoria c1 = new Categoria(1L, "Alimentos", "Desc", null, null);
        
        when(categoriaRepository.findByNomeContainingIgnoreCase(nome)).thenReturn(List.of(c1));

        List<Categoria> resultado = categoriaService.getByContainingName(nome);

        assertEquals(1, resultado.size());
        assertEquals("Alimentos", resultado.get(0).getNome());
    }
}
