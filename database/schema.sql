-- =====================================================================
-- Sistema GranMestre Restaurante - estrutura do banco de dados
--
-- Cria os schemas, tabelas, chaves e restricoes em um PostgreSQL VAZIO.
-- O Spring esta com ddl-auto=none, entao ele NAO cria as tabelas sozinho.
--
-- Como usar:
--   1) createdb -U postgres restaurante
--   2) psql -U postgres -d restaurante -f database/schema.sql
--
-- Este script nao e idempotente: rode-o apenas em um banco novo.
-- Nao inclui dados. Para criar o primeiro usuario ADMIN, rode tambem database/dados-iniciais.sql.
-- =====================================================================

CREATE SCHEMA cadastros;
CREATE SCHEMA transacoes;


-- ---------------------------------------------------------------------
-- SCHEMA cadastros
-- ---------------------------------------------------------------------

CREATE TABLE cadastros.usuarios (
    id           SERIAL PRIMARY KEY,
    nome         VARCHAR,
    usuario      VARCHAR,                -- login
    senha        VARCHAR,                -- hash BCrypt, nunca texto puro
    tipo_usuario VARCHAR,                -- ADMIN, ATENDENTE ou COZINHEIRO (enum TipoUsuario)
    ativo        BOOLEAN,
    CONSTRAINT usuarios_usuario_key UNIQUE (usuario)   -- login unico
);

CREATE TABLE cadastros.tipos_marmita (
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR,
    descricao VARCHAR,
    preco     NUMERIC(10,2),
    ativo     BOOLEAN
);

CREATE TABLE cadastros.acompanhamentos (
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR,
    descricao VARCHAR,
    ativo     BOOLEAN
);


-- ---------------------------------------------------------------------
-- SCHEMA transacoes
-- ---------------------------------------------------------------------

-- Gera o numero do pedido (RN002): sequencial global, nunca se repete
CREATE SEQUENCE transacoes.pedido_numero_seq START 1;

CREATE TABLE transacoes.pedidos (
    id              SERIAL PRIMARY KEY,
    numero_pedido   INTEGER,             -- vem de pedido_numero_seq
    data_hora       TIMESTAMP,
    status          VARCHAR,             -- REGISTRADO, ENVIADO_PARA_COZINHA, EM_PREPARO, PRONTO, FINALIZADO
    tipo_marmita_id INTEGER REFERENCES cadastros.tipos_marmita (id),
    observacao      VARCHAR,
    usuario_id      INTEGER REFERENCES cadastros.usuarios (id),   -- atendente
    impresso        BOOLEAN DEFAULT FALSE,
    CONSTRAINT pedidos_numero_pedido_key UNIQUE (numero_pedido)
);

CREATE TABLE transacoes.itens_pedido (
    id                SERIAL PRIMARY KEY,
    pedido_id         INTEGER REFERENCES transacoes.pedidos (id),
    acompanhamento_id INTEGER REFERENCES cadastros.acompanhamentos (id),
    quantidade        INTEGER
);

CREATE TABLE transacoes.compras (
    id          SERIAL PRIMARY KEY,
    descricao   VARCHAR,
    status      VARCHAR,
    data_compra DATE,
    usuario_id  INTEGER REFERENCES cadastros.usuarios (id)
);

CREATE TABLE transacoes.itens_compra (
    id                 SERIAL PRIMARY KEY,
    id_compras         INTEGER REFERENCES transacoes.compras (id),
    nome_produto       VARCHAR,
    descricao_produto  VARCHAR,
    quantidade_produto NUMERIC,
    status             VARCHAR DEFAULT 'PENDENTE'   -- PENDENTE, SOLICITADO, COMPRADO ou CANCELADO
);
