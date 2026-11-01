# Sentinel — Assignment Mapping

## 1. Purpose

This document maps the main project objectives and functional requirements to the current Sentinel implementation.

It is intended to track development progress, identify implemented functionality, document existing limitations, and distinguish completed work from planned improvements.

Only functionality confirmed in the current source code is marked as implemented.

## 2. Implementation Status

The following statuses are used:

| Status                | Description                                                                        |
| --------------------- | ---------------------------------------------------------------------------------- |
| Implemented           | Functionality is present in the current codebase.                                  |
| Partially implemented | Some required functionality is available, but additional work is needed.           |
| Planned               | Functionality is intended for future development but is not currently implemented. |

## 3. Project Objectives Mapping

| No. | Project objective                                                   | Implementation area                           | Status                |
| --: | ------------------------------------------------------------------- | --------------------------------------------- | --------------------- |
|   1 | Analyze the server monitoring domain and define system requirements | Project documentation                         | Implemented           |
|   2 | Design the application architecture                                 | Backend, frontend, monitoring agent, database | Implemented           |
|   3 | Develop a REST API for server management                            | Spring Boot backend                           | Implemented           |
|   4 | Implement persistent storage for monitoring data                    | PostgreSQL, Spring Data JPA                   | Implemented           |
|   5 | Develop a monitoring agent for collecting system metrics            | Java Monitoring Agent, OSHI                   | Implemented           |
|   6 | Implement periodic transmission of monitoring data                  | Monitoring Agent HTTP client                  | Implemented           |
|   7 | Implement CPU and RAM threshold evaluation                          | MonitoringService                             | Implemented           |
|   8 | Implement incident creation and lifecycle management                | Incident entity and MonitoringService         | Partially implemented |
|   9 | Develop a monitoring dashboard                                      | React frontend                                | Implemented           |
|  10 | Visualize historical server metrics                                 | Frontend charts and metrics API               | Implemented           |
|  11 | Implement automatic detection of unavailable servers                | Backend heartbeat monitoring                  | Planned               |
|  12 | Implement user authentication and authorization                     | Backend and frontend                          | Planned               |
|  13 | Implement automated testing of business logic and API operations    | Backend test suite                            | Partially implemented |
|  14 | Prepare containerized development environment                       | Docker, Docker Compose                        | Implemented           |
|  15 | Prepare production deployment infrastructure                        | Terraform and cloud configuration             | Planned               |

## 4. Functional Requirements Mapping

### 4.1. Server Management

| Requirement                                         | Implementation                                   | Status      |
| --------------------------------------------------- | ------------------------------------------------ | ----------- |
| Retrieve the list of registered servers             | `GET /api/servers`                               | Implemented |
| Register a new server                               | `POST /api/servers`                              | Implemented |
| Update server information                           | `PUT /api/servers/{id}`                          | Implemented |
| Delete a server                                     | `DELETE /api/servers/{id}`                       | Implemented |
| Display server status                               | Server status calculation in `MonitoringService` | Implemented |
| Display the latest CPU and RAM values               | Server response DTO and dashboard                | Implemented |
| Automatically detect servers that stopped reporting | Heartbeat timeout monitoring                     | Planned     |

### 4.2. Metrics Collection

| Requirement                                         | Implementation                         | Status      |
| --------------------------------------------------- | -------------------------------------- | ----------- |
| Collect CPU utilization                             | OSHI in Monitoring Agent               | Implemented |
| Collect memory utilization                          | OSHI in Monitoring Agent               | Implemented |
| Collect metrics periodically                        | Agent reporting interval of 15 seconds | Implemented |
| Send metrics to the backend                         | `POST /api/metrics/ingest`             | Implemented |
| Validate agent requests using a shared token        | `X-Agent-Token` header                 | Implemented |
| Store received measurements                         | `MetricRepository` and PostgreSQL      | Implemented |
| Retrieve server metric history                      | `GET /api/servers/{id}/metrics`        | Implemented |
| Collect disk utilization                            | Not implemented                        | Planned     |
| Collect network utilization                         | Not implemented                        | Planned     |
| Collect process-level metrics                       | Not implemented                        | Planned     |
| Automatically reconnect after transmission failures | Agent retry and reconnection logic     | Planned     |

