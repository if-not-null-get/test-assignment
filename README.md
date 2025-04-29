# Test Assignment — Product REST API

API for managing Products. Built with:
- Java 17
- Gradle 8
- Spring Boot 3
- Spring MVC for REST API design with versioned endpoints (e.g., /api/v1/)
- PostgreSQL in a Docker container
- Flyway migrations
- Spring Data JDBC: at the time of creating this app, it was the preferred lightweight way (at the cost of some verbosity)
   of interacting with DB while avoiding the overhead of ORM/Hibernate
- RestTemplate for 3rd party API calls
- Testcontainers for integration tests (see below)
- JUnit 5 + Mockito for unit tests
- Lombok wasn't used here with the same purpose - to avoid dependencies on additional plugins and/or frameworks
- API is versioned

## Notes

- The goal of this project is to showcase clean, minimalistic backend design.
- The project architecture is organized by feature rather than by technical layer. This means
- that all code related to a specific domain concept (e.g., Product) - including controllers, services etc. - is grouped together.
- Basic functionality is covered with tests and supplemented with basic logging

## Local Setup

Before running these steps, please make sure you've got Docker and Docker Compose installed on your machine:

```bash
docker --version
docker compose version
```
In case they're not installed, please adhere to the official guide https://docs.docker.com/get-docker/

1. Build the application:

```bash
./gradlew clean build
```

This will run both unit and integration tests and prepare the latest version to be run in a Docker environment

2. Start both app and db with Docker Compose:

```bash
   docker-compose up --build
```

3. Access the API at:

```
   http://localhost:8080/api/v1/products
```
Create a new Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "code": "1234567890",
    "name": "Sample Product",
    "priceEur": 19.99,
    "available": true
  }'
```

Get a specific Product by ID

```bash
curl -X GET http://localhost:8080/api/v1/products/{uuid}
```

List all Products

```bash
curl -X GET http://localhost:8080/api/v1/products
```

## Testing

The tests in this project are intentionally separated into two categories:

- **Unit tests** are placed in the standard `src/test/java` directory.
- **Integration tests** are placed in a separate `src/integrationTest/java` source set.

This separation is deliberate to distinguish:

- **Unit tests**:  
  Test individual components and logic in isolation.  
  They do **not** require the Spring context or any framework bootstrapping.  
  They are lightweight and run extremely fast.

- **Integration tests**:
  Verify the interaction between multiple layers of the application.  
  They **do** start the Spring context and utilize a realistic environment setup.  
  Integration tests are executed using the **Testcontainers** framework, spinning up real Docker containers for each test to simulate production-like conditions.

Thanks to this architecture:

- Unit tests remain fast, simple, and independent.
- Integration tests remain reliable and close to real-world scenarios. As a side effect, they naturally take more time to execute.

## Possible Future Improvements

The following features were outside the original assignment scope,
but could be added if the business requirements evolve:

- Security enhancements (authentication, authorization, rate limiting)
- Caching
- Pagination
- Transaction management for critical parts
- Spring profiles
- delete/update/filter-by functionality
- More advanced logging (like MDC, file-based)
- Spring REST Docs for better API documentation
