# HR Payroll Management System - Backend Only

Enterprise-grade microservices-based HR Payroll Management System built with Spring Boot.

## Architecture

This system consists of 8 microservices:

1. **Eureka Server** - Service discovery and registration
2. **API Gateway** - JWT validation, routing, and context propagation
3. **Authentication Service** - User authentication and JWT generation
4. **Employee Service** - Employee lifecycle and designation management
5. **Attendance Service** - Attendance tracking, leave management, and HR meeting requests
6. **Payroll Service** - Salary calculation and payslip generation
7. **Notification Service** - Email notifications and announcements
8. **Audit Log Service** - Centralized audit logging

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- MySQL (each service has its own database)
- JWT Authentication
- Eureka Service Discovery
- Spring Cloud Gateway
- OpenFeign for inter-service communication
- Gmail SMTP for email notifications

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Gmail account with App Password for email service

## Database Setup

Each microservice requires its own MySQL database. Create the following databases:

- `eureka_db` (optional, for Eureka persistence)
- `auth_db`
- `employee_db`
- `attendance_db`
- `payroll_db`
- `notification_db`
- `audit_db`

## Configuration

Update `application.yml` in each service with:
- MySQL connection details
- Eureka server URL
- Gmail SMTP credentials (for Notification Service)
- JWT secret key (for Authentication Service and API Gateway)

## Running the Services

1. Start Eureka Server first
2. Start all other services in any order
3. Services will auto-register with Eureka

## API Gateway Port

The API Gateway runs on port 8080 by default. All client requests should go through the gateway.

## Security

- JWT tokens are validated only at the API Gateway
- User context (userId, employeeId, role) is forwarded via headers:
  - `X-User-Id`
  - `X-Employee-Id`
  - `X-User-Role`

## Roles

- **ADMIN**: Full system access, manages HR accounts
- **HR**: Employee management, leave approvals, payroll operations
- **EMPLOYEE**: Self-service, leave requests, attendance tracking

