# LMS Backend

A Learning Management System (LMS) backend built with Java and Spring Boot.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Security Crypto (BCrypt)
- Maven
- Lombok
- Jakarta Validation

## Current Progress

- [x] Spring Boot project setup
- [x] PostgreSQL database configuration
- [x] User entity and repository
- [x] User registration API
- [x] Password hashing with BCrypt
- [ ] Global exception handling
- [ ] Authentication with JWT
- [ ] Course management
- [ ] Lesson management
- [ ] Quiz
- [ ] Payment integration

## API

### Register

`POST /api/auth/register`

Request body:

```json
{
  "username": "linh",
  "email": "linh@example.com",
  "password": "123456",
  "confirmPassword": "123456"
}
```

## Run Locally

1. Install Java 17 and PostgreSQL.
2. Create a PostgreSQL database named `lms_db`.
3. Configure the environment variables `DB_USERNAME` and `DB_PASSWORD`.
4. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

This project is built for learning Java backend development with Spring Boot.