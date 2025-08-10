
# 🚀 Spring Boot E2E Test Automation Service

This project is a Spring Boot application that exposes a REST endpoint to automate the execution of end-to-end (E2E) tests in an isolated and consistent environment using a Docker container.

The core functionality uses the Docker API to programmatically start a container, clone a Git repository, and run a set of Maven tests within it.

### 💻 Technologies Used

* **Java 21+**: Main programming language.

* **Spring Boot**: Framework para a construção da aplicação.

* **Maven**: Library for interacting with the Docker daemon.

* **Docker Client (docker-java)**: For cloning the test repository.

* **Git**: Para clonar o repositório de testes.

### 📋 Pré-requisitos

Before running the application, make sure you have the following software installed:

* **Java Development Kit (JDK) 17 ou superior**

* **Maven**

* **Docker** (and the Docker daemon must be running)

### 🛠 Como Executar o Projeto

1.  Clone este repositório para sua máquina local:

    ```
    git clone https://github.com/seu-usuario/este-repositorio.git
    cd este-repositorio
    ```

2.  Build the application using Maven:

    ```
    mvn clean install
    ```

3.  Run the Spring Boot application:

    ```
    java -jar target/your-app-name.jar
    ```

    (Replace your-app-name.jar with the name of the generated JAR file)

### 🚀 Endpoint da API

The application exposes a single REST endpoint to initiate the test process.

#### `GET /tests/start`

* **URL**: `http://localhost:8080/tests/start`

* **Method**: `GET`

* **Description**: This endpoint starts the test process. It creates a Docker container, clones a test repository, runs  `mvn clean install test` e, and then displays the logs and removes the container. The HTTP response will be returned upon completion of the test process.

### 📚 Exemplo de Uso

To initiate the test execution, simply make a GET request to the endpoint using a browser or a tool like `curl`:

```
curl http://localhost:8080/tests/start
```

### 📝 Contribuindo

Feel free to `fork` the project and submit `pull requests`. For more information, please open an `issue` on GitHub.