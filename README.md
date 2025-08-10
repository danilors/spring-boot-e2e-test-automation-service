
🚀 ## Spring Boot E2E Test Automation Service
Este projeto é uma aplicação Spring Boot que expõe um endpoint REST para automatizar a execução de testes de ponta a ponta (E2E) em um ambiente isolado e consistente usando um contêiner Docker.

A funcionalidade principal utiliza a API do Docker para, de forma programática, iniciar um contêiner, clonar um repositório Git, e executar um conjunto de testes Maven dentro dele.

💻 Tecnologias Utilizadas
Java 17+: Linguagem de programação principal.

Spring Boot: Framework para a construção da aplicação.

Maven: Ferramenta de build e gerenciamento de dependências.

Docker Client (docker-java): Biblioteca para interagir com o daemon do Docker.

Git: Para clonar o repositório de testes.

📋 Pré-requisitos
Antes de executar a aplicação, certifique-se de que você tem os seguintes softwares instalados:

Java Development Kit (JDK) 17 ou superior

Maven

Docker (e o daemon do Docker deve estar em execução)

🛠 Como Executar o Projeto
Clone este repositório para sua máquina local:

git clone https://github.com/seu-usuario/este-repositorio.git
cd este-repositorio


Construa a aplicação usando Maven:

mvn clean install


Execute a aplicação Spring Boot:

java -jar target/your-app-name.jar


(Substitua your-app-name.jar pelo nome do arquivo JAR gerado)

🚀 Endpoint da API
A aplicação expõe um único endpoint REST para iniciar o processo de testes.

GET /tests/start
URL: http://localhost:8080/tests/start

Método: GET

Descrição: Este endpoint inicia o processo de testes. Ele cria um contêiner Docker, clona um repositório de testes, executa mvn clean install test e, em seguida, exibe os logs e remove o contêiner. A resposta HTTP será retornada após a conclusão do processo de testes.

📚 Exemplo de Uso
Para iniciar a execução dos testes, basta fazer uma requisição GET para o endpoint usando um navegador ou uma ferramenta como o curl:

curl http://localhost:8080/tests/start


📝 Contribuindo
Sinta-se à vontade para fazer fork do projeto e enviar pull requests. Para maiores informações, abra uma issue no GitHub.
