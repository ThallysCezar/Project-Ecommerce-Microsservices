-- Criando tabela de modelo
--CREATE TABLE IF NOT EXISTS t_pagamentos
--(
--    id bigint(20) NOT NULL AUTO_INCREMENT,
--    valor decimal(19,2) NOT NULL,
--    nome varchar(100) DEFAULT NULL,
--    numero varchar(19) DEFAULT NULL,
--    expiracao varchar(7) NOT NULL,
--    codigo varchar(3) DEFAULT NULL,
--    status varchar(255) NOT NULL,
--    forma_de_pagamento_id bigint(20) NOT NULL,
--    pedido_id bigint(20) NOT NULL,
--    PRIMARY KEY (id)
--);

-- Migration para criar tabelas de pedidos e itens no PostgreSQL

CREATE TABLE t_pedidos (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE t_item_do_pedido (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255),
    quantidade INT NOT NULL,
    pedido_id BIGINT NOT NULL,
    CONSTRAINT fk_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);