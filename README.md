### Tech Stack
- Java 17
- Spring Boot 3
- Mysql 8

### Setup
Set the following application properties according to the environemnt:

```
server.port=8087
spring.datasource.url=jdbc:mysql://localhost:3306/justlife
spring.datasource.username=root
spring.datasource.password=root
spring.liquibase.url=jdbc:mysql://localhost:3306/justlife
spring.liquibase.user=root
spring.liquibase.password=root
```

This project uses liquibase for database migrations. When the application is started it will automatically run migrations and seed data.
Seed data can be found in **src/main/resources/seed/data.sql**

### API Documentation
This project uses Swagger for API documentation. When application is started, the documentation will be available on the following path:

```
http://[host]:[port]/swagger-ui/index.html
```

### Entity Relationship Diagram

![justlife_booking_system_erd](https://github.com/SyedHaris/justlife/assets/10946790/230c6ab6-e1b3-43cf-9a1d-a5540d068a75)


### Testing
Run the following command to run test cases:

```
mvn clean verify
```
