-- Script Inicial de Criação de Tabelas e Inserção de Admin
-- Gerenciado pelo Flyway

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    sobrenome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL,
    imagem VARCHAR(500),
    public_id_imagem VARCHAR(255)
);

CREATE TABLE funcionarios (
    id BIGINT PRIMARY KEY,
    tipo_funcionario ENUM('ADMIN', 'ESTOQUISTA') NOT NULL,
    CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    imagem VARCHAR(500),
    public_id_imagem VARCHAR(255)
);

CREATE TABLE marcas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    imagem VARCHAR(500),
    public_id_imagem VARCHAR(255)
);

CREATE TABLE enderecos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cep VARCHAR(255) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    bairro VARCHAR(255) NOT NULL,
    numero VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    complemento VARCHAR(255),
    usuario_id BIGINT,
    CONSTRAINT fk_endereco_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE fornecedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    imagem VARCHAR(500),
    public_id_imagem VARCHAR(255),
    endereco_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_fornecedor_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos(id) ON DELETE CASCADE
);

-- Inserção do Admin Inicial
-- Senha: senha123
INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) 
VALUES ('Admin', 'Sistema', 'admin@mercado.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11999999999');

INSERT INTO funcionarios (id, tipo_funcionario) 
VALUES (LAST_INSERT_ID(), 'ADMIN');
