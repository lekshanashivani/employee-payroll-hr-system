# Swagger API Documentation Guide

Swagger (SpringDoc OpenAPI) has been integrated into all microservices for API testing and documentation.

## Accessing Swagger UI

Once a service is running, you can access its Swagger UI at:

### Service URLs:

1. **Authentication Service** (Port 8091)
   - Swagger UI: http://localhost:8091/swagger-ui/index.html
   - API Docs JSON: http://localhost:8091/v3/api-docs

2. **Employee Service** (Port 8092)
   - Swagger UI: http://localhost:8092/swagger-ui/index.html
   - API Docs JSON: http://localhost:8092/v3/api-docs

3. **Attendance Service** (Port 8093)
   - Swagger UI: http://localhost:8093/swagger-ui/index.html
   - API Docs JSON: http://localhost:8093/v3/api-docs

4. **Payroll Service** (Port 8094)
   - Swagger UI: http://localhost:8094/swagger-ui/index.html
   - API Docs JSON: http://localhost:8094/v3/api-docs

5. **Notification Service** (Port 8095)
   - Swagger UI: http://localhost:8095/swagger-ui/index.html
   - API Docs JSON: http://localhost:8095/v3/api-docs

6. **Audit Log Service** (Port 8096)
   - Swagger UI: http://localhost:8096/swagger-ui/index.html
   - API Docs JSON: http://localhost:8096/v3/api-docs

## Features

- **Interactive API Testing**: Test all endpoints directly from the browser
- **Request/Response Examples**: See example request and response bodies
- **Schema Documentation**: View all DTOs and their properties
- **Try It Out**: Execute API calls and see responses in real-time

## Using Swagger UI

1. **Start the service** you want to test
2. **Open the Swagger UI URL** in your browser
3. **Expand an endpoint** to see its details
4. **Click "Try it out"** to test the endpoint
5. **Fill in the parameters** and click "Execute"
6. **View the response** below

## Authentication in Swagger

For endpoints that require authentication:

1. **Login first** using the `/api/auth/login` endpoint
2. **Copy the JWT token** from the response (just the token value, not "Bearer")
3. **Click the "Authorize" button** (lock icon) at the top right of Swagger UI
4. **Enter your JWT token** in the "Value" field (Swagger will automatically add "Bearer" prefix)
5. **Click "Authorize"** and then "Close"
6. Now all authenticated endpoints will include the token automatically in the `Authorization` header

## Example: Testing Login

1. Go to http://localhost:8091/swagger-ui/index.html
2. Find the `POST /api/auth/login` endpoint
3. Click "Try it out"
4. Enter the request body:
   ```json
   {
     "email": "admin@company.com",
     "password": "admin123"
   }
   ```
5. Click "Execute"
6. Copy the `token` value from the response (just the token string, without quotes)
7. Click the "Authorize" button (lock icon) at the top right
8. Paste the token in the "Value" field and click "Authorize"
9. Now you can test other endpoints that require authentication

## Notes

- Swagger UI is accessible without authentication (public endpoints)
- The `/api/auth/login` endpoint is public and doesn't require a token
- Other endpoints require JWT authentication via the "Authorize" button
- Each service has its own Swagger UI instance

## Complete Testing Guide - All Endpoints

This section provides step-by-step instructions to test all endpoints in the system.

### Prerequisites

1. **Start all services** (Eureka Server, all microservices, and API Gateway)
2. **Get JWT Token** by logging in first (see Authentication section below)
3. **Authorize in Swagger UI** using the token

---

## 1. Authentication Service (Port 8091)

**Swagger UI**: http://localhost:8091/swagger-ui/index.html

### Step 1: Login (Public Endpoint)

1. Open Swagger UI for Authentication Service
2. Find `POST /api/auth/login`
3. Click "Try it out"
4. Enter request body:
   ```json
   {
     "email": "admin@company.com",
     "password": "admin123"
   }
   ```
5. Click "Execute"
6. **Copy the `token` value** from the response
7. Click the "Authorize" button (lock icon) at the top right
8. Paste the token and click "Authorize"

