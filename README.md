# 🖥️ Sentinel — Server Monitoring System

A modern full-stack server monitoring application built with Java and React.

Sentinel is an information system designed to monitor server infrastructure, collect system performance metrics, track resource utilization, and detect potentially critical conditions through threshold-based alerts.

The application provides a centralized dashboard for monitoring registered servers, analyzing CPU and memory usage, and managing incidents.

The project is developed as a modular monolith MVP with a focus on maintainable architecture, REST API communication, relational database design, containerization, automated testing, and practical software engineering principles.

---

## 🛠 Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

- Spring Web
- Spring Data JPA
- Hibernate
- REST API
- OSHI System Information Library

---

### Frontend

![TypeScript](https://img.shields.io/badge/TypeScript-5.7.3-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![React](https://img.shields.io/badge/React-18.3.1-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-6.4.5-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-HTTP_Client-5A29E4?style=for-the-badge&logo=axios&logoColor=white)

- React
- TypeScript
- Vite
- Axios
- Recharts
- Lucide React

---

### Database

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

- PostgreSQL
- Spring Data JPA
- Hibernate ORM
- Relational data modeling
- Entity relationships
- Database persistence

---

### Monitoring Agent

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

- Java Monitoring Agent
- OSHI System Information Library
- CPU utilization collection
- RAM utilization collection
- HTTP metric transmission
- Periodic reporting every 15 seconds

---

### DevOps

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

- Docker
- Docker Compose
- PostgreSQL container
- Spring Boot container
- Nginx-based frontend container
- Environment-based configuration
- GitHub Actions workflow
- Automated E2E test execution in CI

---

### Testing & API Documentation

![JUnit](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

- JUnit 5
- Spring Boot Test
- Maven Surefire
- Automated end-to-end testing
- HTTP API verification
- Swagger UI
- OpenAPI documentation
- Surefire test reports

---

# 📌 Project Overview

## Purpose

The main goal of Sentinel is to develop a centralized server monitoring system that helps identify abnormal infrastructure behavior before it develops into a serious service disruption.

The system collects resource utilization data from monitored machines, stores measurements, evaluates configured threshold values, and creates incidents when CPU or memory usage reaches critical levels.

Sentinel combines:

- server infrastructure management;
- system metrics collection;
- REST API communication;
- relational database persistence;
- threshold-based incident detection;
- interactive monitoring dashboard;
- automated end-to-end testing;
- containerized application deployment;
- continuous integration workflow.

---

# ✨ Main Features

## Server Management

- Register new servers.
- View registered server inventory.
- Store server names, host addresses, and environments.
- Update server information through the API.
- Delete server records.
- Monitor current server status.
- Track the latest heartbeat timestamp.

## System Monitoring

- Collect CPU utilization.
- Collect RAM utilization.
- Receive metrics from the monitoring agent.
- Store historical measurements in PostgreSQL.
- Retrieve the latest 100 metric samples.
- Display CPU and memory utilization charts.
- Refresh dashboard data automatically every 15 seconds.

## Incident Management

- Detect threshold violations.
- Generate WARNING and CRITICAL incidents.
- Track incident severity and status.
- View recent incidents.
- Acknowledge open incidents.
- Automatically resolve threshold-related incidents when the corresponding metric falls below its warning threshold.

## Monitoring Agent

- Collect actual CPU and memory measurements from the host machine.
- Use OSHI to access system information.
- Send measurements to the backend through HTTP.
- Authenticate ingestion requests using a shared agent token.
- Report metrics every 15 seconds.

## Dashboard

- View total registered servers.
- Monitor operational server count.
- Track open incidents.
- View current CPU and RAM utilization.
- Analyze historical resource usage.
- Inspect recent incidents.
- Register new servers through the interface.

---

# 🏗️ System Architecture

Sentinel follows a modular monolith architecture.

The application consists of three main parts:

1. React frontend — provides the monitoring dashboard.
2. Spring Boot backend — handles API requests, business logic, metrics, and incidents.
3. Monitoring Agent — collects system metrics and sends them to the backend.

PostgreSQL is used for persistent data storage.

## General Architecture

```text
                  ┌──────────────────────┐
                  │     React Frontend   │
                  │   TypeScript + Vite  │
                  └──────────┬───────────┘
                             │
                             │ REST API
                             │
                  ┌──────────▼───────────┐
                  │   Spring Boot API    │
                  │      Java 21         │
                  └──────┬─────────┬─────┘
                         │         │
                         │         │
              ┌──────────▼───┐  ┌──▼────────────────┐
              │  PostgreSQL  │  │ Monitoring Agent  │
              │   Database   │  │    Java + OSHI    │
              └──────────────┘  └────────┬───────────┘
                                         │
                                         │ HTTP Metrics
                                         │
                                  ┌──────▼───────┐
                                  │ Monitored    │
                                  │ Host System  │
                                  └──────────────┘
```

## Data Flow

1. The monitoring agent collects CPU and memory utilization from the host machine.
2. The agent sends measurements to the backend using the metric ingestion endpoint.
3. The backend validates the agent token and processes the incoming metrics.
4. Measurements are stored in PostgreSQL.
5. The monitoring service evaluates CPU and memory thresholds.
6. Incidents are created or updated when threshold conditions are met.
7. The frontend retrieves server information, incidents, and metric history through the REST API.
8. The dashboard refreshes its data automatically every 15 seconds.

# 🛠 Technology Stack

## Backend

- Java 21
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- REST API

The backend is responsible for:

- processing HTTP requests;
- managing server records;
- receiving monitoring metrics;
- storing historical measurements;
- evaluating resource utilization thresholds;
- creating and managing incidents.

## Frontend

- TypeScript
- React 18
- Vite
- Axios
- Recharts
- Lucide React

The frontend provides:

- server inventory;
- monitoring dashboard;
- resource utilization charts;
- incident list;
- server registration form;
- automatic data refresh.

## Database

Primary database:

\- PostgreSQL 16

ORM:

- Hibernate
- Spring Data JPA

Database responsibilities:

- storing server information;
- storing metric measurements;
- storing incident records;
- maintaining relationships between monitored servers and their incidents and metrics.

## Monitoring Agent

The monitoring agent is a separate Java application.

It uses OSHI to collect:

- CPU utilization percentage;
- memory utilization percentage.

Collected measurements are transmitted to the backend through an authenticated HTTP endpoint.

## DevOps

- Docker
- Docker Compose
- Nginx
- Environment variables
- Containerized PostgreSQL
- Containerized Spring Boot backend
- Containerized frontend
- GitHub Actions

# 📂 Project Structure

```
sentinel/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sentinel/
│   │   │   │   ├── api/
│   │   │   │   ├── config/
│   │   │   │   ├── domain/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── monitoring-agent/
│   ├── src/
│   │   └── main/java/com/sentinel/agent/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api.ts
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   ├── styles.css
│   │   └── vite-env.d.ts
│   ├── Dockerfile
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
├── e2e-tests/
│   ├── src/
│   │   └── test/
│   │       └── java/com/sentinel/e2e/
│   │           └── AgentIngestionE2ETest.java
│   └── pom.xml
│
├── .github/
│   └── workflows/
│       └── e2e-tests.yml
│
├── docs/
│   ├── API.md
│   ├── ASSIGNMENT_MAPPING.md
│   ├── PROJECT_OVERVIEW.md
│   └── qa/
│       └── TEST_CASES.md
│
├── infrastructure/
│   └── terraform/
│       └── README.md
│
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── README.md
```

# ⚙️ Installation and Setup

## Requirements

Before installation, make sure the following tools are available:

- Java Development Kit 21
- Apache Maven 3.9+
- Node.js 22+
- npm
- Docker Desktop
- Git

# 🚀 Running the Application

## Option 1: Docker Compose

The recommended way to start the complete local application.

Build and start all services:

```
docker compose up --build
```

Docker Compose starts:

- PostgreSQL database;
- Spring Boot backend;
- Nginx frontend.

Application URLs:

Frontend:

```
http://localhost:5173
```

Backend API:

```
http://localhost:8081/api
```

Swagger UI:

```
http://localhost:8081/swagger-ui.html
```

PostgreSQL:

```
localhost:5434
```

To stop the application:

```
docker compose down
```

The PostgreSQL data is stored in a Docker volume and is preserved when containers are stopped using this command.

## Option 2: Local Development

### 1. Start PostgreSQL

From the project root:

```
docker compose up -d db
```

PostgreSQL will be available at:

```
localhost:5434
```

### 2. Start Backend

Open a PowerShell terminal in the project root.

Configure the database connection:

```
$env:DB_HOST = "localhost"
$env:DB_PORT = "5434"
$env:POSTGRES_DB = "sentinel"
$env:POSTGRES_USER = "sentinel"
$env:POSTGRES_PASSWORD = "sentinel"
$env:SERVER_PORT = "8081"
```

Start the backend:

```
mvn -pl backend -am spring-boot:run
```

Backend API:

```
http://localhost:8081/api
```

Swagger UI:

```
http://localhost:8081/swagger-ui.html
```

### 3. Start Frontend

Open another terminal:

```
cd frontend
npm install
npm run dev
```

Frontend:

```
http://localhost:5173
```

The frontend communicates with the backend using:

```
http://localhost:8081/api
```

# 🤖 Monitoring Agent Setup

The Monitoring Agent collects real CPU and RAM measurements from the host machine.

## 1. Register a Server

Start the application and open the dashboard.

Select Add server and register a server.

Example:

```
Name: Example Server
Host: 127.0.0.1
Environment: Development
```

Retrieve the registered server UUID using:

```
GET /api/servers
```

## 2. Configure Agent Environment Variables

Open a separate PowerShell terminal in the project root.

Set the server UUID, backend URL, and authentication token:

```
$env:SENTINEL_SERVER_ID = "YOUR-SERVER-UUID"
$env:SENTINEL_API_URL = "http://localhost:8081/api/metrics/ingest"
$env:SENTINEL_AGENT_TOKEN = "YOUR_AGENT_TOKEN"
```

The backend token must match the value configured through the `AGENT_TOKEN` environment variable.

## 3. Build the Monitoring Agent

```
mvn -pl monitoring-agent -am package
```

## 4. Run the Agent

```
java -jar monitoring-agent/target/sentinel-monitoring-agent-0.1.0-jar-with-dependencies.jar
```

The agent collects CPU and memory utilization and sends measurements to the backend every 15 seconds.

Keep the agent process running while monitoring is required.

# 📖 API Documentation

The backend provides REST API endpoints for server management, metric ingestion, and incident management.

| Method | Endpoint                          | Description                            |
| ------ | --------------------------------- | -------------------------------------- |
| GET    | `/api/servers`                    | Retrieve registered servers            |
| POST   | `/api/servers`                    | Register a new server                  |
| PUT    | `/api/servers/{id}`               | Update server information              |
| DELETE | `/api/servers/{id}`               | Delete a server                        |
| GET    | `/api/servers/{id}/metrics`       | Retrieve the latest 100 metric samples |
| POST   | `/api/metrics/ingest`             | Receive monitoring metrics             |
| GET    | `/api/incidents`                  | Retrieve incidents                     |
| POST   | `/api/incidents/{id}/acknowledge` | Acknowledge an incident                |

Metric ingestion requires the `X-Agent-Token` HTTP header.

Example request:

```
POST /api/metrics/ingest
X-Agent-Token: YOUR_AGENT_TOKEN
Content-Type: application/json
```

Example JSON body:

```
{
  "serverId": "00000000-0000-0000-0000-000000000000",
  "cpuPercent": 42.8,
  "memoryPercent": 67.1,
  "recordedAt": "2026-10-31T10:00:00Z"
}
```

Swagger UI is available at:

```
http://localhost:8081/swagger-ui.html
```

For additional details, see [docs/API.md](https://docs/API.md).

# 🚨 Alert Thresholds

Sentinel currently uses threshold-based monitoring rules.

| Metric             | WARNING | CRITICAL |
| ------------------ | ------- | -------- |
| CPU utilization    | 75%     | 90%      |
| Memory utilization | 80%     | 92%      |

When a metric reaches a configured threshold, the backend creates an incident with the corresponding severity.

When the metric falls below its warning threshold, the corresponding active threshold incident is resolved automatically.

These alerts identify abnormal resource utilization. They do not guarantee or predict that a server will fail.

# 🧪 Testing & Quality

The project uses automated testing at different levels.

## Backend Tests

The backend uses:

- JUnit 5
- Spring Boot Test
- Maven test execution

Run backend tests:

```
mvn -pl backend -am test
```

## End-to-End Tests

A separate Maven-based E2E test module is available in `e2e-tests/`.

The E2E tests use JUnit 5 and Java HTTP Client to verify the interaction between the Monitoring Agent, backend REST API, and PostgreSQL persistence.

Implemented scenarios:

- Create a dedicated test server through the backend API.
- Start the actual Monitoring Agent as a separate operating system process.
- Verify that the agent successfully sends monitoring measurements.
- Verify that the backend accepts metric ingestion with a valid token.
- Verify that received CPU and memory measurements are persisted and returned through the metric history API.
- Validate metric values and timestamps.
- Verify that metric ingestion with an invalid authentication token returns HTTP 401.

The test starts its own agent process and terminates that process after execution.

Run E2E tests locally:

First, build the monitoring agent:

```
mvn -pl monitoring-agent -am package -DskipTests
```

Then configure the agent JAR path in PowerShell:

```
$env:AGENT_JAR_PATH = Join-Path (Get-Location) "monitoring-agent\target\sentinel-monitoring-agent-0.1.0-jar-with-dependencies.jar"
```

Run the E2E test module:

```
mvn -f e2e-tests/pom.xml test
```

The test module uses the following environment variables:

| Variable               | Description                                           |
| ---------------------- | ----------------------------------------------------- |
| `SENTINEL_BASE_URL`    | Backend base URL; defaults to `http://localhost:8081` |
| `SENTINEL_AGENT_TOKEN` | Agent authentication token                            |
| `AGENT_JAR_PATH`       | Path to the built Monitoring Agent JAR                |

The E2E tests require a running backend and PostgreSQL database.

## Test Reports

Maven Surefire generates test reports in:

```
e2e-tests/target/surefire-reports/
```

The reports include:

- human-readable test execution summary;
- XML test results;
- test counts;
- failures and errors;
- execution details.

The latest local verification completed successfully:

```
Tests run: 2
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

These results represent the local test execution and do not imply that the GitHub Actions workflow has already been verified.

## Frontend Build Verification

Frontend production build:

```
cd frontend
npm run build
```

Manual test scenarios are documented in:

```
docs/qa/TEST_CASES.md
```

## Continuous Integration

A GitHub Actions workflow is configured in:

```
.github/workflows/e2e-tests.yml
```

The workflow is triggered by:

- pushes to the `main` branch;
- pull requests targeting `main`;
- manual workflow execution.

The pipeline performs the following operations:

1. Checks out the repository.
2. Configures Java 21.
3. Builds the Monitoring Agent.
4. Starts PostgreSQL and the backend using Docker Compose.
5. Waits for the backend API to become available.
6. Executes the automated E2E tests.
7. Uploads Maven Surefire reports as GitHub Actions artifacts.

Test reports are configured to be uploaded even when the test step fails.

The workflow is configured in the repository but requires a successful GitHub Actions run to confirm its execution in the remote CI environment.

# 🔐 Security & Configuration

The current MVP uses a shared token to protect metric ingestion requests.

The token is configured through the `AGENT_TOKEN` environment variable.

Example local development configuration:

```
$env:AGENT_TOKEN = "YOUR_AGENT_TOKEN"
$env:SENTINEL_AGENT_TOKEN = "YOUR_AGENT_TOKEN"
```

The backend and Monitoring Agent must use the same token.

Important security limitations:

- User authentication is not implemented.
- Role-based authorization is not implemented.
- Workspace-level access control is not implemented.
- Agent authentication currently uses a shared token.
- Production TLS and secrets management are not configured.

Do not expose development credentials or real server information in the public repository.

# 📋 Current Project Scope

## Implemented

- Server registration and inventory.
- Server update and deletion API.
- CPU and RAM metric collection.
- Monitoring Agent based on Java and OSHI.
- HTTP metric ingestion.
- Token-based ingestion authentication.
- PostgreSQL metric persistence.
- Metric history retrieval.
- Threshold-based WARNING and CRITICAL incidents.
- Incident listing and acknowledgement.
- Automatic resolution of threshold-related incidents.
- React monitoring dashboard.
- CPU and memory utilization charts.
- Automatic dashboard refresh.
- Docker Compose configuration.
- Swagger UI.
- Separate Maven-based E2E test module.
- Automated Monitoring Agent ingestion test.
- Metric persistence verification through the API.
- Invalid authentication token test.
- Maven Surefire test reports.
- GitHub Actions workflow for automated E2E execution.
- GitHub Actions artifact upload configuration.

## Planned Improvements

- User registration and authentication.
- Workspace and role management.
- Configurable monitoring thresholds.
- Automatic OFFLINE detection based on missed heartbeats.
- Incident resolution workflow in the dashboard.
- Email notifications.
- Extended system metrics, including disk and network utilization.
- Additional backend unit and integration tests.
- Frontend browser-based E2E testing with Playwright.
- ReportPortal integration and centralized test reporting.
- Verification and further improvement of the GitHub Actions pipeline.
- AWS deployment.
- Terraform infrastructure configuration.
- Production security hardening.

# ☁️ Deployment

The application currently supports local containerized execution using Docker Compose.

AWS deployment is a separate planned stage.

Before implementing cloud infrastructure, the target AWS architecture, expected costs, database connectivity, secrets management, and resource teardown procedure must be reviewed.

Terraform deployment configuration has not yet been implemented.

See [infrastructure/terraform/README.md](https://infrastructure/terraform/README.md) for deployment planning notes.

# 🌿 Git Workflow

The project uses Git for version control.

The main development branch is:

```
main
```

Feature development and project milestones are tracked through separate commits.

Example commit messages:

```
feat(backend): implement server monitoring and incident management

feat(agent): implement system metrics collector

feat(frontend): implement monitoring dashboard

test(e2e): add automated monitoring agent ingestion tests

ci: add GitHub Actions E2E workflow

docs: update testing and project documentation
```

# 📚 Project Documentation

Additional project documentation is available in the `docs/` directory.

| Document                     | Description                                               |
| ---------------------------- | --------------------------------------------------------- |
| `docs/API.md`                | REST API quick reference                                  |
| `docs/PROJECT_OVERVIEW.md`   | Project scope and implementation status                   |
| `docs/ASSIGNMENT_MAPPING.md` | Mapping between project components and course assignments |
| `docs/qa/TEST_CASES.md`      | Manual QA test scenarios                                  |

# 🎯 Future Vision

The long-term goal is to evolve Sentinel into a more comprehensive infrastructure monitoring platform.

Potential improvements include:

- Extended system metrics collection.
- Configurable alert policies.
- Advanced incident management.
- Notification integrations.
- Historical performance analytics.
- Expanded automated testing and deployment.
- Cloud infrastructure support.
- Improved security and access management.
- Centralized test reporting and quality analytics.

# 👩‍💻 Project

Sentinel — Server Failure Early Detection System

Course project focused on full-stack development, backend engineering, system monitoring, database design, automated testing, and DevOps fundamentals.
