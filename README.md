# Transporte Sustentável — API REST (Spring Boot)

API do projeto **Transporte Sustentável** (equipe NepTune Labs), desenvolvida em Java 17 com Spring Boot 3
para a disciplina *Domain Driven Design Using Java* (4ª Sprint).

**URL da API publicada:** https://transporte-sustentavel-api.onrender.com

> A API roda no plano gratuito do Render: se ficar um tempo sem receber requisições, a primeira chamada
> pode levar cerca de um minuto para responder.

## Integrantes

| Nome | RM |
|---|---|
| Bruno Pires | 571202 |
| Gustavo Correia | 568745 |
| Gustavo Gonçalves | 572418 |
| Patrick | 573589 |
| Pedro Henrique | 573547 |

## Arquitetura

| Camada | Pacote | Responsabilidade |
|---|---|---|
| Model | `com.transporte.model` | Entidades (Usuario, Transporte, CartaoPontos, Passagem, Resgate) |
| DAO | `com.transporte.dao` | Acesso ao banco via JDBC (CRUD completo) |
| Conexão | `com.transporte.database` | `ConnectionFactory` (lê as credenciais de variáveis de ambiente) |
| BO | `com.transporte.bo` | Validações e regras de negócio |
| Resource | `com.transporte.resource` | Endpoints REST e `CorsFilter` |
| Exception | `com.transporte.exception` | Exceções de negócio e `GlobalExceptionHandler` |

## Infraestrutura

| Item | Serviço |
|---|---|
| API | Render (Web Service, build pelo `Dockerfile`) |
| Banco de dados | TiDB Cloud (compatível com MySQL) |
| Código-fonte | GitHub |

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
| `/` | `GET` (status da API) |
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

### Testando a API publicada

```bash
curl https://transporte-sustentavel-api.onrender.com/
curl https://transporte-sustentavel-api.onrender.com/transportes
```

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC completa do banco (opcional; tem prioridade sobre as demais) |
| `DB_USER`, `DB_PASSWORD` | Usuário e senha do banco |
| `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` | Alternativa a `DB_URL`/`DB_USER`/`DB_PASSWORD`; padrões: `localhost`, `3306`, `transporte_db`, `root`, senha vazia |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas separadas por vírgula (padrão `*`) |
| `PORT` | Porta HTTP (padrão `8080`; o Render define automaticamente) |

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

## Deploy (Render + TiDB Cloud)

### 1. Banco de dados (TiDB Cloud)

1. Crie um cluster no TiDB Cloud.
2. Em **Connect**, anote host, porta (padrão `4000`), usuário e senha.
3. Crie o banco de dados da aplicação (por exemplo `transporte_db`). As tabelas são criadas pela própria API na primeira inicialização.
4. Monte a URL JDBC. O TiDB Cloud exige conexão segura:

```
jdbc:mysql://<HOST>:4000/transporte_db?sslMode=VERIFY_IDENTITY
```

Se o cluster tiver lista de IPs permitidos, libere o acesso para que o Render consiga conectar.

### 2. API (Render)

1. Suba o projeto para um repositório no GitHub (o `Dockerfile` fica na raiz).
2. No Render: **New → Web Service**, conecte o repositório e escolha o ambiente **Docker**.
3. Em **Environment**, cadastre:

   | Chave | Valor |
   |---|---|
   | `DB_URL` | URL JDBC montada no passo anterior |
   | `DB_USER` | usuário do TiDB Cloud |
   | `DB_PASSWORD` | senha do TiDB Cloud |
   | `CORS_ALLOWED_ORIGINS` | *(opcional)* URL do front-end, por exemplo a da Vercel |

4. Clique em **Create Web Service**. Ao final do build, a API fica disponível em
   https://transporte-sustentavel-api.onrender.com
