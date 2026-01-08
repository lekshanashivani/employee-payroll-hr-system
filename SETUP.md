# HR Payroll Management System - Setup Guide

## Prerequisites

1. **Java 17** - Install JDK 17 or higher
2. **Maven 3.6+** - For building the project
3. **MySQL 8.0+** - Database server
4. **Gmail Account** - With App Password for email notifications

## Database Setup

Create the following MySQL databases:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE employee_db;
CREATE DATABASE attendance_db;
CREATE DATABASE payroll_db;
CREATE DATABASE notification_db;
CREATE DATABASE audit_db;
```

## Configuration

### 1. Update Database Credentials

Update `application.yml` in each service with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/{database_name}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: your_mysql_username
    password: your_mysql_password
```

### 2. Update JWT Secret

Update `jwt.secret` in:
- `authentication-service/src/main/resources/application.yml`
- `api-gateway/src/main/resources/application.yml`

Use a strong secret key (minimum 256 bits).

### 3. Configure Gmail SMTP

Update `notification-service/src/main/resources/application.yml`:

```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-gmail-app-password  # NOT your regular password!
```

**To get Gmail App Password:**
1. Go to Google Account settings
2. Enable 2-Step Verification
3. Go to App Passwords
4. Generate a new app password for "Mail"
5. Use this password in the configuration

## Building the Project

From the root directory:

```bash
mvn clean install
```

## Running the Services

**IMPORTANT:** Start services in this order:

1. **Eureka Server** (Port 8761)
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

2. **All Other Services** (in any order, they will register with Eureka)
   ```bash
   # Authentication Service (Port 8091)
   cd authentication-service
   mvn spring-boot:run

   # Employee Service (Port 8092)
   cd employee-service
   mvn spring-boot:run

   # Attendance Service (Port 8093)
   cd attendance-service
   mvn spring-boot:run

   # Payroll Service (Port 8094)
   cd payroll-service
   mvn spring-boot:run

   # Notification Service (Port 8095)
   cd notification-service
   mvn spring-boot:run

   # Audit Log Service (Port 8096)
   cd audit-log-service
   mvn spring-boot:run

   # API Gateway (Port 8080)
   cd api-gateway
   mvn spring-boot:run
   ```

## Service Ports

- **Eureka Server**: 8761
- **API Gateway**: 8080
- **Authentication Service**: 8091
- **Employee Service**: 8092
- **Attendance Service**: 8093
- **Payroll Service**: 8094
- **Notification Service**: 8095
- **Audit Log Service**: 8096

## API Gateway

All client requests should go through the API Gateway at `http://localhost:8080`.

The gateway routes requests to:
- `/api/auth/**` → Authentication Service
- `/api/employees/**` → Employee Service
- `/api/attendance/**` → Attendance Service
- `/api/payroll/**` → Payroll Service
- `/api/notifications/**` → Notification Service
- `/api/audit-logs/**` → Audit Log Service

## Authentication

1. **Login** (Public endpoint):
   ```bash
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json
   
   {
     "email": "user@example.com",
     "password": "password"
   }
   ```

2. **Use JWT Token**:
   Include the token in subsequent requests:
   ```
   Authorization: Bearer {jwt_token}
   ```

## Initial Setup

1. Create an ADMIN user in the `users` table (manually or via SQL):
   ```sql
   INSERT INTO users (email, password, role, active) 
   VALUES ('admin@company.com', '$2a$10$...', 'ADMIN', true);
   ```
   (Password should be BCrypt hashed)

2. Login as ADMIN and create HR accounts via API

3. Create employee profiles linked to user accounts

## Verification

1. Check Eureka Dashboard: `http://localhost:8761`
   - All services should be registered

2. Test API Gateway: `http://localhost:8080`
   - Should route requests correctly

3. Check service logs for any errors

## Troubleshooting

### Services not registering with Eureka
- Ensure Eureka Server is running first
- Check `eureka.client.service-url.defaultZone` in each service

### Database connection errors
- Verify MySQL is running
- Check database credentials
- Ensure databases are created

### JWT validation errors
- Ensure JWT secret matches in Authentication Service and API Gateway
- Check token expiration

### Email sending failures
- Verify Gmail App Password is correct
- Check Gmail account has 2-Step Verification enabled
- Review notification service logs

## Production Considerations

1. **Security**:
   - Use strong JWT secrets
   - Enable HTTPS
   - Use environment variables for sensitive configs
   - Implement rate limiting

2. **Database**:
   - Use connection pooling
   - Set up database backups
   - Use read replicas for scaling

3. **Monitoring**:
   - Add health checks
   - Implement distributed tracing
   - Set up logging aggregation

4. **Deployment**:
   - Use containerization (Docker)
   - Implement CI/CD pipelines
   - Use configuration management

