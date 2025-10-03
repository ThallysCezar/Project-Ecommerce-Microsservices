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

CREATE TABLE IF NOT EXISTS public.t_pagamentos (
    id BIGSERIAL PRIMARY KEY,  -- auto incremento no Postgres
    valor NUMERIC(19,2) NOT NULL,
    nome VARCHAR(100),
    numero VARCHAR(19),
    expiracao VARCHAR(7) NOT NULL,
    codigo VARCHAR(3),
    status VARCHAR(255) NOT NULL,
    forma_de_pagamento_id BIGINT NOT NULL,
    pedido_id BIGINT NOT NULL
);

--model_name character varying(200) COLLATE pg_catalog."default" NOT NULL