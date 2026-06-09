-- População de Dados Iniciais Expandida - Mercado Inteligente

-- 1. Categorias
INSERT INTO categorias (nome, descricao) VALUES ('Alimentos', 'Produtos alimentícios em geral');
INSERT INTO categorias (nome, descricao) VALUES ('Bebidas', 'Sucos, refrigerantes e águas');
INSERT INTO categorias (nome, descricao) VALUES ('Limpeza', 'Produtos para higiene da casa');
INSERT INTO categorias (nome, descricao) VALUES ('Higiene Pessoal', 'Shampoos, sabonetes e higiene bucal');
INSERT INTO categorias (nome, descricao) VALUES ('Padaria', 'Pães, bolos e salgados');
INSERT INTO categorias (nome, descricao) VALUES ('Hortifruti', 'Frutas, legumes e verduras');

-- 2. Marcas
INSERT INTO marcas (nome, descricao) VALUES ('Nestlé', 'Produtos alimentícios variados');
INSERT INTO marcas (nome, descricao) VALUES ('Coca-Cola', 'Bebidas refrescantes');
INSERT INTO marcas (nome, descricao) VALUES ('Omo', 'Sabão em pó e líquido');
INSERT INTO marcas (nome, descricao) VALUES ('P&G', 'Higiene e cuidados pessoais');
INSERT INTO marcas (nome, descricao) VALUES ('Unilever', 'Bens de consumo variados');
INSERT INTO marcas (nome, descricao) VALUES ('Ambev', 'Cervejas e bebidas não alcoólicas');
INSERT INTO marcas (nome, descricao) VALUES ('Ambev (Horti)', 'Fornecedor de frutas e legumes');

-- 3. Usuários e Funcionários
-- Senha padrão para todos: 'senha123' (Criptografada com BCrypt)
-- Admins
INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) VALUES ('Admin', 'Principal', 'admin@mercado.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11999999999');
INSERT INTO funcionarios (id, tipo_funcionario) VALUES (1, 'ADMIN');

INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) VALUES ('Gerente', 'Ana', 'ana@mercado.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11911112222');
INSERT INTO funcionarios (id, tipo_funcionario) VALUES (2, 'ADMIN');

-- Estoquistas
INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) VALUES ('João', 'Estoque', 'estoque@mercado.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11888888888');
INSERT INTO funcionarios (id, tipo_funcionario) VALUES (3, 'ESTOQUISTA');

INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) VALUES ('Marcos', 'Almoxarifado', 'marcos@mercado.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11877776666');
INSERT INTO funcionarios (id, tipo_funcionario) VALUES (4, 'ESTOQUISTA');

-- Clientes
INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) VALUES ('Pedro', 'Cliente', 'pedro@gmail.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11777777777');
INSERT INTO usuarios (nome, sobrenome, email, senha, telefone) VALUES ('Maria', 'Silva', 'maria@gmail.com', '$2a$10$Y50UaMFOxteibQEYfDj6oeR8.88E4H3PZ/6p1U8D7vT.pLp1u8.p2', '11766665555');

-- 4. Endereços
-- Endereços de Usuários (ID 1, 2, 3)
INSERT INTO enderecos (cep, logradouro, numero, bairro, cidade, estado, usuario_id) VALUES ('01001-000', 'Praça da Sé', '100', 'Sé', 'São Paulo', 'SP', 1);
INSERT INTO enderecos (cep, logradouro, numero, bairro, cidade, estado, usuario_id) VALUES ('07124-000', 'Rua de Teste', '500', 'Centro', 'Guarulhos', 'SP', 5);
INSERT INTO enderecos (cep, logradouro, numero, bairro, cidade, estado, usuario_id) VALUES ('04571-010', 'Avenida Engenheiro Luís Carlos Berrini', '1000', 'Brooklin', 'São Paulo', 'SP', 6);

-- Endereços de Fornecedores (ID 4, 5) - Sem usuario_id
INSERT INTO enderecos (cep, logradouro, numero, bairro, cidade, estado, usuario_id) VALUES ('01310-100', 'Avenida Paulista', '1500', 'Bela Vista', 'São Paulo', 'SP', NULL);
INSERT INTO enderecos (cep, logradouro, numero, bairro, cidade, estado, usuario_id) VALUES ('20031-170', 'Rua do Passeio', '40', 'Centro', 'Rio de Janeiro', 'RJ', NULL);

-- 5. Fornecedores
-- Vinculados aos endereços 4 e 5
INSERT INTO fornecedores (nome, endereco_id) VALUES ('Distribuidora Central ABC', 4);
INSERT INTO fornecedores (nome, endereco_id) VALUES ('Logística Global Sul', 5);

-- 6. Produtos
-- Alimentos
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Arroz 5kg', 'Arroz agulhinha tipo 1', 25.90, '2027-12-31', 1, 1, 1);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Feijão Carioca 1kg', 'Feijão carioca novo', 8.50, '2026-11-20', 1, 5, 2);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Macarrão Espaguete', 'Macarrão de sêmola 500g', 4.20, '2027-05-15', 1, 1, 1);

-- Bebidas
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Coca-Cola 2L', 'Refrigerante de cola', 9.50, '2026-06-30', 2, 2, 1);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Cerveja Skol 350ml', 'Cerveja pilsen lata', 3.80, '2026-03-10', 2, 6, 2);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Suco de Laranja 1L', 'Suco de fruta integral', 12.00, '2026-02-28', 2, 1, 2);

-- Limpeza
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Sabão em Pó Omo 1kg', 'Sabão para lavar roupas', 18.90, '2028-01-01', 3, 3, 1);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Detergente Líquido', 'Detergente neutro 500ml', 2.50, '2027-10-10', 3, 5, 1);

-- Higiene Pessoal
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Creme Dental', 'Creme dental proteção total', 5.50, '2027-08-20', 4, 4, 2);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Shampoo 400ml', 'Shampoo brilho intenso', 15.00, '2027-12-15', 4, 4, 2);

-- Padaria e Hortifruti
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Pão de Forma', 'Pão de forma tradicional', 7.50, '2026-01-15', 5, 5, 1);
INSERT INTO produtos (nome, descricao, preco, validade, categoria_id, marca_id, fornecedor_id) 
VALUES ('Maçã Gala (kg)', 'Maçã nacional selecionada', 10.90, '2026-01-30', 6, 7, 2);

-- 7. Estoque Inicial
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (1, 100, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (2, 150, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (3, 200, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (4, 80, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (5, 500, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (6, 60, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (7, 120, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (8, 300, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (9, 90, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (10, 45, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (11, 30, 0, NOW());
INSERT INTO estoques (produto_id, quantidade_disponivel, quantidade_reservada, atualizado_em) VALUES (12, 50, 0, NOW());
