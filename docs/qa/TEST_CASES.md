# Sentinel — Test Cases

## 1. Purpose

This document describes the test scenarios for the Sentinel server monitoring application.

The purpose of testing is to verify that the main application components work correctly, including:

- server management;
- monitoring agent operation;
- CPU and RAM metrics collection;
- metric transmission and storage;
- threshold-based incident generation;
- incident lifecycle management;
- REST API functionality;
- frontend dashboard behavior;
- Docker-based application startup.

The document also records the current verification status of each test scenario.

## 2. Testing Environment

The following environment is used for local development and testing:

| Component        | Version or configuration |
| ---------------- | ------------------------ |
| Operating system | Windows 11               |
| Java             | 21.0.13                  |
| Maven            | 3.10.0                   |
| Node.js          | 22.22.2                  |
| npm              | 10.9.7                   |
| Docker           | 29.5.3                   |
| Docker Compose   | 5.1.4                    |
| PostgreSQL       | 16.15                    |
| Spring Boot      | 3.4.5                    |
| React            | 18.3.1                   |
| TypeScript       | 5.7.3                    |
| Vite             | 6.4.5                    |

### 2.1. Application Services

The local Docker Compose environment contains the following services:

| Service     | Host address                            |
| ----------- | --------------------------------------- |
| Frontend    | `http://localhost:5173`                 |
| Backend API | `http://localhost:8081/api`             |
| Swagger UI  | `http://localhost:8081/swagger-ui.html` |
| PostgreSQL  | `localhost:5434`                        |

### 2.2. Test Server

The monitoring agent was manually tested using the following registered server:

| Parameter                | Value                                  |
| ------------------------ | -------------------------------------- |
| Server name              | `MOZGGHOST`                            |
| Host                     | `192.168.1.112`                        |
| Environment              | `development`                          |
| Server UUID              | `dcea8d6b-25f9-41d2-ba27-322c0db6750b` |
| Agent reporting interval | 15 seconds                             |

## 3. Test Status Definitions

The following statuses are used:

| Status       | Meaning                                                                                        |
| ------------ | ---------------------------------------------------------------------------------------------- |
| PASS         | The test was executed and the expected result was observed.                                    |
| FAIL         | The test was executed and the expected result was not observed.                                |
| NOT EXECUTED | The test has been defined but has not yet been performed.                                      |
| BLOCKED      | The test cannot currently be performed because a required component or feature is unavailable. |

A test scenario is not considered passed merely because the corresponding functionality exists in the source code.

## 4. Build and Environment Verification

| ID     | Test scenario                              | Expected result                                                        | Current status |
| ------ | ------------------------------------------ | ---------------------------------------------------------------------- | -------------- |
| ENV-01 | Build the backend using Maven              | Backend compilation and packaging complete successfully                | PASS           |
| ENV-02 | Build the monitoring agent                 | Agent JAR is generated successfully                                    | PASS           |
| ENV-03 | Build the frontend                         | TypeScript compilation and Vite production build complete successfully | PASS           |
| ENV-04 | Start the application using Docker Compose | Backend, frontend, and database containers start successfully          | PASS           |
| ENV-05 | Verify PostgreSQL connection               | Backend can access the PostgreSQL database                             | PASS           |
| ENV-06 | Run the backend automated test suite       | Available automated tests complete successfully                        | PASS           |

**Verification notes:**

- Backend packaging was completed using Maven with the required database environment variables.
- The monitoring agent was packaged as a standalone JAR.
- Frontend production build completed successfully.
- Docker Compose services were observed running, and the PostgreSQL container reported a healthy state.
- The backend test suite currently contains one Spring Boot application context-load test. Its successful execution does not establish full business-logic coverage.

## 5. Server Management Tests

| ID     | Test scenario                              | Expected result                                          | Current status |
| ------ | ------------------------------------------ | -------------------------------------------------------- | -------------- |
| SRV-01 | Retrieve the registered server list        | API returns the available server records                 | PASS           |
| SRV-02 | Create a server using valid data           | A new server record is created                           | NOT EXECUTED   |
| SRV-03 | Create a server with invalid data          | API rejects the request and returns an appropriate error | NOT EXECUTED   |
| SRV-04 | Update an existing server                  | Server information is updated                            | NOT EXECUTED   |
| SRV-05 | Update a non-existent server               | API returns a not-found response                         | NOT EXECUTED   |
| SRV-06 | Delete an existing server                  | Server deletion is processed successfully                | NOT EXECUTED   |
| SRV-07 | Retrieve metrics for an existing server    | API returns the server's metric history                  | NOT EXECUTED   |
| SRV-08 | Retrieve metrics for a non-existent server | API returns an appropriate error                         | NOT EXECUTED   |

### 5.1. SRV-01 — Retrieve Server List

**Objective:** Verify that the backend returns registered servers.

**Preconditions:**

- Backend is running.
- PostgreSQL is available.
- At least one server record exists.

**Steps:**

1. Send an HTTP GET request to `/api/servers`.
2. Inspect the response status.
3. Inspect the returned server data.

**Expected result:**

