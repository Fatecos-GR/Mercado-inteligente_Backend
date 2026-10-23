-- Refatoração do relacionamento Endereço-Usuário para 1 para 1
-- Adiciona a chave estrangeira na tabela de usuários de forma ÚNICA
ALTER TABLE usuarios ADD COLUMN endereco_id BIGINT UNIQUE;
ALTER TABLE usuarios ADD CONSTRAINT fk_usuario_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos(id) ON DELETE SET NULL;

-- Remove a chave estrangeira antiga da tabela de endereços
ALTER TABLE enderecos DROP FOREIGN KEY fk_endereco_usuario;
ALTER TABLE enderecos DROP COLUMN usuario_id;
