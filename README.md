# desafio-inmetro

# Rotas <h3>

* # /certificates <h4>

    POST: cria um novo certificado e o insere no banco de dados

    GET: retona todos os certificados presentes no banco de dados

    DELETE: apaga todos os certificados do banco de dados

* # /certificates/{serialNumber} <h4>

    GET: mostra o certificado com o número serial inserido na URL

    DELETE: apaga o certificado com o número serial inserido do banco de dados

* # /certificates/name <h4>

    Query string: name

    GET: mostra todos os certificados do titular com nome = name

    DELETE: apaga todos os certificados do titular com nome = name

* # /certificates/name/interval <h4>

    Query strings: name, startDate, endDate

    GET: retorna retorna todos os certificados válidos em algum momento do intervalo de tempo inserido cujo nome do titular = name

* # /certificates/valid <h4>

    Query string opcional: date
    date precisa estar no formato "DD/MM/YYYYTHH:MM:SS"

    GET: se date foi inserido, mostra todos os certificados válidos na data inserida se date não foi inserido, mostra todos os certificados válidos no momento do request

* # /certificates/valid/interval <h4>

    Query strings: startDate, endDate
    startDate e endDate precisam estar no formato "DD/MM/YYYYTHH:MM:SS"

    GET: retorna todos os certificados válidos em algum momento do intervalo de tempo inserido

* # /certificates/expired <h4>

    GET: mostra todos os certificados expirados no momento do request

    DELETE: apaga todos os certificados expirados no momento do request
