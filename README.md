# Test Assignment — Product REST API

API for managing Products. Built with Java 17 and Spring Boot.

## Notes

- The goal of this project is to showcase clean, minimalistic backend design.
- Priority is given to clarity, readability, and practical maintainability.
- The project architecture is organized by feature rather than by technical layer. This means
- that all code related to a specific domain concept (e.g., Product) - including controllers, services etc. - is grouped together.

## Local Setup

1. Start Postgres in a separate Docker container:

```bash
docker-compose up -d
```

2. Build and run the app:

```bash
   ./gradlew clean build
   ./gradlew bootRun
```

3. Access the API at:

```
   http://localhost:8080/products
```

## Testing

The tests in this project are intentionally separated into two categories:

- **Unit tests** are placed in the standard `src/test/java` directory.
- **Integration tests** are placed in a separate `src/integrationTest/java` source set.

This separation is deliberate to distinguish:

- **Unit tests**:  
  Test individual components or logic in isolation.  
  They do **not** require the Spring context or any framework bootstrapping.  
  They are lightweight and run extremely fast.

- **Integration tests**:
  Verify the interaction between multiple layers of the application.  
  They **do** start the Spring context and utilize a realistic environment setup.  
  Integration tests are executed using the **Testcontainers** framework, spinning up real Docker containers for each test to simulate production-like conditions.

Thanks to this architecture:

- Unit tests remain fast, simple, and independent.
- Integration tests remain reliable and close to real-world scenarios. As a side effect, they naturally take more time to execute.
