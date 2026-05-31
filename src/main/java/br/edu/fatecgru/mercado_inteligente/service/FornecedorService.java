package br.edu.fatecgru.mercado_inteligente.service;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FornecedorService {
  // Método para listar todos
  @Autowired
  private FornecedorRepository fornecedorRepository;

  public List<Fornecedor> listarTodos() {
    return fornecedorRepository.findAll();
  }

  // Listar pelo ID do fornecedor
  public Fornecedor getById(Long id) {
    return fornecedorRepository.findById(id).orElse(null);
  }

  // Listar produto pelo o nome "contido"
  public List<Fornecedor> getByContainsName(String nome) {
    return fornecedorRepository.findByNomeContains(nome);
  }

  // salvar
  public Fornecedor save(Fornecedor fornecedor) {
    return fornecedorRepository.save(fornecedor);
  }

  // cadastrar
  public Fornecedor cadastrar(FornecedorDTO dto) {
    Fornecedor fornecedor = new Fornecedor();

    fornecedor.setNome(dto.nome());

    Endereco endereco = criarEndereco(dto.endereco());

    fornecedor.setEndereco(endereco);

    return fornecedorRepository.save(fornecedor);
  }

  // atualizar
  public Fornecedor atualizar(Long id, FornecedorDTO dto) {
    Fornecedor fornecedor = fornecedorRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

    fornecedor.setNome(dto.nome());

    Endereco endereco = criarEndereco(dto.endereco());

    fornecedor.setEndereco(endereco);

    return fornecedorRepository.save(fornecedor);
  }

  // deletar
  public void deletar(Long id) {
    fornecedorRepository.deleteById(id);
  }

  // método auxiliar
  private Endereco criarEndereco(EnderecoDTO dto) {
    Endereco endereco = new Endereco();

    endereco.setCep(dto.cep());
    endereco.setLogradouro(dto.logradouro());
    endereco.setNumero(dto.numero());
    endereco.setComplemento(dto.complemento());
    endereco.setBairro(dto.bairro());
    endereco.setCidade(dto.cidade());
    endereco.setEstado(dto.estado());

    return endereco;
  }
}
