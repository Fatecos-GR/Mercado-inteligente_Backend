ALTER TABLE carrinhos ADD COLUMN endereco_entrega_id BIGINT;
ALTER TABLE carrinhos ADD CONSTRAINT FK_CARRINHO_ENDERECO FOREIGN KEY (endereco_entrega_id) REFERENCES enderecos(id);