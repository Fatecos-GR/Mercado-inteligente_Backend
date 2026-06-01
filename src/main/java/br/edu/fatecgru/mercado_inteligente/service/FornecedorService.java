package br.edu.fatecgru.mercado_inteligente.service;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.fatecgru.mercado_inteligente.model.dto.EnderecoDTO;
import br.edu.fatecgru.mercado_inteligente.model.dto.FornecedorDTO;
import br.edu.fatecgru.mercado_inteligente.model.entity.Endereco;
import br.edu.fatecgru.mercado_inteligente.model.entity.Fornecedor;
import br.edu.fatecgru.mercado_inteligente.repository.FornecedorRepository;

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

	@Autowired
	private ImagemService imagemService;

	public List<Fornecedor> listarTodos() {
		return fornecedorRepository.findAll();
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

	// salvar
	public Fornecedor save(Fornecedor fornecedor) {
		return fornecedorRepository.save(fornecedor);
	}

	// cadastrar
	public Fornecedor cadastrar(FornecedorDTO dto, MultipartFile imagem) throws Exception {

		Fornecedor fornecedor = new Fornecedor();

		fornecedor.setNome(dto.getNome());

		Endereco endereco = criarEndereco(dto.getEndereco());

		fornecedor.setEndereco(endereco);

		String nomeImagem = imagemService.salvarImagem(imagem, "suppliers/");

		if (nomeImagem != null) {
			fornecedor.setImagem(nomeImagem);
		}

		return fornecedorRepository.save(fornecedor);

	}

	// atualizar
	public Fornecedor atualizar(Long id, FornecedorDTO dto, MultipartFile imagem) throws Exception {

		Fornecedor fornecedor = fornecedorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

		fornecedor.setNome(dto.getNome());

		Endereco endereco = criarEndereco(dto.getEndereco());

		fornecedor.setEndereco(endereco);

		String imagemAtualizada = imagemService.substituirImagem(fornecedor.getImagem(), imagem, "suppliers/");

		fornecedor.setImagem(imagemAtualizada);

		return fornecedorRepository.save(fornecedor);

	}

	// deletar
	public void deletar(Long id) {

		Fornecedor fornecedor = fornecedorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

		if (fornecedor.getImagem() != null) {

			imagemService.deletarImagem(fornecedor.getImagem(), "suppliers/");
		}

		fornecedorRepository.delete(fornecedor);

	}

	// método auxiliar para criar endereço
	private Endereco criarEndereco(EnderecoDTO dto) {

		Endereco endereco = new Endereco();

		endereco.setCep(dto.getCep());
		endereco.setLogradouro(dto.getLogradouro());
		endereco.setNumero(dto.getNumero());
		endereco.setComplemento(dto.getComplemento());
		endereco.setBairro(dto.getBairro());
		endereco.setCidade(dto.getCidade());
		endereco.setEstado(dto.getEstado());

		return endereco;
	}

}
