
CREATE DATABASE IF NOT EXISTS transporte_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE transporte_db;


CREATE TABLE IF NOT EXISTS usuarios (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    cpf           VARCHAR(14)  NOT NULL UNIQUE,   -- formato XXX.XXX.XXX-XX
    data_cadastro VARCHAR(10)  NOT NULL            -- formato DD/MM/AAAA
);


CREATE TABLE IF NOT EXISTS transportes (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    tipo          VARCHAR(50)  NOT NULL,           -- ex: "Ônibus", "Metrô"
    nome          VARCHAR(100) NOT NULL,           -- nome descritivo
    valor_pontos  INT          NOT NULL,           -- custo base em pontos
    taxa_carbono  DOUBLE       NOT NULL            -- kg de CO2 por km
);


CREATE TABLE IF NOT EXISTS cartoes_pontos (
    numero_cartao VARCHAR(20)  PRIMARY KEY,        -- ex: CP-2026-0001
    saldo_pontos  INT          NOT NULL DEFAULT 0,
    data_validade VARCHAR(10)  NOT NULL,
    usuario_id    INT          NOT NULL,
    -- Se o usuário for deletado, o cartão também é removido
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS passagens (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    origem          VARCHAR(100) NOT NULL,
    destino         VARCHAR(100) NOT NULL,
    data_viagem     VARCHAR(10)  NOT NULL,
    transporte_id   INT,                           -- FK para transportes
    valor_em_pontos INT          NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'Disponível',
    FOREIGN KEY (transporte_id) REFERENCES transportes(id)
);


CREATE TABLE IF NOT EXISTS resgates (
    id                INT         AUTO_INCREMENT PRIMARY KEY,
    numero_cartao     VARCHAR(20) NOT NULL,        -- FK para cartoes_pontos
    passagem_id       INT         NOT NULL,        -- FK para passagens
    data_resgate      VARCHAR(10) NOT NULL,
    pontos_utilizados INT         NOT NULL,
    FOREIGN KEY (numero_cartao) REFERENCES cartoes_pontos(numero_cartao),
    FOREIGN KEY (passagem_id)   REFERENCES passagens(id)
);
