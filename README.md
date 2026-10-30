# Transporte Sustentável — API REST (Spring Boot)

API do projeto **Transporte Sustentável** (equipe NepTune Labs), desenvolvida em Java 17 com Spring Boot 3
para a disciplina *Domain Driven Design Using Java* (4ª Sprint).

**URL da API publicada:** `https://SEU-APP.up.railway.app` *(preencher após o deploy)*

## Arquitetura

| Camada | Pacote | Responsabilidade |
|---|---|---|
| Model | `com.transporte.model` | Entidades (Usuario, Transporte, CartaoPontos, Passagem, Resgate) |
| DAO | `com.transporte.dao` | Acesso ao banco via JDBC (CRUD completo) |
| Conexão | `com.transporte.database` | `ConnectionFactory` (lê as credenciais de variáveis de ambiente) |
| BO | `com.transporte.bo` | Validações e regras de negócio |
| Resource | `com.transporte.resource` | Endpoints REST e `CorsFilter` |
| Exception | `com.transporte.exception` | Exceções de negócio e `GlobalExceptionHandler` |

## Regras de negócio

- Todo usuário cadastrado recebe automaticamente um bilhete (`CP-<id>`) com saldo 0.
- CPF precisa ter 11 dígitos; e-mail precisa ser válido; datas no formato `DD/MM/AAAA`.
- Passagem em transporte com taxa de carbono < 0,05 kg CO2/km recebe **15% de desconto** quando o valor não é informado.
- **Resgate:** exige passagem `Disponível` e saldo suficiente; debita os pontos e marca a passagem como `Resgatada`.
- Cancelar (DELETE) um resgate devolve os pontos e libera a passagem.
- Não é possível excluir usuário/bilhete com saldo de pontos, nem alterar/excluir passagem já resgatada.

## Endpoints

| Recurso | Métodos |
|---|---|
| `/usuarios` | `GET`, `POST` · `/{id}`: `GET`, `PUT`, `DELETE` · `/{id}/cartao`: `GET` |
| `/transportes` | `GET`, `POST` · `/{id}`: `GET`, `PUT`, `DELETE` |
| `/cartoes` | `GET`, `POST` · `/{numero}`: `GET`, `PUT`, `DELETE` · `/{numero}/pontos`: `POST` |
| `/passagens` | `GET` (filtro `?status=Disponível`), `POST` · `/{id}`: `GET`, `PUT`, `DELETE` |
| `/resgates` | `GET`, `POST` · `/{id}`: `GET`, `PUT` (altera a data), `DELETE` (cancela) |

Status HTTP: `200` OK, `201` criado, `204` sem conteúdo, `400` dados inválidos, `404` não encontrado,
`409` conflito de regra, `500` erro interno. Erros retornam JSON `{timestamp, status, erro, mensagem}`.

### Exemplos de corpo

```json
POST /usuarios
{ "nome": "Ana Silva", "email": "ana@email.com", "cpf": "123.456.789-00" }

POST /transportes
{ "tipo": "Metrô", "nome": "Linha 1-Azul", "valorPontos": 100, "taxaCarbono": 0.03 }

POST /passagens
{ "origem": "Sé", "destino": "Tucuruvi", "dataViagem": "10/12/2026", "transporte": { "id": 1 } }

POST /cartoes/CP-1/pontos
{ "pontos": 200 }

POST /resgates
{ "numeroCartao": "CP-1", "passagemId": 1 }
```

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC completa (opcional) |
| `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` | Usadas se `DB_URL` não existir (o Railway já fornece essas) |
| `DB_USER`, `DB_PASSWORD` | Sobrescrevem usuário e senha |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas separadas por vírgula (padrão `*`) |
| `PORT` | Porta HTTP (padrão `8080`) |

As tabelas são criadas automaticamente na primeira execução. Para dados de exemplo, rode `dados-exemplo.sql`.

## Rodando localmente

Requisitos: JDK 17+, Maven 3.9+, MySQL 8.

```bash
# 1. crie o banco
mysql -u root -p -e "CREATE DATABASE transporte_db CHARACTER SET utf8mb4"

# 2. configure o acesso
export MYSQLUSER=root
export MYSQLPASSWORD=sua_senha

# 3. execute
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

## Deploy (Railway)

1. Suba o projeto para um repositório no GitHub.
2. No Railway: **New Project → Deploy from GitHub repo** (ele detecta o `Dockerfile`).
3. No mesmo projeto: **New → Database → MySQL**.
4. No serviço da API, em *Variables*, referencie as variáveis do MySQL (`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`).
5. Em *Settings → Networking*, gere o domínio público e cole a URL no topo deste README.
6. (Opcional) Defina `CORS_ALLOWED_ORIGINS` com a URL da Vercel do front-end.
