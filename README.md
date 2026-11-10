## Gestao de Veiculo

Projeto criado como parte de portifolio para obtencao de nota na materia de POO pela Faculdade UNIFecaf, pelo aluno William Kerpen

## Tecnologias utilizadas

- **Java 21** — linguagem e plataforma da aplicação.
- **Spring Boot 4.1.2** — inicialização e configuração do backend.
- **Spring Web MVC** — API REST e disponibilização dos arquivos do frontend.
- **Spring Data JPA e Hibernate** — persistência e mapeamento objeto-relacional.
- **Jakarta Bean Validation** — validação dos dados recebidos pela API.
- **MySQL** — banco de dados relacional.
- **Maven Wrapper** — compilação e execução sem exigir uma instalação global do Maven.
- **HTML5, CSS3 e JavaScript** — interface web responsiva servida pelo Spring Boot.
- **JUnit 5 e Mockito** — testes automatizados.

## Configuração do banco de dados

O projeto utiliza MySQL e Spring Boot.

Antes de executar a aplicação, crie um Banco de dados no MySql: CREATE DATABASE nome_do_banco;

configure as seguintes variáveis de ambiente:

- DB_URL
- DB_USERNAME
- DB_PASSWORD

Exemplo:

DB_URL=jdbc:mysql://localhost:3306/gestao_veiculos
DB_USERNAME=root
DB_PASSWORD=sua_senha

ou crie um arquivo:  java/br/com/sistema_automotivo/gestao_veiculos/application.properties

spring.application.name=gestao-veiculos

spring.datasource.url=jdbc:mysql://localhost:3306/nome_do_banco
spring.datasource.username=seu_user_name
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080

## Comandos uteis para o powerShell

Limpar build anterior:
.\mvnw.cmd clean

compilar: 
.\mvnw.cmd compile

rodar:
.\mvnw.cmd spring-boot:run

API disponivel em:
http://localhost:8080

http://localhost:8080/api/veiculos

Abra a interface pelo endereço `http://localhost:8080` depois de iniciar o Spring
Boot. Não abra `index.html` diretamente com `file://`. Se usar um servidor de
desenvolvimento local em outra porta, o frontend apontará para a API na porta 8080;
a API permite CORS apenas para origens locais `localhost` e `127.0.0.1`.


## Interface web

Com a aplicação em execução, acesse http://localhost:8080 para usar a interface de
gerenciamento de veículos e marcas.

Os links das fotos são armazenados em um único campo `fotos` (String/TEXT) na tabela
`veiculos`, separados por quebra de linha. Nenhum arquivo de imagem é enviado ou
armazenado pelo sistema. Cada veículo aceita até quatro links HTTP/HTTPS.
No formulário de cadastro e edição, informe os links no campo **Links das fotos**,
um endereço por linha; também é possível gerenciá-los pela galeria do veículo.

Veículos incluem cor, modelo em texto livre e ficam armazenados após a venda para
preservar o histórico.

O campo `cor` faz parte do cadastro. O campo `fotos` pode ser enviado junto aos
demais dados em `POST /api/veiculos` ou `PUT /api/veiculos/{id}`, como uma string
com um link por linha:

```json
{
  "marcaId": 2,
  "modelo": "Corolla XEi",
  "cor": "Prata",
  "ano": 2022,
  "placa": "ABC1D23",
  "chassi": "9BWZZZ377VT004251",
  "quilometragem": 35000,
  "combustivel": "Flex",
  "cambio": "Automático",
  "precoCompra": 95000.00,
  "precoVenda": 112000.00,
  "fotos": "https://exemplo.com/carro-frente.jpg\nhttps://exemplo.com/carro-lado.jpg"
}
```

### Consultar e filtrar veículos

`GET /api/veiculos` aceita filtros opcionais e combináveis:

- `modelo`: busca parcial e sem diferenciar maiúsculas/minúsculas.
- `marcaId`: ID da marca cadastrada.
- `precoMin` e `precoMax`: faixa do preço de venda.
- `anoMin` e `anoMax`: faixa de ano do veículo.
- `status`: `DISPONIVEL`, `RESERVADO` ou `VENDIDO`.

Exemplo: `/api/veiculos?modelo=civic&marcaId=2&precoMin=50000&precoMax=100000&anoMin=2018&anoMax=2024&status=DISPONIVEL`.