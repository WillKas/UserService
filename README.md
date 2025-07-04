# User Service

**User Service** é uma aplicação Spring Boot que oferece gerenciamento completo de usuários (CRUD), autenticação via JWT e notificações por e-mail. Ideal para microsserviços que exigem registro e autenticação de usuários com segurança e documentação automática.

---

## Funcionalidades Principais

* **Cadastro de Usuário**: POST `/user/api/v1/save`
* **Atualização de Usuário**: PUT `/user/api/v1/update`
* **Exclusão de Usuário**: DELETE `/user/api/v1/delete?id={id}`
* **Consulta por ID**: GET `/user/api/v1/findById?id={id}`
* **Listagem Paginada e Filtrada**: GET `/user/api/v1/findAll?username={username}&email={email}&enabled={true|false}&pageNumber={n}&pageSize={m}`
* **Autenticação (Login)**: POST `/auth/login` → retorna JWT
* **Segurança Stateless** via JWT em todas as rotas (exceto login e cadastro)
* **Notificações por E‑mail** em operações de criação e atualização
* **Validações** de payload com mensagens claras de erro
* **Tratamento de Exceções** padronizado (códigos HTTP e `GenericErrorModelResponse`)
* **Documentação** automática via OpenAPI/Swagger UI
* **Perfis**: `dev`, `hom`, `prod` (cada um possui seu `application-<perfil>.yml`)

---

## Tecnologias e Versões

| Tecnologia                          | Versão      |
| ----------------------------------- | ----------- |
| Java                                | 21          |
| Spring Boot                         | 3.3.13      |
| Spring Data JPA (H2 runtime)        | -           |
| Spring Security                     | -           |
| JJWT (JSON Web Token)               | 0.11.5      |
| MapStruct                           | 1.5.5.Final |
| Lombok                              | 1.18.32     |
| springdoc-openapi-starter-webmvc-ui | 2.6.0       |
| Maven                               | 4.0.0 POM   |
| JUnit + Mockito                     | -           |
| Docker & Docker Compose             | -           |

---

## Pré‑requisitos

* Java 21 SDK instalado
* Maven instalado (3.8+)
* Docker & Docker Compose (opcional)
* Git

---

## Instalação e Execução Local

1. **Clonar repositório**

   ```bash
   git clone https://github.com/SEU-USUARIO/UserService.git
   cd UserService
   ```

2. **Build & Testes**

   ```bash
   mvn clean package
   mvn test
   ```

3. **Executar com Maven**

   * Profile **dev** (H2 em memória)

     ```bash
     mvn spring-boot:run -Dspring-boot.run.profiles=dev
     ```

4. **Executar o JAR gerado**

   ```bash
   java -jar target/UserService-0.1.jar --spring.profiles.active=dev
   ```

5. **Acessar a aplicação**

   * Swagger UI:  `http://localhost:10041/swagger-ui.html`
   * H2 Console:  `http://localhost:10041/h2-console`  (user/pass conforme `application-dev.yml`)

---

## Perfis de Configuração

* **dev**: `src/main/resources/application-dev.yml` (H2, porta 10041)
* **hom**: `application-hom.yml`
* **prod**: `application-prod.yml`

Cada arquivo define:

* Porta (`server.port: 10041`)
* Datasource H2 em memória
* Credenciais de segurança e JWT
* Configuração de e‑mail (SMTP)
* Paths do Swagger (`/v1/api-docs`, `/swagger-ui.html`)

Para rodar em outro perfil, altere o argumento `--spring.profiles.active=<perfil>`.

---

## Execução via Docker

### Dockerfile (multi-stage)

```dockerfile
# Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG PROFILE=dev
ENV SPRING_PROFILES_ACTIVE=${PROFILE}
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10041
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

#### Build & Run

```bash
# Build para dev (padrão)
docker build -t user-service:dev .
# Build para hom
docker build --build-arg PROFILE=hom -t user-service:hom .
# Executar
docker run -p 10041:10041 user-service:dev
```

### Docker Compose

```yaml
version: "3.8"
services:
   user-service:
      build: .
      image: user-service:${SERVICE_PROFILE:-dev}
      ports:
         - "10041:10041"
      environment:
         SPRING_PROFILES_ACTIVE: ${SERVICE_PROFILE:-dev}
      restart: unless-stopped
