# Plano Completo de Implementação: Autenticação JWT com Spring Security

> **Projeto:** Mercado Inteligente — API REST
> **Stack:** Java 17+ · Spring Boot · Spring Security · Auth0 java-jwt · Bucket4j · SpringDoc OpenAPI
> **Metodologia:** Implementar → Testar → Aprovar → Commit → Próxima etapa

---

## Índice

1. [Objetivo](#1-objetivo)
2. [Arquivos Chave e Contexto](#2-arquivos-chave-e-contexto)
3. [Notas Técnicas Gerais](#3-notas-técnicas-gerais)
4. [Decisões de Escopo](#4-decisões-de-escopo)
5. [Fases da Implementação](#5-fases-da-implementação)
   - [Fase 1 — Dependências e Preparação do Domínio](#fase-1--dependências-e-preparação-do-domínio)
   - [Fase 2 — Serviços Essenciais de Segurança](#fase-2--serviços-essenciais-de-segurança)
   - [Fase 3 — Configuração e Filtros](#fase-3--configuração-e-filtros)
   - [Fase 4 — Endpoints de Autenticação e Adaptação](#fase-4--endpoints-de-autenticação-e-adaptação)
   - [Fase 4.5 — Tratamento de Exceções e Validação](#fase-45--tratamento-de-exceções-e-validação)
   - [Fase 5 — Testes Automatizados](#fase-5--testes-automatizados)
   - [Fase 6 — Documentação com Swagger / OpenAPI](#fase-6--documentação-com-swagger--openapi)
   - [Fase 7 — Hardening e Boas Práticas de Produção](#fase-7--hardening-e-boas-práticas-de-produção)
6. [Checklist Geral](#6-checklist-geral)

---

## 1. Objetivo

Implementar um sistema de autenticação e autorização robusto para a API do Mercado Inteligente utilizando Spring Security e JWT (JSON Web Token). O sistema deve:

- Distinguir permissões entre **Administradores** e **Clientes**
- Garantir **rotas públicas e privadas**
- Ter **tratamento de erros padronizado**
- Ser coberto por **testes automatizados**
- Ter **documentação interativa** via Swagger
- Seguir **boas práticas de segurança** para produção

---

## 2. Arquivos Chave e Contexto

| Arquivo / Pacote | Responsabilidade |
|---|---|
| `pom.xml` | Receberá todas as novas dependências |
| `model.entity.Usuario` | Adaptado para implementar `UserDetails` |
| `application.properties` | Configurações de segurança, JWT e OpenAPI |
| `security/` | Pacote novo: configurações, filtros e serviços de token |
| `exception/` | Pacote novo: tratamento global de erros |
| `model.dto/` | Pacote novo: DTOs de request e response |
| `AuthController` | Rotas públicas de login e registro |
| `UsuarioController` | CRUD administrativo protegido por autenticação |

---

## 3. Notas Técnicas Gerais

### `@EnableMethodSecurity`
Adicionar esta anotação na classe `SecurityConfigurations`. Ela habilita o uso de `@PreAuthorize("hasRole('ADMIN')")` diretamente nos métodos dos Controllers, tornando o controle de acesso por role mais limpo e explícito do que configurar tudo no `filterChain`.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations { ... }
```

---

### `Instant` para expiração do token
Se o projeto usa Java 17+, utilizar `Instant` para definir o tempo de expiração do token é a forma moderna e recomendada. Evitar o uso de `Date` legado.

```java
// Forma correta — Java 17+
Instant expiracao = Instant.now().plus(2, ChronoUnit.HOURS);
```

> ⚠️ **Armadilha UTC:** Servidor e banco de dados devem estar sincronizados em **UTC**. Fusos horários diferentes podem fazer o token nascer já expirado ou durar mais do que o esperado.
>
> Adicionar ao `application.properties`:
> ```properties
> spring.jpa.properties.hibernate.jdbc.time_zone=UTC
> ```
> E garantir que a JVM sobe com:
> ```
> -Duser.timezone=UTC
> ```

---

### Logout — Decisão de Escopo
O JWT é stateless: o servidor não guarda sessão. O **logout será gerenciado pelo frontend** descartando o token localmente. Blacklist com Redis não será implementada neste escopo. Esta decisão deve ser mencionada no `README.md` do projeto.

---

## 4. Decisões de Escopo

| Funcionalidade | Decisão | Justificativa |
|---|---|---|
| Refresh Token | ❌ Fora do escopo | Access Token de 2h é suficiente para o contexto acadêmico |
| Blacklist de tokens (Redis) | ❌ Fora do escopo | Logout gerenciado pelo frontend descartando o token |
| Rate Limiting com Redis | ❌ Fora do escopo | Bucket4j em memória é suficiente para o escopo atual |
| HTTPS | ⚠️ Nota no README | Obrigatório em produção real; configurar no servidor de deploy |

---

## 5. Fases da Implementação

---

### Fase 1 — Dependências e Preparação do Domínio

#### Tarefas

**1. Atualizar `pom.xml`**

Adicionar as seguintes dependências:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (Auth0) -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
</dependency>

<!-- Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- OpenAPI / Swagger -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>

<!-- Bucket4j (Rate Limiting) -->
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

---

**2. Adaptar a entidade `Usuario`**

- Implementar a interface `UserDetails` do Spring Security
- Mapear `TipoUsuario` para `GrantedAuthorities`:
  - `TipoUsuario.ADMIN` → `ROLE_ADMIN`
  - `TipoUsuario.CLIENTE` → `ROLE_CLIENTE`
- Implementar todos os métodos da interface (`isAccountNonExpired`, `isEnabled`, etc.) retornando `true` por padrão

---

**3. Atualizar `UsuarioRepository`**

Adicionar o método de busca por email:

```java
Optional<Usuario> findByEmail(String email);
```

---

#### Testes da Fase 1

Antes de commitar, verificar:

- [ ] O projeto compila sem erros após adicionar as dependências
- [ ] A aplicação sobe normalmente (Spring Security pode bloquear tudo por padrão — é esperado neste momento)
- [ ] O método `findByEmail` funciona via um teste rápido no repositório ou console H2/banco

> ✅ **Aprovado? Então:** `git commit -m "feat: fase 1 - dependencias, UserDetails e findByEmail"`

---

### Fase 2 — Serviços Essenciais de Segurança

#### Tarefas

**4. Criar `TokenService`**

Localização sugerida: `security/service/TokenService.java`

Responsabilidades:
- Gerar o token JWT no momento do login
  - Subject: email do usuário
  - Issuer: nome da aplicação (ex: `"mercado-inteligente"`)
  - Expiração: `Instant.now().plus(2, ChronoUnit.HOURS)` em UTC
- Validar o token recebido nas requisições
- Extrair o email (subject) do token validado

Configuração no `application.properties`:
```properties
api.security.token.secret=${JWT_SECRET}
```

Leitura na classe:
```java
@Value("${api.security.token.secret}")
private String secret;
```

> ⚠️ O valor real de `JWT_SECRET` deve ser definido como variável de ambiente no sistema operacional ou no ambiente de execução. Nunca commitar o valor real no repositório.

---

**5. Criar `AutenticacaoService`**

Localização sugerida: `security/service/AutenticacaoService.java`

Responsabilidades:
- Implementar `UserDetailsService`
- Implementar `loadUserByUsername(String email)`:
  - Buscar o usuário no banco via `findByEmail`
  - Lançar `UsernameNotFoundException` se não encontrado

---

#### Testes da Fase 2

Antes de commitar, verificar:

- [ ] `TokenService` gera um token não nulo para um usuário válido
- [ ] O token gerado contém o email correto no subject (verificar via [jwt.io](https://jwt.io))
- [ ] `TokenService` valida corretamente um token válido
- [ ] `TokenService` rejeita um token com assinatura adulterada
- [ ] `AutenticacaoService` retorna o usuário correto para um email existente
- [ ] `AutenticacaoService` lança exceção para um email inexistente

> ✅ **Aprovado? Então:** `git commit -m "feat: fase 2 - TokenService e AutenticacaoService"`

---

### Fase 3 — Configuração e Filtros

#### Tarefas

**6. Criar `SecurityFilter`**

Localização sugerida: `security/filter/SecurityFilter.java`

- Estender `OncePerRequestFilter`
- Lógica:
  1. Recuperar o token do header `Authorization` (remover o prefixo `"Bearer "`)
  2. Chamar `TokenService` para validar e extrair o email
  3. Buscar o usuário no banco via `AutenticacaoService`
  4. Setar o usuário autenticado no `SecurityContextHolder`
- Tratar tokens inválidos/expirados em `try/catch` — **não lançar exceção direta**, apenas deixar o contexto vazio para o Spring Security retornar 401 automaticamente

---

**7. Criar `SecurityConfigurations`**

Localização sugerida: `security/config/SecurityConfigurations.java`

Configurações:
- `@Configuration` + `@EnableWebSecurity` + `@EnableMethodSecurity`
- `SecurityFilterChain`:
  - Desabilitar CSRF
  - Sessão `STATELESS`
  - Registrar `SecurityFilter` antes do `UsernamePasswordAuthenticationFilter`
  - **Rotas públicas:** `/api/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`
  - **Demais rotas:** exigem autenticação
- **CORS:**
  - `allowedOrigins`: URL específica do frontend (ex: `http://127.0.0.1:5500`)
  - Métodos permitidos: `GET`, `POST`, `PUT`, `DELETE`
  - Headers permitidos: `Authorization`, `Content-Type`
- **Beans:**
  - `AuthenticationManager`
  - `BCryptPasswordEncoder`

---

#### Testes da Fase 3

Antes de commitar, verificar:

- [ ] Acessar qualquer rota protegida sem token → retorna `401 Unauthorized`
- [ ] Acessar qualquer rota protegida com token inválido → retorna `401 Unauthorized`
- [ ] Acessar `/swagger-ui/index.html` sem token → retorna `200 OK` (rota pública)
- [ ] A aplicação sobe sem erros de configuração de beans

> ✅ **Aprovado? Então:** `git commit -m "feat: fase 3 - SecurityFilter e SecurityConfigurations"`

---

### Fase 4 — Endpoints de Autenticação e Adaptação

#### Tarefas

**8. Criar `AuthController`**

Localização sugerida: `controller/AuthController.java`

**`POST /api/auth/login`**
- Receber `LoginRequest` (email, senha)
- Autenticar via `AuthenticationManager`
- Retornar `LoginResponse` contendo o token JWT
- Em caso de falha: logar tentativa (email + timestamp) via `@Slf4j` e retornar `401`

**`POST /api/auth/register`**
- Receber `RegistroRequest` (nome, email, senha)
- ⚠️ **Segurança Crítica:** Forçar `TipoUsuario = CLIENTE` no servidor, ignorando qualquer valor enviado pelo cliente no body
- Criptografar a senha com `BCryptPasswordEncoder` antes de salvar
- Retornar `201 Created` em caso de sucesso

---

**9. Ajustar `UsuarioController`**

- Garantir que métodos administrativos (ex: deletar usuário, listar todos) exijam `ROLE_ADMIN`
- Usar `@PreAuthorize("hasRole('ADMIN')")` nos métodos correspondentes (habilitado pelo `@EnableMethodSecurity`)

---

#### Testes da Fase 4

Antes de commitar, verificar:

- [ ] `POST /api/auth/login` com credenciais válidas → retorna `200 OK` + token no body
- [ ] `POST /api/auth/login` com senha errada → retorna `401 Unauthorized`
- [ ] `POST /api/auth/register` com dados válidos → retorna `201 Created`
- [ ] `POST /api/auth/register` com email já existente → retorna `400 Bad Request`
- [ ] `POST /api/auth/register` enviando `"tipoUsuario": "ADMIN"` no body → usuário criado como `CLIENTE` (teste de segurança crítico)
- [ ] Senha salva no banco está ilegível (hash BCrypt — verificar direto no banco)
- [ ] Rota administrativa do `UsuarioController` bloqueada para token com `ROLE_CLIENTE`

> ✅ **Aprovado? Então:** `git commit -m "feat: fase 4 - AuthController e protecao de rotas administrativas"`

---

### Fase 4.5 — Tratamento de Exceções e Validação

#### Tarefas

**10. Criar `GlobalExceptionHandler`**

Localização sugerida: `exception/GlobalExceptionHandler.java`

- Anotar com `@RestControllerAdvice`
- Mapear as seguintes exceções:

| Exceção | Status HTTP |
|---|---|
| `AuthenticationException` | 401 Unauthorized |
| `AccessDeniedException` | 403 Forbidden |
| `MethodArgumentNotValidException` | 400 Bad Request (com lista de erros por campo) |
| `IllegalArgumentException` | 400 Bad Request |
| `Exception` (genérica) | 500 Internal Server Error |

- Resposta padrão para erros gerais:
```json
{
  "status": 400,
  "erro": "Mensagem descritiva do erro",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

- Resposta para `MethodArgumentNotValidException` — retornar **lista de erros por campo** para facilitar o consumo pelo frontend (React pode exibir o erro diretamente no input correspondente):
```json
{
  "status": 400,
  "timestamp": "2025-01-01T00:00:00Z",
  "erros": [
    { "campo": "email", "mensagem": "formato inválido" },
    { "campo": "senha", "mensagem": "deve ter no mínimo 6 caracteres" }
  ]
}
```
Extrair os erros via `ex.getBindingResult().getFieldErrors()`, mapeando `getField()` e `getDefaultMessage()`.

---

**11. Adicionar validação nos DTOs**

Anotar os campos de `LoginRequest` e `RegistroRequest`:

```java
public class RegistroRequest {
    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;
}
```

Anotar os parâmetros dos endpoints com `@Valid`:

```java
public ResponseEntity<?> register(@RequestBody @Valid RegistroRequest request) { ... }
```

---

#### Testes da Fase 4.5

Antes de commitar, verificar:

- [ ] `POST /api/auth/register` sem o campo `email` → retorna `400` com `"campo": "email"` na lista de erros
- [ ] `POST /api/auth/register` com email em formato inválido (ex: `"abc"`) → retorna `400` com mensagem de formato
- [ ] `POST /api/auth/register` com senha de 3 caracteres → retorna `400` com mensagem de tamanho mínimo
- [ ] Acessar rota de Admin com token de Cliente → retorna `403 Forbidden` no formato JSON padronizado
- [ ] Simular erro interno → retorna `500` no formato JSON padronizado (sem stacktrace exposto)

> ✅ **Aprovado? Então:** `git commit -m "feat: fase 4.5 - GlobalExceptionHandler e validacao nos DTOs"`

---

### Fase 5 — Testes Automatizados

#### Tarefas

**12. Testes unitários do `TokenService`**

- Geração: token gerado não é nulo e contém o email correto no subject
- Validação de token válido: retorna o email do usuário
- Validação de token expirado: deve falhar apropriadamente
- Validação de token com assinatura adulterada: deve falhar a verificação

**13. Testes unitários do `AutenticacaoService`**

- `loadUserByUsername` com email existente → retorna o `UserDetails` correto
- `loadUserByUsername` com email inexistente → lança `UsernameNotFoundException`

**14. Testes de integração do `AuthController`**

Usar `@SpringBootTest` + `MockMvc`:

- `POST /api/auth/login` com credenciais válidas → `200 OK` + token
- `POST /api/auth/login` com senha errada → `401 Unauthorized`
- `POST /api/auth/register` com dados válidos → `201 Created`
- `POST /api/auth/register` com email já existente → `400 Bad Request`
- `POST /api/auth/register` com campos inválidos → `400` com lista de erros por campo
- `POST /api/auth/register` enviando `"tipoUsuario": "ADMIN"` no body → usuário criado como `CLIENTE`

**15. Testes de autorização por role**

- Rota de Admin acessada com token de Cliente → `403 Forbidden`
- Rota protegida acessada sem token → `401 Unauthorized`
- Rota protegida acessada com token válido de Cliente → `200 OK`

---

#### Testes da Fase 5

Antes de commitar, verificar:

- [ ] Todos os testes unitários passam (`mvn test`)
- [ ] Todos os testes de integração passam
- [ ] Cobertura de testes cobre os cenários críticos de segurança

> ✅ **Aprovado? Então:** `git commit -m "test: fase 5 - testes unitarios e de integracao"`

---

### Fase 6 — Documentação com Swagger / OpenAPI

#### Tarefas

**16. Configurar `SecurityScheme` no Swagger**

Criar um bean `OpenAPI` que registra o esquema de autenticação Bearer:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Mercado Inteligente API").version("1.0"))
        .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
        .components(new Components()
            .addSecuritySchemes("bearer-key",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
}
```

**17. Documentar os endpoints do `AuthController`**

- Anotar a classe com `@Tag(name = "Autenticação")`
- Anotar cada endpoint com `@Operation(summary = "...")` e `@ApiResponses`
- Anotar os campos dos DTOs com `@Schema(description = "...")`

**18. Liberar rotas do Swagger no `SecurityConfigurations`** *(já previsto na Fase 3)*

Confirmar que `/swagger-ui/**` e `/v3/api-docs/**` estão nas rotas públicas.

---

#### Testes da Fase 6

Antes de commitar, verificar:

- [ ] Acessar `http://localhost:8080/swagger-ui/index.html` → página carrega corretamente
- [ ] Endpoints do `AuthController` aparecem documentados
- [ ] O botão "Authorize" aparece no Swagger UI para informar o token Bearer
- [ ] É possível testar um endpoint protegido diretamente pelo Swagger UI informando o token

> ✅ **Aprovado? Então:** `git commit -m "docs: fase 6 - documentacao Swagger com suporte a Bearer Token"`

---

### Fase 7 — Hardening e Boas Práticas de Produção

#### Tarefas

**19. Rate Limiting no endpoint de login**

- Biblioteca: `Bucket4j` (já adicionada no `pom.xml` na Fase 1)
- Estratégia: `Map<String, Bucket>` em memória, com controle por IP
- Recuperar o IP via `HttpServletRequest.getRemoteAddr()`
- Limite sugerido: **5 tentativas por minuto por IP**
- Retornar `429 Too Many Requests` quando o limite for atingido
- Objetivo: prevenir ataques de brute force no endpoint de login

**20. Gestão de segredos**

- Confirmar que `application.properties` contém apenas `${JWT_SECRET}` — sem valor real
- Confirmar que `application.properties` **não está** no `.gitignore` (o arquivo de configuração pode ser versionado, mas sem segredos)
- Confirmar que nenhum segredo foi commitado no histórico do Git (`git log` + `git diff`)
- Documentar no `README.md` como configurar a variável de ambiente `JWT_SECRET` localmente

**21. Nota sobre HTTPS**

- Documentar no `README.md` que HTTPS é obrigatório em produção
- Em ambiente local/desenvolvimento, HTTP é aceitável
- Em deploy (ex: Railway, Render, AWS), garantir que o servidor ou proxy reverso (Nginx) force HTTPS

---

#### Testes da Fase 7

Antes de commitar, verificar:

- [ ] Fazer mais de 5 tentativas de login em menos de 1 minuto pelo mesmo IP → retorna `429 Too Many Requests`
- [ ] Após 1 minuto, o acesso é liberado novamente
- [ ] Confirmar que `JWT_SECRET` não aparece em nenhum arquivo versionado (`git grep JWT_SECRET`)
- [ ] `README.md` contém instruções claras de configuração do ambiente

> ✅ **Aprovado? Então:** `git commit -m "feat: fase 7 - rate limiting, gestao de segredos e documentacao de producao"`

---

## 6. Checklist Geral

| Fase | Descrição | Status |
|---|---|---|
| 1 | Dependências e Preparação do Domínio | 🔲 Pendente |
| 2 | Serviços Essenciais de Segurança | 🔲 Pendente |
| 3 | Configuração e Filtros | 🔲 Pendente |
| 4 | Endpoints de Autenticação e Adaptação | 🔲 Pendente |
| 4.5 | Tratamento de Exceções e Validação | 🔲 Pendente |
| 5 | Testes Automatizados | 🔲 Pendente |
| 6 | Documentação com Swagger / OpenAPI | 🔲 Pendente |
| 7 | Hardening e Boas Práticas de Produção | 🔲 Pendente |