### 4.3. Alert and Incident Management

| Requirement                                                                                         | Implementation                                                      | Status      |
| --------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------- | ----------- |
| Evaluate CPU utilization against thresholds                                                         | `MonitoringService`                                                 | Implemented |
| Evaluate memory utilization against thresholds                                                      | `MonitoringService`                                                 | Implemented |
| Generate WARNING incidents                                                                          | Threshold evaluation                                                | Implemented |
| Generate CRITICAL incidents                                                                         | Threshold evaluation                                                | Implemented |
| Avoid creating another incident when an active incident with the same server and title exists       | Incident repository checks                                          | Implemented |
| Retrieve the incident list                                                                          | `GET /api/incidents`                                                | Implemented |
| Acknowledge an incident                                                                             | `POST /api/incidents/{id}/acknowledge`                              | Implemented |
| Automatically resolve an incident when the corresponding metric returns below its warning threshold | `MonitoringService`                                                 | Implemented |
| Manually resolve an incident through the API                                                        | No dedicated endpoint                                               | Planned     |
| Send email or external notifications                                                                | Not implemented                                                     | Planned     |
| Configure alert thresholds through the user interface                                               | Thresholds are currently configured in backend application settings | Planned     |

### 4.4. Dashboard and User Interface

| Requirement                                                              | Implementation                                         | Status      |
| ------------------------------------------------------------------------ | ------------------------------------------------------ | ----------- |
| Display monitoring overview                                              | React dashboard                                        | Implemented |
| Display registered servers                                               | Server list component                                  | Implemented |
| Display CPU utilization                                                  | Dashboard metrics and charts                           | Implemented |
| Display memory utilization                                               | Dashboard metrics and charts                           | Implemented |
| Display historical metric data                                           | Recharts visualization                                 | Implemented |
| Display recent incidents                                                 | Incident list                                          | Implemented |
| Create a server through the interface                                    | Server creation form                                   | Implemented |
| Acknowledge incidents through the interface                              | Incident action                                        | Implemented |
| Refresh monitoring information periodically                              | Frontend polling every 15 seconds                      | Implemented |
| Provide individual user accounts                                         | Not implemented                                        | Planned     |
| Provide role-based access to system resources                            | Not implemented                                        | Planned     |
| Provide a complete server details page with advanced monitoring controls | Current interface is focused on the overview dashboard | Planned     |

## 5. Non-Functional Requirements Mapping

| Requirement                             | Current implementation                        | Status                |
| --------------------------------------- | --------------------------------------------- | --------------------- |
| Modular application structure           | Separate backend, frontend, and agent modules | Implemented           |
| Relational persistent storage           | PostgreSQL                                    | Implemented           |
| HTTP-based communication                | REST API                                      | Implemented           |
| Containerized local environment         | Docker Compose                                | Implemented           |
| API discoverability and documentation   | Springdoc OpenAPI and Swagger UI              | Implemented           |
| Automated backend testing               | Basic Spring Boot context test                | Partially implemented |
| Authentication of application users     | Not implemented                               | Planned               |
| Role-based authorization                | Not implemented                               | Planned               |
| Secure production configuration         | Development configuration only                | Planned               |
| Horizontal scalability                  | No dedicated scaling configuration            | Planned               |
| Production monitoring and observability | Not configured                                | Planned               |
| Automated CI/CD pipeline                | Not implemented                               | Planned               |
| Cloud deployment                        | Not implemented                               | Planned               |

## 6. Technology Requirements Mapping