- The API returns a successful response.
- The response contains registered server records.
- Each server includes its identifier and available monitoring information.

**Observed result:** The endpoint was manually checked and returned the registered server `MOZGGHOST`.

**Status:** PASS.

## 6. Monitoring Agent Tests

| ID     | Test scenario                                   | Expected result                           | Current status |
| ------ | ----------------------------------------------- | ----------------------------------------- | -------------- |
| AGT-01 | Start the agent with a valid server UUID        | Agent starts successfully                 | PASS           |
| AGT-02 | Collect CPU utilization                         | Agent obtains a CPU utilization value     | PASS           |
| AGT-03 | Collect memory utilization                      | Agent obtains a memory utilization value  | PASS           |
| AGT-04 | Send metrics to the backend                     | Backend accepts a valid metric request    | PASS           |
| AGT-05 | Send a request with an invalid agent token      | Backend rejects the request               | NOT EXECUTED   |
| AGT-06 | Send metrics for an unknown server UUID         | Backend rejects the request               | NOT EXECUTED   |
| AGT-07 | Start the agent with an invalid server UUID     | Agent reports a configuration error       | NOT EXECUTED   |
| AGT-08 | Simulate a temporary backend connection failure | Agent retries or reconnects automatically | BLOCKED        |

### 6.1. AGT-01 to AGT-04 — Agent Operation

**Objective:** Verify that the monitoring agent collects and transmits system metrics.

**Preconditions:**

- Backend and database are running.
- A registered server exists.
- The agent has a valid server UUID and token.

**Steps:**

1. Configure the agent environment variables.
2. Start the standalone agent JAR.
3. Observe the agent console output.
4. Check whether the backend accepts the metric requests.
5. Inspect the server monitoring information.

**Expected result:**

- The agent starts successfully.
- CPU and memory utilization values are collected.
- Metric requests are sent periodically.
- The backend accepts valid requests.

**Observed result:** The agent successfully collected CPU and RAM measurements and received HTTP `202 Accepted` responses from the backend.

**Status:** PASS.

## 7. Metrics and Data Processing Tests

| ID     | Test scenario                                            | Expected result                                 | Current status |
| ------ | -------------------------------------------------------- | ----------------------------------------------- | -------------- |
| MET-01 | Submit valid CPU and RAM measurements                    | Measurements are accepted and stored            | PASS           |
| MET-02 | Submit measurements with a missing required field        | Request validation rejects invalid data         | NOT EXECUTED   |
| MET-03 | Submit CPU values outside the expected range             | Invalid values are rejected                     | NOT EXECUTED   |
| MET-04 | Submit RAM values outside the expected range             | Invalid values are rejected                     | NOT EXECUTED   |
| MET-05 | Retrieve metric history                                  | API returns stored measurements                 | NOT EXECUTED   |
| MET-06 | Retrieve metric history containing more than 100 records | API returns no more than the latest 100 records | NOT EXECUTED   |
| MET-07 | Verify metric timestamps                                 | Timestamps are stored and returned correctly    | NOT EXECUTED   |

**Verification note:** Successful agent ingestion was observed. Dedicated boundary-value and invalid-input tests have not yet been performed.

## 8. Alert and Incident Tests

The current default alert thresholds are:

| Metric          | WARNING | CRITICAL |
| --------------- | ------: | -------: |
| CPU utilization |     75% |      90% |
| RAM utilization |     80% |      92% |

| ID     | Test scenario                                | Expected result                                                       | Current status |
| ------ | -------------------------------------------- | --------------------------------------------------------------------- | -------------- |
| INC-01 | CPU reaches the warning threshold            | A CPU WARNING incident is created                                     | NOT EXECUTED   |
| INC-02 | CPU reaches the critical threshold           | A CPU CRITICAL incident is created                                    | NOT EXECUTED   |
| INC-03 | RAM reaches the warning threshold            | A memory WARNING incident is created                                  | NOT EXECUTED   |
| INC-04 | RAM reaches the critical threshold           | A memory CRITICAL incident is created                                 | NOT EXECUTED   |
| INC-05 | A metric remains above its threshold         | Another active incident with the same server and title is not created | NOT EXECUTED   |
| INC-06 | CPU returns below its warning threshold      | The corresponding active CPU incident is resolved                     | NOT EXECUTED   |
| INC-07 | RAM returns below its warning threshold      | The corresponding active memory incident is resolved                  | NOT EXECUTED   |
| INC-08 | Retrieve incidents                           | API returns the incident list                                         | PASS           |
| INC-09 | Acknowledge an open incident                 | Incident status changes to ACKNOWLEDGED                               | NOT EXECUTED   |
| INC-10 | Acknowledge an already acknowledged incident | Existing status remains unchanged                                     | NOT EXECUTED   |

### 8.1. INC-08 — Retrieve Incident List

**Objective:** Verify that the backend returns stored incidents.

**Preconditions:**

- Backend is running.
- Incident records exist in the database.

**Steps:**

1. Send an HTTP GET request to `/api/incidents`.
2. Inspect the response.
3. Verify that incident information is returned.

