# Sentinel — Project Overview

## 1. Project Description

**Sentinel — Server Failure Early Detection System** is a server monitoring application designed to collect system performance metrics, track server availability, and notify users about potentially critical resource usage.

The current MVP provides a centralized monitoring dashboard, server inventory management, CPU and RAM metrics collection, and threshold-based incident generation.

The system is implemented as a modular application consisting of a React frontend, a Java Spring Boot backend, a standalone Java monitoring agent, and a PostgreSQL database.

Sentinel currently uses threshold-based monitoring. It does not implement machine learning or guarantee prediction of server failures.

## 2. Technology Stack

| Component          | Technology                         | Purpose                                   |
| ------------------ | ---------------------------------- | ----------------------------------------- |
| Backend            | Java 21                            | Main application logic                    |
| Backend framework  | Spring Boot 3.4.5                  | REST API and application configuration    |
| Data access        | Spring Data JPA, Hibernate         | Database interaction                      |
| Frontend           | React 18, TypeScript               | User interface                            |
| Build tool         | Vite 6                             | Frontend development and production build |
| HTTP client        | Axios                              | Communication with backend                |
| Charts             | Recharts                           | Metrics visualization                     |
| Icons              | Lucide React                       | Interface icons                           |
| Monitoring Agent   | Java 21                            | System metrics collection                 |
| System information | OSHI 6.6.5                         | Reading CPU and memory information        |
| Database           | PostgreSQL 16                      | Persistent data storage                   |
| Containerization   | Docker, Docker Compose             | Local application deployment              |
| API documentation  | Springdoc OpenAPI                  | API specification and Swagger UI          |
| Backend testing    | JUnit 5, Mockito, Spring Boot Test | Automated backend testing                 |

## 3. System Architecture

Sentinel follows a modular monolithic backend architecture. The backend is deployed as one Spring Boot application rather than a collection of independent microservices.

The system consists of the following components:

### 3.1. Frontend

The frontend is a single-page application built with React and TypeScript.

Its responsibilities include:

- displaying the monitoring dashboard;
- displaying registered servers;
- showing CPU and RAM usage;
- visualizing metric history;
- displaying recent incidents;
- providing server creation and editing interfaces;
- allowing users to acknowledge incidents;
- periodically refreshing monitoring information.

The frontend communicates with the backend through HTTP REST requests using Axios.

The dashboard refreshes server and incident information every 15 seconds.

### 3.2. Backend

The backend is implemented using Java and Spring Boot.

It provides the main application logic and exposes REST API endpoints.

Its responsibilities include:

- managing server records;
- receiving monitoring data from agents;
- validating incoming metric requests;
- storing metric records;
- updating server monitoring information;
- evaluating CPU and RAM thresholds;
- creating and automatically resolving incidents;
- providing monitoring data to the frontend.

The backend uses Spring Data JPA and Hibernate to communicate with PostgreSQL.

### 3.3. Monitoring Agent

The Monitoring Agent is a separate Java application.

It runs on the monitored machine and collects system information using the OSHI library.

The current implementation collects:

- CPU utilization percentage;
- memory utilization percentage.

The agent periodically sends collected information to the backend through the metric ingestion endpoint.

The reporting interval is 15 seconds.

Each request contains:

- server UUID;
- CPU utilization;
- memory utilization;
- metric recording timestamp.

The agent authenticates requests using the `X-Agent-Token` HTTP header.

### 3.4. Database

PostgreSQL is used for persistent storage.

The current data model includes the following main entities:

- `ServerNode` — registered server information and current monitoring state;
- `Metric` — historical CPU and memory measurements;
- `Incident` — monitoring events generated when configured thresholds are exceeded.

The database is accessed through Spring Data JPA repositories.

Hibernate schema management is currently configured with `ddl-auto: update`.

## 4. Data Flow

The main monitoring process consists of the following steps:

1. The Monitoring Agent starts on the monitored machine.
2. The agent reads CPU and memory utilization using OSHI.
3. The agent creates a metric request containing the server UUID, metric values, and timestamp.
4. The agent sends the request to the backend using HTTP POST.
5. The backend validates the agent token and request data.
6. The backend stores the received metrics in PostgreSQL.
7. The backend evaluates CPU and memory values against configured thresholds.
8. If a threshold is exceeded, the backend creates an incident or updates the server monitoring status.
9. If resource utilization returns below the warning threshold, the corresponding active incident is automatically resolved.
10. The frontend periodically requests updated server and incident information and displays the results.

## 5. Monitoring and Alert Logic

Sentinel uses configurable threshold values to identify elevated resource utilization.

The current default thresholds are:

| Metric             | Warning | Critical |
| ------------------ | ------: | -------: |
| CPU utilization    |     75% |      90% |
| Memory utilization |     80% |      92% |

The backend evaluates CPU and memory independently.

### 5.1. Incident Creation

