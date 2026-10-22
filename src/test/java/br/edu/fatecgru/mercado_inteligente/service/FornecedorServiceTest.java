package br.edu.fatecgru.mercado_inteligente.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.exception.ResourceNotFoundException;
import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private ImagemService imagemService;

    @InjectMocks
    private FornecedorService fornecedorService;

    @Mock
    private MultipartFile imagem;

    @Test
    @DisplayName("Deve cadastrar um fornecedor com sucesso quando os dados são válidos")
    void deveCadastrarFornecedorComSucesso() throws Exception {
        // Cenário
        EnderecoDTO enderecoDTO = new EnderecoDTO("07123-000", "Rua Teste", "100", null, "Bairro Teste", "Cidade Teste", "SP");
        FornecedorDTO dto = new FornecedorDTO("Fornecedor Teste", enderecoDTO);
        
        when(imagemService.salvarImagem(any(), anyString())).thenReturn(new ImagemDTO("http://url.com", "publicId"));
        when(fornecedorRepository.save(any(Fornecedor.class))).thenAnswer(i -> i.getArguments()[0]);

        // Ação
        Fornecedor resultado = fornecedorService.cadastrar(dto, imagem);

        // Verificação
        assertNotNull(resultado);
        assertEquals("Fornecedor Teste", resultado.getNome());
        assertEquals("http://url.com", resultado.getImagem());
        verify(fornecedorRepository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar fornecedor inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        // Cenário
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());
        FornecedorDTO dto = new FornecedorDTO("Novo Nome", null);

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> {
            fornecedorService.atualizar(1L, dto, null);
        });
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar fornecedor inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        // Cenário
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> {
            fornecedorService.deletar(1L);
        });
    }
}