**Expected result:**

- The API returns a successful response.
- The response contains available incident records and their associated information.

**Observed result:** The endpoint was manually checked and returned incident records associated with the registered server.

**Status:** PASS.

## 9. Frontend Tests

| ID    | Test scenario                                 | Expected result                                     | Current status |
| ----- | --------------------------------------------- | --------------------------------------------------- | -------------- |
| UI-01 | Open the dashboard                            | Dashboard loads successfully                        | PASS           |
| UI-02 | Display registered servers                    | Server information is visible                       | PASS           |
| UI-03 | Display CPU and RAM values                    | Current metric values are displayed                 | PASS           |
| UI-04 | Display metric charts                         | Historical measurements are visualized              | PASS           |
| UI-05 | Display recent incidents                      | Incident records are visible                        | PASS           |
| UI-06 | Refresh monitoring information                | Dashboard requests updated information periodically | PASS           |
| UI-07 | Create a server through the interface         | New server appears after successful creation        | NOT EXECUTED   |
| UI-08 | Acknowledge an incident through the interface | Incident status is updated                          | NOT EXECUTED   |
| UI-09 | Backend becomes unavailable                   | Frontend displays an appropriate error state        | NOT EXECUTED   |
| UI-10 | No servers are registered                     | Dashboard displays an appropriate empty state       | NOT EXECUTED   |

**Verification notes:**

The dashboard was opened and inspected during manual verification. Server information, monitoring values, metric charts, and incident information were displayed.

The frontend code implements periodic polling with a 15-second interval.

Dedicated automated browser tests have not been implemented.

## 10. Docker and Integration Tests

| ID     | Test scenario                                   | Expected result                                                            | Current status |
| ------ | ----------------------------------------------- | -------------------------------------------------------------------------- | -------------- |
| INT-01 | Start all services using Docker Compose         | All configured containers start                                            | PASS           |
| INT-02 | Verify backend-to-database communication        | Backend successfully accesses PostgreSQL                                   | PASS           |
| INT-03 | Verify frontend-to-backend communication        | Frontend retrieves monitoring information                                  | PASS           |
| INT-04 | Send agent metrics to the containerized backend | Backend accepts agent requests                                             | PASS           |
| INT-05 | Restart application containers                  | Services start again and persistent database data remains available        | NOT EXECUTED   |
| INT-06 | Stop PostgreSQL while backend is running        | Backend handles database unavailability without unexpected data corruption | NOT EXECUTED   |

## 11. Security Tests

| ID     | Test scenario                                                                 | Expected result                                                           | Current status |
| ------ | ----------------------------------------------------------------------------- | ------------------------------------------------------------------------- | -------------- |
| SEC-01 | Submit a metric request without an agent token                                | Backend rejects the request                                               | NOT EXECUTED   |
| SEC-02 | Submit a metric request with an incorrect token                               | Backend rejects the request                                               | NOT EXECUTED   |
| SEC-03 | Access server management endpoints without user authentication                | Current API permits access because user authentication is not implemented | PASS           |
| SEC-04 | Verify that development secrets are not hardcoded in production configuration | Production secret management is configured                                | BLOCKED        |

**Security note:** SEC-03 documents an existing limitation, not a successful security control. The current server and incident management endpoints do not require user authentication. The shared agent token provides only basic request verification and is not a substitute for a complete authentication and authorization system.

## 12. Known Testing Limitations

The current testing process has several limitations:

1. The automated backend test suite contains only a basic application context-load test.
2. Business logic in `MonitoringService` requires dedicated unit tests.
3. REST controllers require integration tests covering valid and invalid requests.
4. Threshold boundary conditions require explicit verification.
5. Incident lifecycle transitions require additional tests.
6. Frontend interactions require automated browser testing.
7. Agent behavior during network interruptions requires further verification.
8. Authentication and authorization tests cannot be completed until those features are implemented.
9. Automatic offline detection cannot be tested until heartbeat timeout processing is implemented.
10. Performance and load testing have not been performed.

## 13. Recommended Testing Improvements

The following testing improvements are recommended:

| Area                          | Recommended tool or approach                  |
| ----------------------------- | --------------------------------------------- |
| Backend unit tests            | JUnit 5 and Mockito                           |
| REST API integration tests    | Spring Boot Test                              |
| Database integration tests    | PostgreSQL test environment or Testcontainers |
| Frontend component tests      | React Testing Library                         |
| End-to-end tests              | Playwright                                    |
| API contract verification     | OpenAPI specification                         |
| Performance testing           | k6 or Apache JMeter                           |
| Container integration testing | Docker Compose-based test environment         |

## 14. Conclusion

The current Sentinel MVP has been built and manually verified at the basic integration level.

Successful checks include backend, frontend, and monitoring agent builds; Docker Compose startup; PostgreSQL connectivity; manual retrieval of server and incident information; and successful metric transmission from the monitoring agent.

Further testing is required to verify threshold boundary conditions, invalid requests, complete incident lifecycle behavior, error handling, and application security.

All test results should be updated after the corresponding scenarios have actually been executed.
