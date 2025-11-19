# RateioPro API: Gerenciador de Despesas de Grupo

O RateioPro é uma API de gerenciamento financeiro de grupos, inspirada em ferramentas como Splitwise. Seu objetivo principal é simplificar o acerto de contas ao final de viagens, eventos ou para dividir despesas recorrentes (como contas de república).

A aplicação foi desenvolvida seguindo os princípios de Design Orientado a Domínio (DDD) e arquitetura em camadas, com foco em segurança, testes robustos, lógica de negócio precisa (`BigDecimal`) e práticas modernas de DevOps.

## ⚙️ Tecnologias Utilizadas

| Categoria | Tecnologia | Justificativa |
| :--- | :--- | :--- |
| **Back-end** | Java 21, Spring Boot 3 | Framework robusto para desenvolvimento de APIs REST. |
| **Segurança** | Spring Security, JWT | Autenticação stateless e segura. |
| **Banco de Dados** | MySQL | Banco de dados relacional para garantir integridade e transações. |
| **ORM** | Spring Data JPA / Hibernate | Mapeamento Objeto-Relacional e abstração do acesso ao banco. |
| **Migrations** | Flyway | Gestão e versionamento confiável do esquema do banco de dados. |
| **Testes** | JUnit 5, Mockito, AssertJ | Testes de Unidade e Integração automatizados. |
| **DevOps & Infra** | Docker & Docker Compose | Containerização da aplicação e orquestração de ambientes. |
| **CI/CD** | GitHub Actions | Pipeline de Integração Contínua para build e testes automáticos. |

---

##  Funcionalidades Principais

Este projeto demonstra domínio sobre os seguintes desafios técnicos e regras de negócio:

### 1. Sistema de Autenticação e Autorização
* **Autenticação Híbrida:** Suporte para **Login/Senha** tradicional e **OAuth 2.0** (via GitHub e Google) para acesso rápido.
* **JWT (JSON Web Tokens):** Geração, validação e filtro de segurança de tokens para proteger os endpoints da API.

### 2. Lógica de Negócio (O Coração do App)
* **Gerenciamento de Grupo:** Criação de grupos com um sistema de convite baseado em `código/token` exclusivo.
* **Perfis Contextuais:** Gerenciamento de papéis dinâmicos (`DONO` vs. `MEMBRO`). O perfil de um usuário é contextual ao grupo, não global.
* **Cálculo de Balanço Preciso:** Implementação da lógica de **rateio** principal, utilizando a classe `java.math.BigDecimal` para garantir precisão monetária, evitando erros de ponto flutuante.
* **Endpoint:** `GET /grupos/{id}/balanco` calcula a situação financeira do grupo: (Total Pago por Usuário) - (Valor Devido por Usuário) = **Balanço**.

### 3. Persistência e Qualidade de Código
* **Relacionamentos Complexos:** Implementação correta de relacionamentos **N:M (Many-to-Many)**, como a tabela `UsuarioGrupo` que armazena dados contextuais (`PerfilGrupo`).
* **Exclusão Lógica:** Utilização de campos booleanos (`ativo`) nas entidades para preservar o histórico financeiro mesmo após exclusões.
* **Tratamento Global de Erros:** Utilização de `@RestControllerAdvice` para formatar respostas de erro.

---

##  Como Rodar o Projeto

Você pode rodar a aplicação de duas formas: utilizando a infraestrutura completa via Docker (recomendado) ou configurando o ambiente manualmente.

### Opção 1: Via Docker Compose (Recomendado)

Esta opção sobe tanto a API quanto o Banco de Dados MySQL automaticamente, sem necessidade de instalações locais além do Docker.

**Pré-requisitos:**
* Docker e Docker Compose instalados.

**Passos:**
1.  Clone o repositório:
    ```bash
    git clone [https://github.com/SEU-USUARIO/projeto-rateiopro.git](https://github.com/SEU-USUARIO/projeto-rateiopro.git)
    cd projeto-rateiopro
    ```
2.  Configure as variáveis de ambiente (opcional, ou ajuste no `docker-compose.yml`):
    * Certifique-se de inserir suas credenciais de OAuth/JWT no arquivo `docker-compose.yml` ou em um arquivo `.env` se configurado.
3.  Execute o comando de orquestração:
    ```bash
    docker-compose up --build
    ```
    *A aplicação estará disponível em `http://localhost:8080` e o banco de dados estará acessível internamente no container.*

### Opção 2: Execução Manual (Local)

Caso prefira rodar a aplicação diretamente na sua máquina.

**Pré-requisitos:**
* Java 21+
* MySQL Server rodando localmente.

**Passos:**
1.  Crie o banco de dados no seu MySQL local:
    ```sql
    CREATE DATABASE rateiopro_api;
    ```
2.  Configure o arquivo `src/main/resources/application.properties` com suas credenciais:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/rateiopro_api
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    
    # Credenciais OAuth (GitHub/Google) e JWT
    # ... (preencher conforme necessário)
    ```
3.  Inicie a aplicação via Maven Wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```

---

##  Documentação

A API é documentada utilizando Swagger/OpenAPI. Com a aplicação rodando, acesse:
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

##  CI/CD

O projeto conta com um pipeline de Integração Contínua (CI) configurado via **GitHub Actions**.
* A cada `push` ou `pull_request` para uma branch , o pipeline executa automaticamente:
    1.  Setup do ambiente Java.
    2.  Build da aplicação.
    3.  Execução de toda a suíte de testes automatizados (Unitários e Integração).
  4. Assim que passar,o Pull request para a main será liberado.

---

##  Testes

Para rodar a suíte de testes manualmente:

```bash
./mvnw test