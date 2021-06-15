# desafio-inmetro

API desenvolvida em Java com Spring Boot e SQLite3 para o desafio de desenvolvimento back-end. Possui rotas para criar e acessar certificados X.509.

- Parâmetros com "*" são obrigatórios
- Datas precisam estar no formato YYYY-MM-DDTHH:MM:SS (exemplo: 2025-02-28T12:30:00)

## Pré-requisitos

- Docker

## Instruções

- Para compilar o projeto:
  - ```make build```
  
- Para executar o projeto:
  - ```make run```

## Rotas:

### /certificates

- POST: cria um novo certificado e o insere no banco de dados
  - Params: ```name*```, ```expirationDate*```
- GET: retona todos os certificados presentes no banco de dados
- DELETE: apaga todos os certificados do banco de dados

### /certificates/__\<serialNumber>__

- GET: mostra o certificado com o número serial inserido na URL
- DELETE: apaga o certificado com o número serial inserido do banco de dados

### /certificates/name

- GET: mostra todos os certificados do titular com nome = ```name```
  - Params: ```name*```
- DELETE: apaga todos os certificados do titular com nome = ```name```
  - Params: ```name*```

### /certificates/name/interval

- GET: retorna todos os certificados válidos em algum momento do intervalo de tempo inserido cujo nome do titular = ```name```
  - Params: ```name*```, ```startDate*```, ```endDate*```

### /certificates/valid

- GET: se ```date``` não foi inserido, mostra todos os certificados válidos no momento do request, caso contrário, mostra todos os certificados válidos na data inserida
  - Params: ```date```

### /certificates/valid/interval

- GET: retorna todos os certificados válidos em algum momento do intervalo de tempo inserido
  - Params: ```startDate*```, ```endDate*```

### /certificates/expired

- GET: mostra todos os certificados expirados no momento do request
- DELETE: apaga todos os certificados expirados no momento do request