When CPU or memory utilization reaches a configured threshold, the backend determines the corresponding incident severity.

The current severity levels are:

- `INFO` — informational event;
- `WARNING` — resource utilization has reached the warning threshold;
- `CRITICAL` — resource utilization has reached the critical threshold.

The backend creates separate incidents for CPU and memory conditions.

### 5.2. Incident Lifecycle

The current incident statuses include:

- `OPEN` — incident has been created and requires attention;
- `ACKNOWLEDGED` — incident has been acknowledged;
- `RESOLVED` — incident condition has been cleared.

Users can acknowledge an incident through the API.

The backend automatically resolves the corresponding active incident when the monitored metric falls below its warning threshold.

The current implementation does not provide a separate manual incident resolution endpoint.

### 5.3. Server Status

The backend calculates the overall server status based on the latest CPU and memory measurements.

The status is determined as follows:

- `CRITICAL` — at least one monitored metric has reached its critical threshold;
- `WARNING` — at least one monitored metric has reached its warning threshold, but no metric is critical;
- `HEALTHY` — monitored metrics are below their warning thresholds.

Although an `OFFLINE` status is defined in the server status model, automatic offline detection based on missing heartbeats has not yet been implemented.

## 6. Main REST API Areas

The backend exposes REST endpoints for the following operations:

| Resource         | Supported operations                       |
| ---------------- | ------------------------------------------ |
| Servers          | List, create, update, delete               |
| Server metrics   | Retrieve metric history                    |
| Metric ingestion | Receive metrics from monitoring agents     |
| Incidents        | List incidents and acknowledge an incident |

The detailed endpoint descriptions, request structures, response formats, and examples are documented in [API.md](API.md).

The API documentation is also available through Swagger UI when the backend is running:

`http://localhost:8081/swagger-ui.html`

## 7. Project Structure

The repository is organized into separate directories for the backend, frontend, monitoring agent, documentation, and infrastructure configuration.

```text
sentinel/
│
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/com/sentinel/
│       │   │   ├── api/
│       │   │   ├── config/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/
│       └── test/
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.ts
│
├── monitoring-agent/
│   └── src/
│       └── main/
│           └── java/com/sentinel/agent/
│
├── docs/
│   ├── API.md
│   ├── PROJECT_OVERVIEW.md
│   ├── ASSIGNMENT_MAPPING.md
│   └── qa/
│       └── TEST_CASES.md
│
├── infrastructure/
│   └── terraform/
│       └── README.md
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

## 8. Security and Current Limitations

The current implementation is an MVP intended for development and demonstration.

The following limitations should be considered:

- User authentication has not been implemented.
- Role-based access control has not been implemented.
- Server and incident management endpoints do not currently require user authentication.
- Monitoring agent requests use a shared token configured through an environment variable.
- The default development token must be replaced before any real deployment.
- HTTPS and production secret management have not been configured.
- Automatic detection of offline servers has not been implemented.
- The monitoring agent currently collects CPU and RAM metrics only.
- Agent retry and reconnection mechanisms have not been implemented.
- Metric history retrieval is limited to the latest 100 records.
- Advanced metric filtering and pagination are not implemented.
- The current backend test suite is limited and requires additional unit and integration tests.

These limitations define the current project scope and should not be interpreted as completed functionality.

## 9. Deployment and Development Environment

The project uses Docker Compose to run the main application components locally.

The current development environment includes:

| Service    | Container port | Host port |
| ---------- | -------------: | --------: |
| Backend    |           8080 |      8081 |
| Frontend   |             80 |      5173 |
| PostgreSQL |           5432 |      5434 |

The frontend is available at:

`http://localhost:5173`

The backend API is available at:

`http://localhost:8081/api`

The Swagger UI is available at:

`http://localhost:8081/swagger-ui.html`

The Monitoring Agent is built as a standalone executable JAR and can be started separately on the monitored machine.

Detailed setup and execution instructions are provided in the root `README.md`.

## 10. Planned Improvements

The following features are possible directions for further development and are not considered implemented functionality:

- user authentication and authorization;
- workspace and user management;
- automatic offline server detection;
- additional monitoring metrics, including disk and network utilization;
- agent retry and reconnection mechanisms;
- configurable alert thresholds;
- advanced metric filtering and pagination;
- notification delivery;
- extended automated testing;
- production deployment configuration;
- infrastructure provisioning with Terraform;
- monitoring and observability of the Sentinel application itself.

## 11. Conclusion

Sentinel provides the foundation for centralized server monitoring through periodic collection of system metrics, persistent storage, threshold-based incident generation, and dashboard visualization.

The current MVP demonstrates the complete basic monitoring data flow from a separate monitoring agent to the backend and database, followed by presentation of monitoring information in the frontend.

The architecture allows the system to be extended with additional monitoring capabilities, security mechanisms, notification services, and deployment infrastructure.
