# Relatório de Correções - Cadastro de Fornecedores
**Projeto:** Mercado Inteligente
**Data:** 15 de Junho de 2026

## 1. O Problema Original (Erro 500)
O erro ocorria ao tentar cadastrar um fornecedor sem enviar o objeto de endereço no JSON.
- **Causa Raiz:** O código no `FornecedorService` tentava acessar propriedades de um objeto nulo (`dto.endereco().cep()`), gerando um `NullPointerException`.
- **Sintoma:** O servidor "quebrava" internamente e retornava uma mensagem genérica de erro 500.

## 2. Correções Implementadas

### A. Validação e Segurança (Camada de DTO e Service)
- **Obrigatariedade de Endereço:** Adicionada a anotação `@NotNull` no campo `endereco` do `FornecedorDTO`. Agora o Spring bloqueia a requisição e retorna **400 Bad Request** com uma mensagem clara se o endereço faltar.
- **Uso de Mapper Seguro:** Substituída a criação manual de endereços no `FornecedorService` pelo `EnderecoMapper.toEntity()`. O Mapper já possui verificações internas que evitam erros de ponteiro nulo.
- **Transacionalidade:** Adicionada a anotação `@Transactional` nos métodos de salvar, atualizar e deletar, garantindo que o banco de dados e o Cloudinary fiquem sempre sincronizados.

### B. Gestão de Erros e Banco de Dados
- **Status HTTP Corretos:** Substituída a `RuntimeException` por `ResourceNotFoundException`. Agora, ao buscar, atualizar ou deletar um fornecedor inexistente, o sistema retorna **404 Not Found** em vez de 500.
- **Limpeza de Órfãos:** Configurado `orphanRemoval = true` na entidade `Fornecedor`. Isso evita que endereços antigos fiquem "jogados" no banco de dados após uma atualização de endereço.
- **Configuração Cloudinary:** As credenciais foram devidamente configuradas no `application.properties`, permitindo o upload real de imagens.

### C. Estabilização do Ambiente de Testes
- **Injeção de Admin Automática:** Para evitar problemas com scripts SQL (`import.sql`) ou bancos vazios no Docker, foi criado um `CommandLineRunner` na classe principal (`MercadoInteligenteApplication`).
- **Usuário de Teste:** O sistema agora garante a existência do usuário `admin@mercado.com` (senha: `senha123`) com perfil de **ADMIN** toda vez que inicia.
- **Porta do Banco:** Ajustada a conexão para a porta **3307**, que é a porta real exposta pelo Docker Desktop.

## 3. Como Testar Novamente
1. **Login:** `POST /api/auth/login` com `admin@mercado.com` / `senha123`.
2. **Cadastro (Erro):** Enviar JSON de fornecedor sem o campo `endereco` -> Deve retornar **400**.
3. **Cadastro (Sucesso):** Enviar JSON completo com `nome` e `endereco` -> Deve retornar **201**.

---
*Documento gerado para registro de melhorias técnicas e estabilidade do sistema.*
