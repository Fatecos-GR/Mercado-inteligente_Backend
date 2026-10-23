package br.edu.fatecgru.mercado_inteligente.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.edu.fatecgru.mercado_inteligente.model.dto.DashboardStatsDTO;
import br.edu.fatecgru.mercado_inteligente.service.CategoriaService;
import br.edu.fatecgru.mercado_inteligente.service.FornecedorService;
import br.edu.fatecgru.mercado_inteligente.service.MarcaService;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerUnitTest {

    @InjectMocks
    private DashboardController dashboardController;

    @Mock
    private MarcaService marcaService;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private FornecedorService fornecedorService;

    @BeforeEach
    void setUp() {
        // MockitoExtension já cuida da inicialização
    }

    @Test
    void deveRetornarEstatisticasCorretamente() {
        // Cenário (Given)
        when(marcaService.contar()).thenReturn(15L);
        when(categoriaService.contar()).thenReturn(8L);
        when(fornecedorService.contar()).thenReturn(4L);

        // Ação (When)
        ResponseEntity<DashboardStatsDTO> response = dashboardController.obterEstatisticas();

        // Verificação (Then)
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        DashboardStatsDTO stats = response.getBody();
        assertNotNull(stats);
        assertEquals(15L, stats.quantidadeMarcas());
        assertEquals(8L, stats.quantidadeCategorias());
        assertEquals(4L, stats.quantidadeFornecedores());
    }

    @Test
    void deveRetornarContagemIndividualDeMarcas() {
        when(marcaService.contar()).thenReturn(10L);

        ResponseEntity<Long> response = dashboardController.contarMarcas();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10L, response.getBody());
    }
}
