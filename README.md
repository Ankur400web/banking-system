# 🏦 Banking System

A production-style banking management system backend built with **Java
21, Spring Boot, MySQL, Spring Security, JWT, Flyway, Docker, and GitHub
Actions**.

This project focuses on production-oriented backend engineering:
authentication, authorization, transaction processing, concurrency
protection, validation, exception handling, automated testing, database
migrations, containerization, and CI/CD.

## 🚀 Features

### User Management

-   Create, retrieve, update, and delete users
-   Change password
-   Duplicate email protection
-   Request validation

### Authentication & Security

-   JWT-based authentication
-   Stateless Spring Security
-   BCrypt password hashing
-   Protected REST endpoints
-   Account-level authorization
-   Custom JWT authentication filter
-   Invalid credential and unauthorized-access handling

### Account Management

-   Create bank accounts
-   Retrieve accounts by ID or account number
-   Delete accounts
-   Associate accounts with users
-   Balance management

### Transactions

-   Deposits
-   Withdrawals
-   Transfers
-   Transaction history
-   Balance-after-transaction tracking
-   Insufficient-balance protection
-   Same-account transfer protection
-   Concurrency protection against double spending

### Backend Engineering

-   DTO-based API design
-   Bean validation
-   Global exception handling
-   JPA/Hibernate
-   Flyway migrations
-   Structured logging
-   Spring Boot Actuator
-   OpenAPI / Swagger
-   Automated tests

### DevOps

-   Docker
-   Docker Compose
-   Environment-based configuration
-   GitHub Actions CI
-   Automated Docker image builds
-   GitHub Container Registry publishing

## 🛠️ Tech Stack

  Category            Technology
  ------------------- ---------------------------------------------
  Language            Java 21
  Framework           Spring Boot 4.1.1
  Web                 Spring MVC / REST
  Security            Spring Security + JWT
  ORM                 Spring Data JPA / Hibernate
  Database            MySQL 8.4
  Migrations          Flyway
  Validation          Jakarta Bean Validation
  API Documentation   Springdoc OpenAPI / Swagger
  Monitoring          Spring Boot Actuator
  Build Tool          Maven
  Testing             JUnit 5, Mockito, Spring Boot Test, MockMvc
  Containerization    Docker
  Orchestration       Docker Compose
  CI/CD               GitHub Actions
  Registry            GitHub Container Registry

## 🏗️ Architecture

``` text
Client
  │
  ▼
REST Controllers
  │
  ▼
DTOs / Validation
  │
  ▼
Services
  │
  ├── Authentication
  ├── User Management
  ├── Account Management
  └── Transactions
  │
  ▼
Repositories
  │
  ▼
MySQL
```

### Security Flow

``` text
Client
  │
  │ Authorization: Bearer <JWT>
  ▼
JWTAuthenticationFilter
  │
  ▼
JwtService
  │
  ▼
Spring Security Context
  │
  ▼
Protected Controller
```

## 📁 Project Structure

``` text
src/main/java/com/BankingSystem/Banking_System
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
└── service
```

## 🔐 Authentication

Login:

``` http
POST /api/auth/login
Content-Type: application/json
```

Example:

``` json
{
  "email": "user@example.com",
  "password": "your-password"
}
```

Protected endpoints use:

``` http
Authorization: Bearer <JWT>
```

The application uses stateless authentication.

## 🔌 API Endpoints

### Authentication

  Method   Endpoint            Description
  -------- ------------------- -----------------------------------
  POST     `/api/auth/login`   Authenticate user and receive JWT

### Users

  Method   Endpoint                Description
  -------- ----------------------- -----------------
  POST     `/api/users`            Create user
  GET      `/api/users`            Get all users
  GET      `/api/users/{id}`       Get user by ID
  PUT      `/api/users/{id}`       Update user
  DELETE   `/api/users/{id}`       Delete user
  PUT      `/api/users/password`   Change password

### Accounts

  ---------------------------------------------------------------------------------
  Method                  Endpoint                          Description
  ----------------------- --------------------------------- -----------------------
  POST                    `/api/accounts`                   Create account

  GET                     `/api/accounts/id/{id}`           Get account by ID

  GET                     `/api/accounts/{accountNumber}`   Get account by account
                                                            number

  DELETE                  `/api/accounts/{accountNumber}`   Delete account
  ---------------------------------------------------------------------------------