### Step 2: Create User (Requires Authentication)

1. Find `POST /api/auth/users`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "email": "hr@company.com",
     "password": "hr123",
     "role": "HR"
   }
   ```
4. Add headers (Swagger will show these):
   - `X-User-Id`: `1` (your user ID from login)
   - `X-User-Role`: `ADMIN`
5. Click "Execute"

### Step 3: Update User Role

1. Find `PUT /api/auth/users/{userId}/role`
2. Click "Try it out"
3. Enter:
   - `userId`: `2` (the user ID you want to update)
   - `newRole`: `EMPLOYEE` (or `HR`, `ADMIN`)
   - `X-User-Id`: `1`
4. Click "Execute"

### Step 4: Deactivate User

1. Find `PUT /api/auth/users/{userId}/deactivate`
2. Click "Try it out"
3. Enter `userId`: `2`
4. Click "Execute"

---

## 2. Employee Service (Port 8092)

**Swagger UI**: http://localhost:8092/swagger-ui/index.html

**Note**: Make sure you're authorized with a JWT token from Authentication Service.

### Step 1: Create Designation

1. Find `POST /api/employees/designations`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "name": "Software Engineer",
     "baseSalary": 50000.00,
     "taxPercentage": 15.0,
     "bonusPercentage": 10.0,
     "active": true
   }
   ```
4. Click "Execute"
5. **Note the `id` from response** (you'll need it for creating employees)

### Step 2: Get All Designations

1. Find `GET /api/employees/designations`
2. Click "Try it out"
3. Click "Execute"

### Step 3: Create Employee

1. Find `POST /api/employees`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "userId": 2,
     "name": "John Doe",
     "phoneNumber": "1234567890",
     "dateOfBirth": "1990-01-15",
     "address": "123 Main St, City",
     "department": "Engineering",
     "designation": {
       "id": 1,
       "name": "Software Engineer",
       "baseSalary": 50000.00,
       "taxPercentage": 15.0,
       "bonusPercentage": 10.0,
       "active": true
     },
     "status": "ACTIVE"
   }
   ```
4. Click "Execute"
5. **Note the employee `id` from response**

### Step 4: Get Employee by ID

1. Find `GET /api/employees/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Click "Execute"

### Step 5: Get All Employees

1. Find `GET /api/employees`
2. Click "Try it out"
3. Click "Execute"

### Step 6: Update Employee

1. Find `PUT /api/employees/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Enter request body:
   ```json
   {
     "name": "John Doe Updated",
     "phoneNumber": "9876543210",
     "department": "Engineering",
     "designationId": 1,
     "status": "ACTIVE"
   }
   ```
5. Add headers:
   - `X-User-Id`: `1`
   - `X-User-Role`: `ADMIN`
6. Click "Execute"

### Step 7: Get Employee by User ID

1. Find `GET /api/employees/user/{userId}`
2. Click "Try it out"
3. Enter `userId`: `2`
4. Click "Execute"

### Step 8: Deactivate Employee

1. Find `PUT /api/employees/{id}/deactivate`
2. Click "Try it out"
3. Enter:
   - `id`: `1`
   - `status`: `TERMINATED`
   - `X-User-Id`: `1`
4. Click "Execute"

---

## 3. Attendance Service (Port 8093)

**Swagger UI**: http://localhost:8093/swagger-ui/index.html

### Step 1: Mark Attendance

1. Find `POST /api/attendance/mark`
2. Click "Try it out"
3. Enter:
   - `employeeId`: `1`
   - `date`: `2026-01-09` (use current date format: YYYY-MM-DD)
4. Click "Execute"

### Step 2: Get Attendance by Employee and Date Range

1. Find `GET /api/attendance/employee/{employeeId}`
2. Click "Try it out"
3. Enter:
   - `employeeId`: `1`
   - `startDate`: `2026-01-01`
   - `endDate`: `2026-01-31`
4. Click "Execute"

### Step 3: Create Leave Request

1. Find `POST /api/attendance/leave-requests`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "employeeId": 1,
     "leaveType": "SICK_LEAVE",
     "startDate": "2026-01-15",
     "endDate": "2026-01-17",
     "reason": "Medical appointment",
     "status": "PENDING"
   }
   ```
