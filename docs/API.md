# Sentinel REST API Documentation

## 1. Overview

Sentinel provides a REST API for managing monitored servers, receiving system metrics, retrieving metric history, and managing monitoring incidents.

The backend is implemented using Java 21 and Spring Boot 3.4.5. Data is persisted in PostgreSQL 16.

### 1.1 Base URLs

When running the application through Docker Compose:

```text
http://localhost:8081/api
```

When running the backend directly with its default Spring Boot configuration:

```text
http://localhost:8080/api
```

The frontend Docker Compose configuration uses the first address.

### 1.2 API Documentation Interface

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8081/v3/api-docs
```

These addresses assume the Docker Compose configuration is running.

### 1.3 Data Format

- Request format: JSON (`application/json`).
- Response format: JSON, except endpoints returning an empty response.
- Date and time values: ISO 8601 timestamps.
- Server and incident identifiers: UUID strings.
- Metric values: percentages from 0 to 100.

---

## 2. API Endpoints

| Method | Endpoint                          | Description                     |
| ------ | --------------------------------- | ------------------------------- |
| GET    | `/api/servers`                    | Retrieve all registered servers |
| POST   | `/api/servers`                    | Register a new server           |
| PUT    | `/api/servers/{id}`               | Update server information       |
| DELETE | `/api/servers/{id}`               | Delete a server                 |
| GET    | `/api/servers/{id}/metrics`       | Retrieve metric history         |
| POST   | `/api/metrics/ingest`             | Submit monitoring metrics       |
| GET    | `/api/incidents`                  | Retrieve monitoring incidents   |
| POST   | `/api/incidents/{id}/acknowledge` | Acknowledge an incident         |

---

## 3. Server Management

### 3.1 Get All Servers

Retrieves the list of registered servers.

**Request**

```http
GET /api/servers
```

**Example**

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8081/api/servers" `
    -Method Get
