# Mercado Inteligente - Project Context

## Project Overview
**Mercado Inteligente** is a Java-based backend application built with **Spring Boot** (Version 4.0.5 in `pom.xml`, likely a custom or experimental version as of now). It serves as a management system for a "Smart Market," handling entities such as Users, Products, Stock (Estoque), Inventory Movements (MovimentacaoEstoque), Shopping Carts (Carrinho), and Suppliers (Fornecedor).

### Key Technologies
- **Language:** Java 21
- **Framework:** Spring Boot 
- **Persistence:** Spring Data JPA with Hibernate
- **Database:** MySQL 8 (Production/Development), H2 (Testing)
- **API Documentation:** SpringDoc OpenAPI (Swagger/UI)
- **Containerization:** Docker & Docker Compose
- **Build Tool:** Maven (using `mvnw` wrapper)
- **Utilities:** Lombok (available in `pom.xml`, though some classes use manual getters/setters)

### Architecture
The project follows a standard layered architecture:
- **`controller`**: REST endpoints using `@RestController`. Cross-origin is generally enabled for all origins.
- **`service`**: Business logic layer.
- **`repository`**: Data access layer using Spring Data JPA interfaces.
- **`model.entity`**: JPA entities representing database tables.
- **`model.dto`**: Data Transfer Objects for API requests/responses, often including Jakarta Validation constraints.
- **`mapper`**: Manual mapping logic between Entities and DTOs.

---

## Building and Running

### Prerequisites
- **Java 21** or higher.
- **Docker** (optional, for running with Docker Compose).

### Development Setup
1. **Database:** The project expects a MySQL database named `mercadobd`.
   - You can use the provided `compose.yaml` to spin up a MySQL instance on port `3307`.
   - `docker-compose up -d`

2. **Configuration:** Environment variables for database connection:
   - `SPRING_DATASOURCE_URL` (default: `jdbc:mysql://localhost:3306/mercadobd`)
   - `SPRING_DATASOURCE_USERNAME` (default: `root`)
   - `SPRING_DATASOURCE_PASSWORD` (default: empty)

### Key Commands
- **Run Application:**
  ```powershell
  ./mvnw spring-boot:run
  ```
- **Build Project:**
  ```powershell
  ./mvnw clean install
  ```
- **Run Tests:**
  ```powershell
  ./mvnw test
  ```
- **Swagger Documentation:**
  - Once running, access: `http://localhost:8080/swagger-ui.html`

---

## Development Conventions

### Coding Style
- **Package Structure:** Organised by layer (`controller`, `service`, `repository`, `model`).
- **Entity Names:** Use Portuguese names (e.g., `Usuario`, `Produto`, `Estoque`).
- **Mapping:** Prefer manual mapping in the `mapper` package instead of automated tools like MapStruct, unless otherwise specified.
- **Validation:** Use Jakarta Validation annotations (e.g., `@Pattern`, `@NotNull`) in DTOs.

### Testing Practices
- The project uses **H2** for testing purposes, as configured in `src/test/resources/application-test.properties` and `pom.xml`.
- Standard Spring Boot Test setup with `MercadoInteligenteApplicationTests`.

### API Design
- Endpoints are prefixed with `/api/` (e.g., `/api/usuarios`).
- Use appropriate HTTP methods: `GET` for retrieval, `POST` for creation, etc.
- CORS is enabled globally on controllers using `@CrossOrigin(origins = "*")`.
