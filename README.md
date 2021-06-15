# desafio-inmetro

API desenvolvida em Java com Spring Boot para o desafio de desenvolvimento back-end

Parâmetros com "*" são obrigatórios

## Rotas:

### /certificates

- POST: cria um novo certificado e o insere no banco de dados
  - Params: ```name*```, ```expirationDate*```
    - ```expirationDate``` precisa estar no formato "DD/MM/YYYYTHH:MM:SS"
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
    - ```startDate``` e ```endDate``` precisam estar no formato "DD/MM/YYYYTHH:MM:SS" 

### /certificates/valid

- GET: se ```date``` não foi inserido, mostra todos os certificados válidos no momento do request, caso contrário, mostra todos os certificados válidos na data inserida
  - Params: ```date```
    - ```date``` precisa estar no formato "DD/MM/YYYYTHH:MM:SS"

### /certificates/valid/interval

- GET: retorna todos os certificados válidos em algum momento do intervalo de tempo inserido
  - Params: ```startDate*```, ```endDate*```
    - ```startDate``` e ```endDate``` precisam estar no formato "DD/MM/YYYYTHH:MM:SS" 

### /certificates/expired

- GET: mostra todos os certificados expirados no momento do request
- DELETE: apaga todos os certificados expirados no momento do request