```

```bash
# Rodar em dev (default)
SERVICE_PROFILE=dev docker-compose up --build
# Rodar em hom
SERVICE_PROFILE=hom docker-compose up --build
```

---

## Executando os Testes

```bash
mvn test
```

> Os testes de unidade e controller (MockMvc) garantem cobertura de fluxos
> de criação, atualização, autenticação e erros.

---

## Documentação da API

Ao iniciar a aplicação, abra:

```
http://localhost:10041/swagger-ui.html
```

Lá você verá todos os endpoints, modelos de request/response e poderá testar chamadas.

---

## Motivação e Cobertura de Testes

Foram criados testes em três camadas distintas para assegurar robustez e confiabilidade:

* **AuthControllerTest** (Camada de Controller)

   * **Cenário de sucesso**: verifica que `/auth/login` retorna 200 e o token correto quando credenciais válidas são enviadas.
   * **Cenários de falha**:

      * Usuário não existe → 401 Unauthorized.
      * Senha incorreta → 401 Unauthorized.
      * Payload mal formado → 400 Bad Request.
   * **Motivação**: garantir que o controlador trate adequadamente fluxos de autenticação e retorne códigos HTTP e mensagens consistentes.

* **UserControllerTest** (Camada de Controller)

   * **Criação de usuário**:

      * Sucesso → 200 OK e JSON com dados do usuário.
      * Serviço retorna `null` → 400 Bad Request.
   * **Atualização de usuário**:

      * Sucesso → 200 OK e dados atualizados.
      * Serviço retorna `null` → 400 Bad Request.
   * **Exclusão de usuário**:

      * Sucesso → 200 OK.
      * ID inválido (<=0) → 400 Bad Request.
      * Usuário não encontrado → 404 Not Found.
   * **Listagem paginada**:

      * Sucesso → 200 OK e payload de página.
      * Nenhum usuário → 204 No Content.
   * **Busca por ID**:

      * Sucesso → 200 OK.
      * ID inválido → 400 Bad Request.
      * Usuário não encontrado → 404 Not Found.
   * **Motivação**: validar todos os endpoints REST, assegurando contratos de entrada/saída e tratamento de erros.

* **UserServiceTest** (Camada de Serviço)

   * **Validações**:

      * E-mail já existente → lança `EmailAlreadyExistsException`.
      * Senha fraca → lança `InvalidPasswordException`.
      * Username em branco → lança `UsernameNotProvidedException`.
      * E-mail inválido → lança `InvalidEmailException`.
   * **Fluxo de criação**: configura mocks do repositório, codificador de senha e mapeador, e verifica chamada ao `emailService`.
   * **Fluxo de atualização**: simula existência ou não do usuário e garante comportamento correto.
   * **Busca e listagem**:

      * `findById` lança `UserNotFoundException` ou retorna o DTO.
      * `findAllUsers` mapeia corretamente cenários com itens e sem itens.
   * **Remoção**:

      * Usuário inexistente → lança `EntityNotFoundException`.
      * Usuário existente → remove sem exceções.
   * **Motivação**: testar a lógica de negócio isoladamente, cobrindo todos os ramos de decisão e interações com o repositório.

Essa cobertura em três níveis (controller, serviço e integração via MockMvc) assegura que:

1. **Contratos de API** estão corretos e confiáveis.
2. **Regras de negócio** são aplicadas e validadas.
3. **Erros** são tratados de forma consistente.

Com essa abordagem, ganhamos confiança para evoluir a aplicação sem quebrar funcionalidades existentes.

---
## Possibilidades para frontend

Para a camada de apresentação (frontend), algumas tecnologias populares são:

### JSP (JavaServer Pages)

**Prós**:

* Totalmente integrado ao ciclo de request/response do Spring MVC.
* Fácil configuração em servidores de aplicação Java (Tomcat, WildFly).
* Adequado para aplicações simples ou legadas.
  **Contras**:
* Mistura código Java e HTML, tornando manutenção e testes mais difíceis.
* Escalabilidade limitada em SPAs mais complexas.

### JSF (JavaServer Faces)

**Prós**:

* Abordagem baseada em componentes reutilizáveis.
* Integração nativa com Contexts and Dependency Injection (CDI).
* RichFaces, PrimeFaces e outros frameworks complementares.
  **Contras**:
* Curva de aprendizado acentuada.
* Estado do componente mantido no servidor, gerando overhead e complexidade de cluster.
* Resposta mais lenta para interações AJAX em grandes árvores de componentes.

### Angular

**Prós**:

* Framework completo para SPAs, com CLI, roteamento e internacionalização.
* Arquitetura unidirecional baseada em componentes e injeção de dependência.
* Ferramentas de teste integradas (Karma, Jasmine).
  **Contras**:
* Tamanho do bundle inicial relativamente grande.
* Curva de aprendizado elevada devido a TypeScript e conceitos avançados.
* Atualizações de versão podem exigir refatorações significativas.

### Vue.js

**Prós**:

* Framework progressivo: pode ser adotado gradualmente em partes da aplicação.
* Sintaxe simples e reatividade direta nos templates.
* Bundle mais leve comparado ao Angular.
  **Contras**:
* Ecossistema menor do que Angular em termos de ferramentas oficiais.
* Pouca padronização entre bibliotecas de terceiros, exigindo escolhas de arquitetura.

> A escolha ideal depende dos requisitos de projeto, equipe e infraestrutura disponíveis.