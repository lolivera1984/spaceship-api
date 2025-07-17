# Spaceship API

This is a Spring Boot REST API to manage spaceships from movies and series.

## Requirements
✅ Functional Requirements
- Retrieve all spaceships using pagination.
- Retrieve a single spaceship by its unique identifier.
- Retrieve all spaceships whose name contains a search parameter.
For example, if the request parameter is "wing", the response should include "X-Wing".
- Create a new spaceship.
- Update an existing spaceship.
- Delete a spaceship.

✅ Technical Requirements

- Develop an @Aspect to log a message when a spaceship is requested using a negative ID.
- Centralized exception handling must be implemented.
- Some type of caching mechanism must be used (e.g., in-memory cache).

✅ Infrastructure & Implementation Guidelines

- A database migration library (Mongock) to manage DDL scripts.
- The application must be containerized using Docker.
- API documentation with Swagger.
- Security (via JWT).
- Message broker integration, (Kafka).


## 🚀 Features

- CRUD for spaceship entities
- MongoDB persistence
- Kafka messaging for spaceship events (delete flow / producer)
- Security using JWT
- Caching using Caffeine (findById , 1 min expiration)
- Centralized exception handling
- Aspect to log when accessing a spaceship with negative ID (WARN level)
- OpenAPI documentation (Swagger UI)
- Dockerized environment
- Integration tests with Testcontainers

## 🧪 Test Credentials

Login using:

```
POST /auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

Returns a JWT token to be used as:
```
Authorization: Bearer <token>
```

------------------------------------------------------------------------

## 🐳 Docker

To run the app locally:

```bash
docker-compose up --build
```

This will start:
- Spring Boot app
- MongoDB
- Kafka + Zookeeper

## 📚 Swagger

Once the app is running, access the API docs:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🧪 Tests

Includes:
- Unit tests
- Integration tests using Testcontainers for MongoDB and Kafka

To Run tests locally:
```bash
mvn clean test
```

## 🛠 Tech Stack

- Java 21
- Spring Boot 3.3
- MongoDB
- Apache Kafka
- JWT + Spring Security
- Caffeine Cache
- Springdoc OpenAPI 3

------------------------------------------------------------------------

## How to Run Locally
Prerequisites:
- Java 21
- Maven 3.9+
- Docker and Docker Compose

Steps:

1- Clone the repo and enter the project directory:

2- Start the app with MongoDB and Kafka using Docker:

```bash
DOCKER_DEFAULT_PLATFORM=linux/amd64 docker-compose -f ./docker/docker-compose.yml up --build
```

3- Wait for the application to start (look for: Started SpaceshipApiApplication).

4- Access the Swagger UI for API testing:
http://localhost:8080/swagger-ui/index.html

5- Login to get a JWT token:

```bash
curl -X POST http://localhost:8080/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{"username": "admin", "password": "admin123"}'
```
Use the token in subsequent requests:

```bash
--- Get All ----
curl -X GET \
  "http://localhost:8080/spaceships" \
  -H "accept: */*" \
  -H "Authorization: Bearer <token>"
```

```bash
--- Get by id ----
curl -X GET \
  "http://localhost:8080/spaceships/686f2a5450ffba61fc498ec5" \
  -H 'accept: */*' \
  -H "Authorization: Bearer <token>"
```

```bash
--- Search ----
curl -X GET \
  "http://localhost:8080/spaceships/search?name=Space" \
  -H 'accept: */*' \
  -H "Authorization: Bearer <token>"
```

```bash
--- Create ----
curl -X 'POST' \
  'http://localhost:8080/spaceships' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer <token>" \
  -d '{
  "name": "testNameCreated",
  "model": "testModel",
  "manufacturer": "testManufacturer"
}'
```

```bash
--- Update ----
curl -X 'PUT' \
  'http://localhost:8080/spaceships/686f568026114c3d165ef785' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer <token>" \
  -d '{
  "name": "testNameCreated1",
  "model": "testModel1",
  "manufacturer": "testManufacturer1"
}'
```

```bash
--- Delete ----
curl -X 'DELETE' \
  'http://localhost:8080/spaceships/686f568026114c3d165ef785' \
  -H 'accept: */*' \
  -H "Authorization: Bearer <token>" 
```


6- Stop all containers:
```bash
docker compose -f docker/docker-compose.yml down
```

------------------------------------------------------------------------

Another option could be to execute the containers dependencies (mongo and kafka) from terminal, and run the Springboot from IntelliJ  

1- Start Mongo and Kafka containers using Docker:
```bash
DOCKER_DEFAULT_PLATFORM=linux/amd64 docker-compose -f docker/docker-compose.yml up mongodb kafka zookeeper
```

2- Run IntelliJ (run configurations):
```bash
VM Options:
-Dspring.profiles.active=local 
```

------------------------------------------------------------------------
## Tech Debt
- Custom Validators in the controller (Annotations)
- Custom Request DTO/record for every endpoint with body request
- MapStruct or some mapper to convert between model and DTO/records
- Add more unit test coverage (reach at least 80%)
- Implement caché in more places in the service and in the DDBB level 
- Kafka create a consumer for the delete flow, which write in a special log (auditory mongo collection)