4. Click "Execute"
5. **Note the leave request `id` from response**

### Step 4: Get Leave Requests by Employee

1. Find `GET /api/attendance/leave-requests/employee/{employeeId}`
2. Click "Try it out"
3. Enter `employeeId`: `1`
4. Click "Execute"

### Step 5: Get Pending Leave Requests

1. Find `GET /api/attendance/leave-requests/pending`
2. Click "Try it out"
3. Click "Execute"

### Step 6: Approve Leave Request

1. Find `PUT /api/attendance/leave-requests/{id}/approve`
2. Click "Try it out"
3. Enter:
   - `id`: `1` (leave request ID)
   - `X-User-Id`: `1`
   - `X-User-Role`: `HR` or `ADMIN`
4. Click "Execute"

### Step 7: Reject Leave Request

1. Find `PUT /api/attendance/leave-requests/{id}/reject`
2. Click "Try it out"
3. Enter:
   - `id`: `1`
   - `rejectionReason`: `Insufficient leave balance`
   - `X-User-Id`: `1`
   - `X-User-Role`: `HR` or `ADMIN`
4. Click "Execute"

### Step 8: Create HR Meeting Request

1. Find `POST /api/attendance/hr-meetings`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "employeeId": 1,
     "reason": "Performance review discussion",
     "preferredDateTime": "2026-01-20T10:00:00",
     "status": "PENDING"
   }
   ```
4. Click "Execute"
5. **Note the meeting request `id` from response**

### Step 9: Get HR Meeting Requests by Employee

1. Find `GET /api/attendance/hr-meetings/employee/{employeeId}`
2. Click "Try it out"
3. Enter `employeeId`: `1`
4. Click "Execute"

### Step 10: Approve HR Meeting Request

1. Find `PUT /api/attendance/hr-meetings/{id}/approve`
2. Click "Try it out"
3. Enter:
   - `id`: `1` (meeting request ID)
   - `scheduledDateTime`: `2026-01-20T14:00:00`
   - `X-User-Id`: `1`
4. Click "Execute"

---

## 4. Payroll Service (Port 8094)

**Swagger UI**: http://localhost:8094/swagger-ui/index.html

### Step 1: Generate Payslip

1. Find `POST /api/payroll/payslips/generate`
2. Click "Try it out"
3. Enter:
   - `employeeId`: `1`
   - `payPeriod`: `2026-01` (format: YYYY-MM)
   - `X-User-Id`: `1`
4. Click "Execute"
5. **Note the payslip `id` from response**

### Step 2: Get Payslip by ID

1. Find `GET /api/payroll/payslips/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Click "Execute"

### Step 3: Get Payslips by Employee

1. Find `GET /api/payroll/payslips/employee/{employeeId}`
2. Click "Try it out"
3. Enter `employeeId`: `1`
4. Click "Execute"

### Step 4: Get Payslips by Pay Period

1. Find `GET /api/payroll/payslips/period/{payPeriod}`
2. Click "Try it out"
3. Enter `payPeriod`: `2026-01`
4. Click "Execute"

### Step 5: Grant Bonus

1. Find `POST /api/payroll/bonuses`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "employeeId": 1,
     "amount": 5000.00,
     "reason": "Performance bonus",
     "grantedDate": "2026-01-09"
   }
   ```
4. Add header:
   - `X-User-Id`: `1`
5. Click "Execute"

### Step 6: Get Bonuses by Employee

1. Find `GET /api/payroll/bonuses/employee/{employeeId}`
2. Click "Try it out"
3. Enter `employeeId`: `1`
4. Click "Execute"

### Step 7: Get Bonus by ID

1. Find `GET /api/payroll/bonuses/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Click "Execute"

---

## 5. Notification Service (Port 8095)

