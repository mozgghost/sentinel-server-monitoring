# Sentinel — Terraform Infrastructure

## 1. Purpose

This directory is intended to contain Terraform configuration files for managing the cloud infrastructure of the Sentinel application.

Terraform is planned as an Infrastructure as Code (IaC) tool for defining, provisioning, and maintaining cloud resources through version-controlled configuration.

The intended infrastructure provider is Amazon Web Services (AWS).

**Current status:** Terraform configuration and AWS deployment have not yet been implemented. This directory currently contains documentation describing the planned infrastructure direction.

## 2. Infrastructure Goals

The planned infrastructure should support the following objectives:

- provide an environment for hosting the Sentinel backend;
- provide a deployment environment for the frontend;
- configure managed or self-hosted PostgreSQL database infrastructure;
- enable secure communication between application components;
- centralize environment configuration and secrets management;
- support application logging and monitoring;
- make infrastructure provisioning repeatable;
- separate development and production environments.

## 3. Planned Architecture

The following AWS services are potential infrastructure components for a future deployment.

| Component        | Potential AWS service           | Purpose                                      |
| ---------------- | ------------------------------- | -------------------------------------------- |
| Backend          | Amazon ECS or Amazon EC2        | Run the Spring Boot application              |
| Frontend         | Amazon S3 and Amazon CloudFront | Host and distribute the frontend application |
| Database         | Amazon RDS for PostgreSQL       | Persistent application data storage          |
| Container images | Amazon ECR                      | Store Docker images                          |
| Secrets          | AWS Secrets Manager             | Store database credentials and agent tokens  |
| Networking       | Amazon VPC                      | Isolate and connect infrastructure resources |
| Access control   | AWS IAM                         | Manage permissions for AWS resources         |
| Monitoring       | Amazon CloudWatch               | Collect infrastructure logs and metrics      |

The final service selection depends on deployment requirements, expected workload, security considerations, and operating costs.

No AWS resources are currently provisioned by this project.

## 4. Planned Deployment Structure

The intended deployment flow is:

1. Build the Sentinel frontend and backend.
2. Build Docker images for the application components.
3. Publish container images to a container registry where required.
4. Provision the required cloud infrastructure using Terraform.
5. Configure application environment variables and secrets.
6. Deploy the backend application.
7. Configure the frontend hosting environment.
8. Connect the backend to the PostgreSQL database.
9. Configure networking, access permissions, and monitoring.
10. Verify application availability and connectivity.

The Monitoring Agent is intended to run separately on monitored machines and send metrics to the publicly reachable backend API through a secured connection.

## 5. Expected Terraform Directory Structure

When infrastructure implementation begins, the directory may follow this structure:

```text
infrastructure/
└── terraform/
    ├── README.md
    ├── versions.tf
    ├── providers.tf
    ├── variables.tf
    ├── outputs.tf
    ├── main.tf
    ├── terraform.tfvars.example
    │
    └── modules/
        ├── networking/
        ├── database/
        ├── backend/
        ├── frontend/
        └── monitoring/
```

This is a proposed structure. These Terraform configuration files and modules have not yet been created.

## 6. Terraform Configuration Responsibilities

The planned configuration files will have the following responsibilities:

| File                       | Intended responsibility                         |
| -------------------------- | ----------------------------------------------- |
| `versions.tf`              | Define required Terraform and provider versions |
| `providers.tf`             | Configure the AWS provider                      |
| `variables.tf`             | Declare configurable infrastructure parameters  |
| `outputs.tf`               | Export useful deployment information            |
| `main.tf`                  | Define and connect infrastructure resources     |
| `terraform.tfvars.example` | Provide example variable values without secrets |
| `modules/`                 | Organize reusable infrastructure components     |

Terraform state should be stored securely. For a shared production environment, a remote state backend with appropriate access control and state locking should be configured.

## 7. Security Considerations

Infrastructure deployment must follow basic cloud security practices:

- Do not commit AWS credentials or access keys.
- Do not store database passwords or agent tokens in version control.
- Use IAM roles and least-privilege permissions.
- Restrict network access to required ports and services.
- Avoid exposing PostgreSQL directly to the public internet.
- Use HTTPS for external application communication.
- Store sensitive configuration in AWS Secrets Manager or an equivalent secure service.
- Protect Terraform state because it may contain sensitive infrastructure information.
- Review planned infrastructure changes before applying them.
- Configure backups and recovery procedures for persistent data.

The current local development configuration uses development credentials and a shared agent token. These values must not be reused as production secrets.

## 8. Environment Management

The future infrastructure should support separate environments.

| Environment | Purpose                                     |
| ----------- | ------------------------------------------- |
| Development | Feature development and manual verification |
| Testing     | Automated and integration testing           |
| Production  | Deployment for real monitoring workloads    |

Environment-specific configuration should be supplied through variables and secure configuration mechanisms rather than hardcoded into Terraform files.

## 9. Deployment and Cost Considerations

Cloud infrastructure must be selected according to the actual needs of the application.

Before provisioning AWS resources, the following factors should be evaluated:

- expected number of monitored servers;
- metric ingestion frequency;
- required metric retention period;
- backend resource requirements;
- database storage requirements;
- network traffic;
- backup and recovery requirements;
- availability and scaling requirements;
- estimated monthly infrastructure costs.

AWS resources may incur charges even when the application is not actively used. Infrastructure should therefore be provisioned only after reviewing the expected cost.

## 10. Current Implementation Status

| Feature                                    | Status          |
| ------------------------------------------ | --------------- |
| Docker-based local development environment | Implemented     |
| Backend Docker container                   | Implemented     |
| Frontend Docker container                  | Implemented     |
| PostgreSQL Docker container                | Implemented     |
| Terraform configuration                    | Not implemented |
| AWS provider configuration                 | Not implemented |
| AWS networking resources                   | Not implemented |
| Cloud database deployment                  | Not implemented |
| Cloud backend deployment                   | Not implemented |
| Cloud frontend hosting                     | Not implemented |
| Cloud monitoring configuration             | Not implemented |
| Automated cloud provisioning               | Not implemented |

## 11. Future Work

The following steps are recommended before using Terraform for a real deployment:

1. Select the target AWS deployment architecture.
2. Define required infrastructure resources.
3. Establish development and production environment configurations.
4. Create Terraform provider and version configuration.
5. Implement reusable infrastructure modules.
6. Configure secure remote Terraform state storage.
7. Add infrastructure validation and planning to the CI/CD workflow.
8. Review estimated AWS costs.
9. Provision a test environment.
10. Verify the deployed Sentinel application and monitoring agent connectivity.

## 12. Conclusion

Terraform is planned as a future tool for automating Sentinel infrastructure provisioning.

The current project uses Docker Compose for local development and integration of the frontend, backend, and PostgreSQL database.

Cloud infrastructure, AWS deployment, and Terraform automation remain future development tasks.
