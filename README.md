# LMS Backend (Learning Management System)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

A robust and modular Learning Management System (LMS) backend service built with Java 17 and Spring Boot. This application provides core authentication, user lifecycle management, role-based access foundations, and database persistence with PostgreSQL.

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Project Architecture](#-project-architecture)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Database Configuration](#database-configuration)
  - [Running the Application](#running-the-application)
- [Running Tests](#-running-tests)
- [API Documentation](#-api-documentation)
  - [Register User](#1-register-user)
  - [Login User](#2-login-user)
  - [Error Handling & Status Codes](#3-error-handling--status-codes)
- [Development Roadmap](#-development-roadmap)
- [Contributing & License](#-contributing--license)

---

## 🚀 Overview

The LMS Backend is designed to handle core functionalities of an online education platform. Currently implemented features include:
- **User Registration**: Duplicate email/username validation, password confirmation matching, and secure password hashing using BCrypt.
- **User Authentication**: Credential validation with email & password, and account lock status check.
- **Global Error Handling**: Centralized exception handler mapping business errors to standard HTTP status codes (`400`, `401`, `403`, `409`).
- **Unit Testing Suite**: Mockito and JUnit 5 tests covering all edge cases of registration and authentication logic.

---

## 🛠 Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java 17 |
| **Framework** | Spring Boot 4.1.0 |
| **Web Layer** | Spring MVC (`spring-boot-starter-webmvc`) |
| **Data Persistence** | Spring Data JPA / Hibernate ORM |
| **Database** | PostgreSQL 17+ |
| **Security** | Spring Security Crypto (`BCryptPasswordEncoder`) |
| **Validation** | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| **Boilerplate Reduction** | Project Lombok |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |
| **Build Tool** | Apache Maven |

---

## 📂 Project Architecture

The project follows a standard multi-tier Spring Boot architecture:

```text
src/
├── main/
│   ├── java/com/linh/lms/
│   │   ├── config/              # Application configurations (SecurityConfig)
│   │   ├── controller/          # REST Controllers handling incoming HTTP requests
│   │   ├── dto/                 # Data Transfer Objects (Requests & Responses)
│   │   ├── entity/              # JPA Entities (User, Role)
│   │   ├── exception/           # Custom business exceptions & GlobalExceptionHandler
│   │   ├── repository/          # Spring Data JPA repositories
│   │   ├── service/             # Business logic layer
│   │   └── LmsBackendApplication.java # Spring Boot entry point
│   └── resources/
│       └── application.properties     # Application configuration & datasource
└── test/
    └── java/com/linh/lms/
        ├── service/
        │   └── UserServiceTest.java   # Unit tests with Mockito
        └── LmsBackendApplicationTests.java
```

---

## 🗄 Database Schema

The core user entity is mapped to the `users` table:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    address VARCHAR(255),
    status BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50) NOT NULL DEFAULT 'STUDENT'
);
```

### Supported User Roles (`Role` Enum)
- `STUDENT`: Default role upon registration.
- `TEACHER`: Instructor role for course creation.
- `ADMIN`: Administrator role for system management.

---

## ⚙ Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher
- **PostgreSQL**: Version 14 or higher running on port `5432`
- **Git**

### Environment Variables

Before running the backend, ensure the database credentials are set in your environment:

| Variable | Description | Example |
| :--- | :--- | :--- |
| `DB_USERNAME` | PostgreSQL database username | `postgres` |
| `DB_PASSWORD` | PostgreSQL database password | `your_secret_password` |

On Windows (PowerShell):
```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_secret_password"
```

On Linux / macOS:
```bash
export DB_USERNAME="postgres"
export DB_PASSWORD="your_secret_password"
```

### Database Configuration

1. Create a database named `lms_db` in your PostgreSQL instance:
   ```sql
   CREATE DATABASE lms_db;
   ```
2. Hibernate is set to `spring.jpa.hibernate.ddl-auto=update`, so tables will be generated automatically upon application startup.

### Running the Application

Using the Maven Wrapper included in the repository:

**On Windows (PowerShell / Command Prompt):**
```powershell
.\mvnw.cmd spring-boot:run
```

**On Linux / macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## 🧪 Running Tests

The test suite contains Mockito unit tests verifying registration validation, duplicate detection, and login credential verification.

Run all tests via Maven:

```powershell
.\mvnw.cmd test
```

---

## 📡 API Documentation

Base URL: `http://localhost:8080/api/auth`

### 1. Register User

Registers a new user account with role `STUDENT`.

- **Endpoint:** `POST /api/auth/register`
- **Headers:** `Content-Type: application/json`

#### Request Body
```json
{
  "username": "student01",
  "email": "student@example.com",
  "password": "Password123",
  "confirmPassword": "Password123"
}
```

#### Validation Rules
- `username`: Not blank
- `email`: Not blank, must be valid email format
- `password`: Not blank
- `confirmPassword`: Must match `password`

#### Success Response (`200 OK`)
```json
{
  "username": "student01",
  "email": "student@example.com"
}
```

#### Error Responses
- **`400 Bad Request`**: Validation failure or password mismatch (`Password không khớp với PasswordConfirm`).
- **`409 Conflict`**: Email already in use (`Email đã được dùng`) or Username already in use (`Username đã được dùng`).

---

### 2. Login User

Authenticates an existing user.

- **Endpoint:** `POST /api/auth/login`
- **Headers:** `Content-Type: application/json`

#### Request Body
```json
{
  "email": "student@example.com",
  "password": "Password123"
}
```

#### Success Response (`200 OK`)
```json
{
  "id": 1,
  "username": "student01",
  "email": "student@example.com",
  "role": "STUDENT"
}
```

#### Error Responses
- **`400 Bad Request`**: Missing email or password format invalid.
- **`401 Unauthorized`**: Incorrect credentials (`Mật khẩu hoặc email không chính xác`).
- **`403 Forbidden`**: Account is disabled/locked (`Tài khoản của bạn đã bị khóa`).

---

### 3. Error Handling & Status Codes

All errors are handled by `GlobalExceptionHandler`:

| Status Code | Description | Scenario |
| :--- | :--- | :--- |
| `400 Bad Request` | Validation Error or Password Mismatch | Returns validation field map or mismatch message |
| `401 Unauthorized` | Invalid Credentials | Email not found or password does not match |
| `403 Forbidden` | Account Locked | User status is `false` |
| `409 Conflict` | Unique Constraint Violation | Email or Username already registered |

#### Validation Error Format (`400 Bad Request`)
```json
{
  "email": ["must be a well-formed email address"],
  "username": ["must not be blank"]
}
```

---

## 🗺 Development Roadmap

- [x] Initial Spring Boot 4 setup & PostgreSQL connection
- [x] User entity, Role enum, and repository
- [x] BCrypt password hashing
- [x] Registration and Login endpoints
- [x] Global Exception Handler & validation
- [x] Comprehensive unit tests with Mockito
- [ ] JWT (JSON Web Token) access & refresh token implementation
- [ ] Spring Security filter chain & role-based route protection
- [ ] Course & Category management APIs
- [ ] Module & Lesson management (Video / Documents)
- [ ] Quiz & Assessment engine
- [ ] Student enrollment & progress tracking
- [ ] Payment gateway integration (VNPay / Stripe)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).