build:
	docker build -t desafio-paola:latest .

run: 
	docker run -it -p 8080:8080 desafio-paola