### Transactions

  Method   Endpoint                             Description
  -------- ------------------------------------ -------------------------
  POST     `/api/transaction/deposit`           Deposit money
  POST     `/api/transaction/withdraw`          Withdraw money
  POST     `/api/transaction/transfer`          Transfer money
  GET      `/api/transaction/{accountNumber}`   Get transaction history

## 🗄️ Database

MySQL 8.4 is used with Flyway for version-controlled schema migrations.

``` text
V1 → users
V2 → accounts
V3 → transactions
```

Relationships:

``` text
User
 │
 └── 1 ──────── * Account
                    │
                    └── 1 ──────── * Transaction
```

Hibernate is configured to validate the database schema rather than
automatically modifying it.

## 🧪 Testing

The automated test suite covers:

-   Service-layer business logic
-   Authentication
-   Controllers
-   Repositories
-   JWT service
-   Spring Security configuration
-   JWT authentication filter
-   Validation
-   Exception handling
-   Transaction behavior

Run tests:

``` bash
./mvnw test
```

or:

``` bash
mvn test
```

## 🐳 Run with Docker Compose

Create a `.env` file in the project root:

``` env
DB_PASSWORD=your_database_password
MYSQL_ROOT_PASSWORD=your_root_password
JWT_SECRET=your_jwt_secret
```

Never commit real credentials.

Start the services:

``` bash
docker compose up -d
```

Check them:

``` bash
docker compose ps
```

Check API health:

``` bash
curl http://localhost:8080/actuator/health
```

Stop the services:

``` bash
docker compose down
```

## 📦 Docker Image

The application is automatically published to GitHub Container Registry.

``` bash
docker pull ghcr.io/ankur400web/banking-system:main
```

The Docker image is published only after the CI job succeeds.

## ⚙️ CI/CD

For pushes to `main` and pull requests targeting `main`, GitHub Actions
runs:

``` text
Checkout
   ↓
Java 21 setup
   ↓
MySQL service
   ↓
Run automated tests
   ↓
Build application
   ↓
Build Docker image
```

For pushes to `main`, the publishing job continues:

``` text
CI succeeds
   ↓
Build application JAR
   ↓
Build Docker image
   ↓
Authenticate with GHCR
   ↓
Push image to GHCR
```

This prevents a failing build or test suite from publishing a new
container image.

## 📊 Monitoring

Actuator endpoints:

``` text
GET /actuator/health
GET /actuator/info
```

The application also uses structured logging with different levels for
normal operations, security/business failures, and diagnostics.

Sensitive information such as passwords, JWTs, and authentication
headers is not logged.

## 📚 API Documentation

OpenAPI / Swagger documentation is included through Springdoc.

When the application is running, open the configured Swagger UI endpoint
to explore and test the REST API.

## 🔒 Security Considerations

The project includes:

-   BCrypt password hashing
-   JWT authentication
-   Stateless security
-   Protected API endpoints
-   Account ownership checks
-   Input validation
-   Global exception handling
-   Environment-based database credentials
-   Environment-based JWT configuration
-   Gitignored secrets
-   No password or JWT logging

## 🎯 Project Goals

This project was built to practice production-oriented Java backend
development.

The main goals were:

1.  Build a non-trivial REST API.
2.  Apply layered backend architecture.
3.  Implement authentication and authorization.
4.  Handle financial transaction logic safely.
5.  Write automated tests.
6.  Introduce database migrations.
7.  Containerize the application.
8.  Build a CI/CD pipeline.
9.  Publish the application container to a registry.

## 🧠 What I Learned

-   Spring Boot application architecture
-   REST API design
-   Spring Security
-   JWT authentication
-   JPA/Hibernate
-   MySQL relational modeling
-   Flyway database migrations
-   DTOs and validation
-   Exception handling
-   Transaction management
-   Concurrency and double-spending protection
-   Automated testing
-   Docker and Docker Compose
-   GitHub Actions
-   GitHub Container Registry
-   Environment-based configuration
-   Application monitoring and logging

## 📌 Project Status

**Version 1.0 --- Complete**

The current version focuses on the backend and development/CI
infrastructure.

Cloud deployment and frontend development are outside the scope of this
version.

## 👨‍💻 Author

**Ankur**

GitHub: [@Ankur400web](https://github.com/Ankur400web)

## 📄 License

This project is primarily a learning and portfolio project.
