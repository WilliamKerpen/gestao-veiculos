powerShell
Limpar build anterior:
.\mvnw.cmd clean

compilar: 
.\mvnw.cmd compile

rodar:
.\mvnw.cmd spring-boot:run

disponivel em:
http://localhost:8080

http://localhost:8080/api/veiculos


## Configuração do banco de dados

O projeto utiliza MySQL e Spring Boot.

Antes de executar a aplicação, configure as seguintes
variáveis de ambiente:

- DB_URL
- DB_USERNAME
- DB_PASSWORD

Exemplo:

DB_URL=jdbc:mysql://localhost:3306/gestao_veiculos
DB_USERNAME=root
DB_PASSWORD=sua_senh