| Technology             | Intended purpose                             | Current status        |
| ---------------------- | -------------------------------------------- | --------------------- |
| Java 21                | Backend and monitoring agent                 | Implemented           |
| Spring Boot 3.4.5      | Backend application framework                | Implemented           |
| Spring Data JPA        | Database access                              | Implemented           |
| Hibernate              | Object-relational mapping                    | Implemented           |
| React 18               | Frontend interface                           | Implemented           |
| TypeScript             | Frontend development                         | Implemented           |
| Vite 6                 | Frontend build tooling                       | Implemented           |
| Axios                  | HTTP communication                           | Implemented           |
| Recharts               | Metrics visualization                        | Implemented           |
| OSHI 6.6.5             | System information collection                | Implemented           |
| PostgreSQL 16          | Persistent data storage                      | Implemented           |
| Docker                 | Application containerization                 | Implemented           |
| Docker Compose         | Local multi-container environment            | Implemented           |
| Springdoc OpenAPI      | API documentation                            | Implemented           |
| JUnit 5                | Backend testing                              | Partially implemented |
| Mockito                | Backend component testing                    | Partially implemented |
| Playwright             | End-to-end frontend testing                  | Planned               |
| Terraform              | Infrastructure provisioning                  | Planned               |
| AWS                    | Cloud deployment                             | Planned               |
| Prometheus and Grafana | Application monitoring and visualization     | Planned               |
| Redis                  | Caching or asynchronous processing           | Planned               |
| AI/LLM API             | Potential intelligent analysis functionality | Planned               |

## 7. Current Implementation Limitations

The current version is an MVP focused on the core server monitoring workflow.

The following limitations remain:

1. User authentication and authorization are not implemented.
2. The application does not automatically mark a server as offline when its heartbeat becomes outdated.
3. The monitoring agent collects CPU and RAM utilization only.
4. The agent does not currently implement automatic retry or reconnection.
5. The backend uses a shared development token for agent requests.
6. Server and incident management endpoints are not protected by user authentication.
7. Metric history retrieval is limited to the latest 100 records.
8. Advanced filtering and pagination are not implemented.
9. Incident notifications through email or external services are not implemented.
10. The automated test suite requires additional unit and integration tests.
11. Production deployment and cloud infrastructure are not configured.
12. The current frontend uses static demonstration identity and workspace labels rather than authenticated user data.

## 8. Further Development Priorities

The following improvements are recommended for subsequent development stages:

| Priority | Improvement                             | Expected result                                   |
| -------- | --------------------------------------- | ------------------------------------------------- |
| High     | Implement user authentication           | Secure access to the application                  |
| High     | Add role-based authorization            | Separate user and administrator permissions       |
| High     | Implement offline server detection      | Identify servers that stop sending metrics        |
| High     | Expand automated testing                | Improve reliability of core monitoring operations |
| Medium   | Add agent retry and reconnection        | Improve resilience to temporary network failures  |
| Medium   | Add disk and network monitoring         | Expand collected system information               |
| Medium   | Implement configurable alert thresholds | Allow monitoring rules to be adjusted             |
| Medium   | Add notification delivery               | Inform users about critical incidents             |
| Medium   | Add metric filtering and pagination     | Improve handling of historical data               |
| Low      | Introduce Prometheus and Grafana        | Expand operational observability                  |
| Low      | Prepare Terraform configuration         | Support repeatable infrastructure provisioning    |
| Low      | Evaluate AI/LLM integration             | Explore additional analysis capabilities          |

## 9. Conclusion

The current Sentinel implementation covers the fundamental monitoring workflow: server registration, periodic collection of CPU and memory metrics, backend processing, threshold-based incident generation, persistent storage, and dashboard visualization.

The project has a working modular foundation and a containerized development environment.

Authentication, automatic offline detection, extended monitoring capabilities, production deployment, and advanced testing remain areas for future development.

This mapping reflects the current implementation status and should be updated whenever new functionality is added or existing functionality changes.
