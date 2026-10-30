CREATE TABLE IF NOT EXISTS usuarios (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    cpf           VARCHAR(14)  NOT NULL UNIQUE,
    data_cadastro VARCHAR(10)  NOT NULL
);

CREATE TABLE IF NOT EXISTS transportes (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    tipo          VARCHAR(50)  NOT NULL,
    nome          VARCHAR(100) NOT NULL,
    valor_pontos  INT          NOT NULL,
    taxa_carbono  DOUBLE       NOT NULL
);

CREATE TABLE IF NOT EXISTS cartoes_pontos (
    numero_cartao VARCHAR(20)  PRIMARY KEY,
    saldo_pontos  INT          NOT NULL DEFAULT 0,
    data_validade VARCHAR(10)  NOT NULL,
    usuario_id    INT          NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS passagens (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    origem          VARCHAR(100) NOT NULL,
    destino         VARCHAR(100) NOT NULL,
    data_viagem     VARCHAR(10)  NOT NULL,
    transporte_id   INT,
    valor_em_pontos INT          NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'Disponível',
    FOREIGN KEY (transporte_id) REFERENCES transportes(id)
);

CREATE TABLE IF NOT EXISTS resgates (
    id                INT         AUTO_INCREMENT PRIMARY KEY,
    numero_cartao     VARCHAR(20) NOT NULL,
    passagem_id       INT         NOT NULL,
    data_resgate      VARCHAR(10) NOT NULL,
    pontos_utilizados INT         NOT NULL,
    FOREIGN KEY (numero_cartao) REFERENCES cartoes_pontos(numero_cartao),
    FOREIGN KEY (passagem_id)   REFERENCES passagens(id)
)
