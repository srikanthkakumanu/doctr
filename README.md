# Doctr Microservice

This project is a simple Spring Boot microservice for managing doctors. It provides a RESTful API for all standard CRUD (Create, Read, Update, Delete) operations.

## Features

- **CRUD Operations**: Full support for creating, reading, updating, and deleting doctor records.
- **Pagination**: The API supports pagination for retrieving lists of doctors.
- **HATEOAS Support**: REST API follows HATEOAS principles with hypermedia links for better discoverability.
- **Security**: Implemented OWASP security best practices including authentication, input validation, and CORS support.
- **In-Memory Database**: Uses H2 as an in-memory database for easy setup and testing.
- **API Documentation**: Integrated Swagger UI for clear, interactive API documentation.
- **Modern Java**: Built with modern, clean Java code.
- **Code Quality**: Integrated SonarQube for static code analysis.
- **Security Scanning**: OWASP Dependency Check for vulnerability scanning.
- **Test Coverage**: JaCoCo for code coverage reporting.
- **CI/CD Pipeline**: Jenkins pipeline for automated building, testing, Docker image creation, and deployment.

## Technologies Used

- **Java 21**: The core programming language.
- **Spring Boot 3.4.4**: The application framework.
- **Spring Web**: For building the RESTful API.
- **Spring Data JPA**: For database interaction.
- **Spring Security**: For authentication and authorization.
- **Spring Validation**: For input validation.
- **Spring HATEOAS**: For hypermedia-driven REST APIs.
- **H2 Database**: An in-memory database.
- **Lombok**: To reduce boilerplate code.
- **Springdoc OpenAPI**: For generating Swagger API documentation.
- **Gradle 9.3.1**: The build automation tool.
- **JaCoCo**: For code coverage.
- **SonarQube**: For code quality analysis.
- **OWASP Dependency Check**: For security vulnerability scanning.
- **Docker**: For containerization.
- **Kubernetes**: For deployment orchestration.
- **Jenkins**: For CI/CD pipeline.

## Getting Started

### Prerequisites

- JDK 21 or later
- Gradle 8.x or later
- Docker (for containerization)
- Kubernetes cluster (for deployment)

### Building and Running the Application

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd doctr
    ```

2.  **Run the application using Gradle:**
    ```bash
    ./gradlew bootRun
    ```

The application will start on `http://localhost:9091`.

**Note**: The API endpoints require authentication. Use HTTP Basic Auth with username `admin` and password `password` when making API calls.

### Running with Docker

1. Build the Docker image:

   ```bash
   docker build -t doctr .
   ```

2. Run the container:
   ```bash
   docker run -p 9091:9091 doctr
   ```

## Accessing the H2 Database Console

To view and interact with the data directly, you can enable the H2 web console. The project is already configured for this.