**Swagger UI**: http://localhost:8095/swagger-ui/index.html

### Step 1: Get Notifications by Employee

1. Find `GET /api/notifications/employee/{employeeId}`
2. Click "Try it out"
3. Enter `employeeId`: `1`
4. Click "Execute"

### Step 2: Get Notification by ID

1. Find `GET /api/notifications/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Click "Execute"

### Step 3: Create Announcement

1. Find `POST /api/notifications/announcements`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "title": "Company Holiday Notice",
     "content": "Office will be closed on January 26th",
     "targetAudience": "ALL",
     "active": true
   }
   ```
4. Add header:
   - `X-User-Id`: `1`
5. Click "Execute"

### Step 4: Get Active Announcements

1. Find `GET /api/notifications/announcements`
2. Click "Try it out"
3. Click "Execute"

### Step 5: Get Announcement by ID

1. Find `GET /api/notifications/announcements/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Click "Execute"

**Note**: The notification endpoints for payroll-generated, leave-approval, and hr-meeting are typically called internally by other services, but you can test them directly if needed.

---

## 6. Audit Log Service (Port 8096)

**Swagger UI**: http://localhost:8096/swagger-ui/index.html

### Step 1: Create Audit Log

1. Find `POST /api/audit-logs`
2. Click "Try it out"
3. Enter request body:
   ```json
   {
     "action": "CREATE_EMPLOYEE",
     "serviceName": "employee-service",
     "performedBy": 1,
     "targetId": 1,
     "description": "Created new employee",
     "oldValues": {},
     "newValues": {
       "name": "John Doe",
       "department": "Engineering"
     }
   }
   ```
4. Click "Execute"

### Step 2: Get Audit Log by ID

1. Find `GET /api/audit-logs/{id}`
2. Click "Try it out"
3. Enter `id`: `1`
4. Click "Execute"

### Step 3: Get Audit Logs by Performed By

1. Find `GET /api/audit-logs/performed-by/{performedBy}`
2. Click "Try it out"
3. Enter `performedBy`: `1`
4. Click "Execute"

### Step 4: Get Audit Logs by Target ID

1. Find `GET /api/audit-logs/target/{targetId}`
2. Click "Try it out"
3. Enter `targetId`: `1`
4. Click "Execute"

### Step 5: Get Audit Logs by Action

1. Find `GET /api/audit-logs/action/{action}`
2. Click "Try it out"
3. Enter `action`: `CREATE_EMPLOYEE`
4. Click "Execute"

### Step 6: Get Audit Logs by Service Name

1. Find `GET /api/audit-logs/service/{serviceName}`
2. Click "Try it out"
3. Enter `serviceName`: `employee-service`
4. Click "Execute"

---

## Testing Workflow Example

Here's a complete workflow to test the system:

1. **Login** (Authentication Service)
   - Get JWT token
   - Authorize in Swagger UI

2. **Create Designation** (Employee Service)
   - Create a designation (e.g., "Software Engineer")

3. **Create Employee** (Employee Service)
   - Create an employee linked to a user

4. **Mark Attendance** (Attendance Service)
   - Mark attendance for the employee

5. **Create Leave Request** (Attendance Service)
   - Create a leave request

6. **Approve Leave** (Attendance Service)
   - Approve the leave request (as HR/ADMIN)

7. **Generate Payslip** (Payroll Service)
   - Generate payslip for the employee

8. **Check Notifications** (Notification Service)
   - View notifications for the employee

9. **Check Audit Logs** (Audit Log Service)
   - View all audit logs created during the workflow

---

## Troubleshooting

- **404 Not Found**: Make sure the service is running
- **CORS Issues**: Swagger UI should work fine for same-origin requests
- **Authentication Errors**: Make sure you've authorized with a valid JWT token
- **401 Unauthorized**: Token may have expired, login again and re-authorize
- **400 Bad Request**: Check request body format and required fields
- **Header Issues**: Some endpoints require `X-User-Id` and `X-User-Role` headers - add them manually in Swagger if not auto-populated