```

**Response example**

```json
[
  {
    "id": "dcea8d6b-25f9-41d2-ba27-322c0db6750b",
    "name": "MOZGGHOST",
    "host": "192.168.1.112",
    "environment": "development",
    "status": "HEALTHY",
    "cpuPercent": 12.5,
    "memoryPercent": 78.2,
    "lastHeartbeat": "2026-10-31T10:00:00Z"
  }
]
```

The response contains the following fields:

| Field           | Type      | Description                         |
| --------------- | --------- | ----------------------------------- |
| `id`            | UUID      | Unique server identifier            |
| `name`          | String    | Server name                         |
| `host`          | String    | Server host address                 |
| `environment`   | String    | Deployment environment              |
| `status`        | Enum      | Current server status               |
| `cpuPercent`    | Number    | Latest CPU utilization              |
| `memoryPercent` | Number    | Latest RAM utilization              |
| `lastHeartbeat` | Timestamp | Time of the latest received metrics |

Possible server statuses:

- `HEALTHY`
- `WARNING`
- `CRITICAL`
- `OFFLINE` (defined in the status model; automatic offline detection is not implemented yet).

### 3.2 Register a Server

Registers a new server in the monitoring system.

**Request**

```http
POST /api/servers
Content-Type: application/json
```

**Request body**

```json
{
  "name": "Production Server",
  "host": "192.168.1.100",
  "environment": "production"
}
```

**PowerShell example**

```powershell
$body = @{
    name = "Production Server"
    host = "192.168.1.100"
    environment = "production"
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8081/api/servers" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

**Validation rules**

- `name` must not be blank.
- `host` must not be blank.
- `environment` must not be blank.

The backend validates these fields using Jakarta Bean Validation.

**Response**

Returns the created server as a `ServerResponse` object.

### 3.3 Update a Server

Updates the name, host address, and environment of an existing server.

**Request**

```http
PUT /api/servers/{id}
Content-Type: application/json
```

**Request body**

```json
{
  "name": "Updated Production Server",
  "host": "192.168.1.105",
  "environment": "production"
}
```

**PowerShell example**

```powershell
$serverId = "dcea8d6b-25f9-41d2-ba27-322c0db6750b"

$body = @{
    name = "Updated Production Server"
    host = "192.168.1.105"
    environment = "production"
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8081/api/servers/$serverId" `
    -Method Put `
    -ContentType "application/json" `
    -Body $body
```

**Validation rules**

All three fields are required and must not be blank.

**Response**

Returns the updated server.

**Possible errors**

- `404 Not Found` — server does not exist.
- Validation error — one or more required fields are invalid.

### 3.4 Delete a Server

Deletes a server record.

**Request**

```http
DELETE /api/servers/{id}
```

**PowerShell example**

```powershell
$serverId = "dcea8d6b-25f9-41d2-ba27-322c0db6750b"

Invoke-RestMethod `
    -Uri "http://localhost:8081/api/servers/$serverId" `
    -Method Delete
```

**Possible errors**

- `404 Not Found` — server does not exist.

The endpoint does not return a JSON response body.

The database relationship and deletion behavior for associated metrics and incidents depend on the entity mapping and database constraints.

---

## 4. Metrics API

### 4.1 Submit Monitoring Metrics

The monitoring agent sends CPU and memory utilization measurements to the backend.

**Request**

```http
POST /api/metrics/ingest
Content-Type: application/json
X-Agent-Token: <AGENT_TOKEN>
```

**Request body**

```json
{
  "serverId": "dcea8d6b-25f9-41d2-ba27-322c0db6750b",
  "cpuPercent": 42.8,
  "memoryPercent": 67.1,
  "recordedAt": "2026-10-31T10:00:00Z"
}
```

**PowerShell example**

```powershell
$body = @{
    serverId = "dcea8d6b-25f9-41d2-ba27-322c0db6750b"
    cpuPercent = 42.8
    memoryPercent = 67.1
    recordedAt = (Get-Date).ToUniversalTime().ToString("o")
} | ConvertTo-Json

$headers = @{
    "X-Agent-Token" = "local-dev-token"
}

Invoke-RestMethod `
    -Uri "http://localhost:8081/api/metrics/ingest" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $body
```

**Request fields**

| Field           | Type      | Validation | Description                        |
| --------------- | --------- | ---------- | ---------------------------------- |
| `serverId`      | UUID      | Required   | Identifier of the monitored server |
| `cpuPercent`    | Number    | 0–100      | CPU utilization percentage         |
| `memoryPercent` | Number    | 0–100      | RAM utilization percentage         |
| `recordedAt`    | Timestamp | Required   | Measurement timestamp              |

**Authentication**

The endpoint requires the `X-Agent-Token` HTTP header.

The supplied token is compared with the backend configuration property:

```yaml
sentinel:
  agent-token: ${AGENT_TOKEN:local-dev-token}
```

The default development token is `local-dev-token`.

**Responses**

| HTTP status        | Description                             |
| ------------------ | --------------------------------------- |
| `202 Accepted`     | Metrics were accepted for processing    |
| `401 Unauthorized` | Agent token is missing or invalid       |
| `404 Not Found`    | The specified server does not exist     |
| `400 Bad Request`  | Request body or field validation failed |

The endpoint uses synchronous service processing before returning the accepted response.

---

### 4.2 Retrieve Server Metric History

Returns up to the latest 100 metric records for a specified server.

**Request**

```http
GET /api/servers/{id}/metrics
```

**PowerShell example**

```powershell
$serverId = "dcea8d6b-25f9-41d2-ba27-322c0db6750b"

Invoke-RestMethod `
    -Uri "http://localhost:8081/api/servers/$serverId/metrics" `
    -Method Get
```

**Response example**

```json
[
  {
    "recordedAt": "2026-10-31T09:45:00Z",
    "cpuPercent": 15.2,
    "memoryPercent": 76.4
  },
  {
    "recordedAt": "2026-10-31T10:00:00Z",
    "cpuPercent": 42.8,
    "memoryPercent": 67.1
  }
]
```

**Response fields**

| Field           | Type      | Description      |
| --------------- | --------- | ---------------- |
| `recordedAt`    | Timestamp | Measurement time |
| `cpuPercent`    | Number    | CPU utilization  |
| `memoryPercent` | Number    | RAM utilization  |

The backend selects the latest 100 records and returns them sorted by measurement time in ascending order.

**Possible errors**

- `404 Not Found` — server does not exist.

---

## 5. Incident Management

### 5.1 Retrieve Incidents

Returns monitoring incidents ordered by creation time, newest first.

**Request**

```http
GET /api/incidents
```

**PowerShell example**

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8081/api/incidents" `
    -Method Get
```

**Response example**

```json
[
  {
    "id": "a12f6d80-6e5c-4b62-a99d-78e8f8a11234",
    "serverId": "dcea8d6b-25f9-41d2-ba27-322c0db6750b",
    "serverName": "MOZGGHOST",
    "severity": "WARNING",
    "status": "OPEN",
    "title": "Elevated memory usage",
    "description": "Memory usage reached 85.0%.",
    "createdAt": "2026-10-31T10:00:00Z",
    "acknowledgedAt": null
  }
]
```

**Response fields**

| Field            | Type              | Description               |
| ---------------- | ----------------- | ------------------------- |
| `id`             | UUID              | Incident identifier       |
| `serverId`       | UUID              | Related server identifier |
| `serverName`     | String            | Related server name       |
| `severity`       | Enum              | Incident severity         |
| `status`         | Enum              | Current incident status   |
| `title`          | String            | Incident title            |
| `description`    | String            | Incident details          |
| `createdAt`      | Timestamp         | Creation time             |
| `acknowledgedAt` | Timestamp or null | Acknowledgement time      |

Possible incident statuses:

- `OPEN`
- `ACKNOWLEDGED`
- `RESOLVED`

Possible severity values include `INFO`, `WARNING`, and `CRITICAL`, as defined by the domain model.

### 5.2 Acknowledge an Incident

Changes an incident from `OPEN` to `ACKNOWLEDGED`.

**Request**

```http
POST /api/incidents/{id}/acknowledge
```

**PowerShell example**

```powershell
$incidentId = "a12f6d80-6e5c-4b62-a99d-78e8f8a11234"

Invoke-RestMethod `
    -Uri "http://localhost:8081/api/incidents/$incidentId/acknowledge" `
    -Method Post
```

**Response**

Returns the updated incident object.

**Possible errors**

- `404 Not Found` — incident does not exist.

Acknowledgement does not resolve an incident. Resolution is performed automatically by the monitoring service when the corresponding metric falls below its warning threshold.

There is currently no separate public API endpoint for manually resolving an incident.

---

## 6. Alert Threshold Configuration

The monitoring service evaluates CPU and RAM utilization independently.

Default configuration:

| Metric | WARNING | CRITICAL |
| ------ | ------: | -------: |
| CPU    |     75% |      90% |
| RAM    |     80% |      92% |

The thresholds are configured in `application.yml`:

```yaml
sentinel:
  alert:
    cpu-warning: 75
    cpu-critical: 90
    memory-warning: 80
    memory-critical: 92
```

The application can override these values through its configuration.

### 6.1 Server Status Evaluation

The overall server status is calculated as follows:

| Condition                                    | Server status |
| -------------------------------------------- | ------------- |
| CPU or RAM reaches CRITICAL                  | `CRITICAL`    |
| CPU or RAM reaches WARNING, without CRITICAL | `WARNING`     |
| Both CPU and RAM are below WARNING           | `HEALTHY`     |

### 6.2 Incident Creation

The monitoring service creates separate incidents for CPU and RAM threshold violations.

Examples:

- `Elevated CPU usage`
- `High CPU usage`
- `Elevated memory usage`
- `High memory usage`

An active incident with the same server and title is not duplicated by the normal service-level check.

### 6.3 Automatic Incident Resolution

When CPU usage drops below 75%, active CPU-related incidents are resolved.

When RAM usage drops below 80%, active RAM-related incidents are resolved.

This behavior is implemented in `MonitoringService`.

These alerts indicate abnormal resource utilization. They are not machine-learning predictions or guarantees of an impending server failure.

---

## 7. Authentication and Security

The current MVP implements shared-token authentication only for metric ingestion.

| Resource           | Authentication         |
| ------------------ | ---------------------- |
| Server endpoints   | Not implemented        |
| Incident endpoints | Not implemented        |
| Metric ingestion   | Shared `X-Agent-Token` |

The development token is configured through:

```text
AGENT_TOKEN=local-dev-token
```

For non-development environments, configure a unique secret through environment variables.

The current implementation does not yet include:

- user authentication;
- role-based authorization;
- workspace isolation;
- token rotation;
- per-agent credentials;
- TLS configuration.

Do not expose development credentials in a public deployment.

---

## 8. Error Handling and Validation

The API uses request DTO validation through Jakarta Bean Validation.

Examples of validation rules:

- Server name cannot be blank.
- Server host cannot be blank.
- Server environment cannot be blank.
- Metric server identifier is required.
- Metric timestamp is required.
- CPU and RAM percentages must be between 0 and 100.

The service layer uses HTTP `404 Not Found` when a requested server or incident does not exist.

The metric ingestion endpoint returns `401 Unauthorized` when the agent token is missing or invalid.

The exact JSON structure of validation and error responses depends on Spring Boot's exception handling configuration.

---

## 9. Running the API Locally

### 9.1 Start the Application

From the project root:

```powershell
docker compose up -d --build
```

Check container status:

```powershell
docker compose ps
```

### 9.2 Verify the API

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8081/api/servers" `
    -Method Get
```

### 9.3 Open Swagger UI

```text
http://localhost:8081/swagger-ui.html
```

### 9.4 Stop the Application

```powershell
docker compose down
```

This command preserves the named PostgreSQL volume.

---

## 10. API Limitations

The current Sentinel MVP has the following API limitations:

- No pagination for server and incident lists.
- Metric history is limited to the latest 100 records.
- No metric filtering by date range.
- No endpoint for manually resolving incidents.
- No endpoint for configuring alert thresholds.
- No automatic OFFLINE detection based on missed heartbeats.
- No user authentication or role-based authorization.
- No rate limiting for metric ingestion.
- No formal versioning prefix such as `/api/v1`.

These capabilities may be introduced in future iterations.

---

## 11. Related Project Documentation

- [Main project README](../README.md)
- [Project overview](PROJECT_OVERVIEW.md)
- [Course assignment mapping](ASSIGNMENT_MAPPING.md)
- [Manual QA test cases](qa/TEST_CASES.md)
- [Terraform deployment notes](../infrastructure/terraform/README.md)