- **H2 Console**: [http://localhost:9091/h2](http://localhost:9091/h2)

Use the default settings to connect (`JDBC URL: jdbc:h2:mem:docterdb`).

## Getting Started

### Prerequisites

- JDK 21 or later
- Gradle 8.x

### Building and Running the Application

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd doctr
    ```

2.  **Run the application using Gradle:**
    ```bash
    ./gradlew bootRun
    ```

The application will start on `http://localhost:9091`.

**Note**: The API endpoints require authentication. Use HTTP Basic Auth with username `admin` and password `password` when making API calls.

## Accessing the H2 Database Console

To view and interact with the data directly, you can enable the H2 web console. The project is already configured for this.

- **H2 Console**: [http://localhost:9091/h2](http://localhost:9091/h2)

Use the default settings to connect (`JDBC URL: jdbc:h2:mem:doctordb`).

## API Endpoints

The API provides the following endpoints for managing doctors. All endpoints require HTTP Basic Authentication and return HATEOAS-compliant responses with hypermedia links.

| Method   | Endpoint                         | Description                                                          |
| -------- | -------------------------------- | -------------------------------------------------------------------- |
| `POST`   | `/api/doctors`                   | Creates a new doctor. Returns created resource with links.           |
| `GET`    | `/api/doctors`                   | Retrieves a paginated list of all doctors with navigation links.     |
| `GET`    | `/api/doctors?pincode={pincode}` | Finds doctors by their pincode (paginated) with links.               |
| `GET`    | `/api/doctors/{id}`              | Retrieves a single doctor by their ID with action links.             |
| `PUT`    | `/api/doctors/{id}`              | Updates the details of an existing doctor. Returns updated resource. |
| `DELETE` | `/api/doctors/{id}`              | Deletes a doctor by their ID.                                        |
| `DELETE` | `/api/doctors`                   | Deletes all doctors.                                                 |

### Pagination Parameters

The `GET /api/doctors` endpoint supports the following query parameters for pagination:

- `page`: The page number to retrieve (0-indexed).
- `size`: The number of items per page.
- `sort`: A comma-separated list of properties to sort by (e.g., `lastName,asc`).

**Example:**

```
GET /api/doctors?page=0&size=5&sort=lastName,asc
```

This request retrieves the first page of 5 doctors, sorted by their last name in ascending order.

### HATEOAS Response Format

All API responses follow HATEOAS principles and include hypermedia links in the `_links` section:

**Single Doctor Response:**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "address": "123 Main St",
  "city": "Anytown",
  "pincode": "12345",
  "_links": {
    "self": { "href": "http://localhost:9091/api/doctors/1" },
    "update": { "href": "http://localhost:9091/api/doctors/1" },
    "delete": { "href": "http://localhost:9091/api/doctors/1" },
    "doctors": { "href": "http://localhost:9091/api/doctors" }
  }
}
```

**Paginated List Response:**

```json
{
  "content": [...],
  "page": {
    "size": 10,
    "totalElements": 25,
    "totalPages": 3,
    "number": 0
  },
  "_links": {
    "self": { "href": "http://localhost:9091/api/doctors?page=0&size=10" },
    "next": { "href": "http://localhost:9091/api/doctors?page=1&size=10" },
    "create": { "href": "http://localhost:9091/api/doctors" }
  }
}
```

## Security

The API implements OWASP security best practices:

### Authentication

- **HTTP Basic Authentication**: All API endpoints require authentication.
- **Default Credentials**: `admin` / `password` (change in production).
- **Public Endpoints**: Actuator health checks and H2 console are publicly accessible.

### Input Validation

- All input fields are validated using Jakarta Bean Validation.
- Required fields: firstName, lastName, address, city, pincode.
- Field constraints: names (2-50 chars), address (max 255), city (max 100), pincode (5-6 digits).

### CORS Support

- Cross-Origin Resource Sharing is enabled for web client integration.
- Configurable allowed origins (currently allows all for development).

### Security Headers

- CSRF protection disabled for REST API.
- Frame options configured for H2 console access.

### Vulnerability Scanning

- OWASP Dependency Check integrated for dependency vulnerability scanning.
- Run with: `./gradlew dependencyCheckAnalyze`

## API Documentation (Swagger UI)

Once the application is running, you can access the interactive Swagger UI to explore the API endpoints in detail.

- **Swagger UI**: [http://localhost:9091/swagger-ui.html](http://localhost:9091/swagger-ui.html)

## Running Tests

To run the full suite of unit and integration tests, use the following Gradle command:

```bash
./gradlew test jacocoTestReport
```

The test suite includes:

- Unit tests for business logic
- Integration tests with authentication
- Validation tests for input constraints
- HATEOAS link verification tests

Test reports will be generated in the `build/test-results/test/` directory, and coverage reports in `build/reports/jacoco/test/html/index.html`.

### SonarQube Analysis

The project is configured for SonarQube code quality analysis. The `sonar-project.properties` file contains the necessary configuration.

To run SonarQube analysis locally:

```bash
sonar-scanner -Dsonar.host.url=https://your-sonarqube-server -Dsonar.login=YOUR_TOKEN
```

### OWASP Dependency Check

The project includes OWASP Dependency Check for identifying known vulnerabilities in project dependencies.

To run vulnerability scanning:

```bash
./gradlew dependencyCheckAnalyze
```

Reports will be generated in `build/reports/dependency-check-report.html`.

## CI/CD Pipeline

The project supports both Jenkins and GitHub Actions for Continuous Integration and Continuous Deployment.

### Jenkins Pipeline

- **Configuration**: [Jenkinsfile](Jenkinsfile)
- **CI Stages**: Build, Test (with JaCoCo), Code Quality (SonarQube), Quality Gate, Build Docker Image, Push to DockerHub.
- **CD Stage**: Deploy to Kubernetes on the `main` branch.

### GitHub Actions Pipeline

- **Workflow**: [.github/workflows/ci-cd.yml](.github/workflows/ci-cd.yml)
- **CI Job**: Build, Test (with JaCoCo), SonarQube Scan, Build and Push Docker Image to DockerHub.
- **CD Job**: Deploy to Kubernetes on push to `main` branch.

### Prerequisites for Both:

- SonarQube server with `SONAR_HOST_URL` and `SONAR_TOKEN` secrets.
- DockerHub credentials (`DOCKERHUB_USERNAME` and `DOCKERHUB_PASSWORD` for GitHub Actions; `dockerhub-credentials` for Jenkins).
- Kubernetes cluster access (`KUBE_CONFIG_DATA` secret for GitHub Actions; kubectl configured for Jenkins).
