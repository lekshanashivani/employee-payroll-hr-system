# Postman Testing Guide - HR Payroll System

## 📋 Prerequisites

### 1. Start All Services
**IMPORTANT:** Start services in this order:

```bash
# Terminal 1: Eureka Server (Port 8761)
cd eureka-server
mvn spring-boot:run

# Terminal 2: API Gateway (Port 8080)
cd api-gateway
mvn spring-boot:run

# Terminal 3-9: All other services (can start in parallel)
cd authentication-service && mvn spring-boot:run
cd employee-service && mvn spring-boot:run
cd attendance-service && mvn spring-boot:run
cd payroll-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd audit-log-service && mvn spring-boot:run
```

### 2. Verify Services are Running
- **Eureka Dashboard**: http://localhost:8761
  - Should show all 7 services registered
- **API Gateway**: http://localhost:8080 (all requests go here)

### 3. Database Setup
Ensure all databases are created:
- `auth_db`
- `employee_db`
- `attendance_db`
- `payroll_db`
- `notification_db`
- `audit_db`

---

## 🔧 Postman Setup

### Create Postman Environment

Create a new environment in Postman with these variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `eureka_url` | `http://localhost:8761` | `http://localhost:8761` |
| `admin_token` | (leave empty) | (will be set after login) |
| `hr_token` | (leave empty) | (will be set after login) |
| `employee_token` | (leave empty) | (will be set after login) |
| `admin_user_id` | (leave empty) | (will be set after login) |
| `hr_user_id` | (leave empty) | (will be set after login) |
| `employee_user_id` | (leave empty) | (will be set after login) |
| `employee_id` | (leave empty) | (will be set after creating employee) |
| `designation_id` | (leave empty) | (will be set after creating designation) |
| `leave_request_id` | (leave empty) | (will be set after creating leave) |
| `payslip_id` | (leave empty) | (will be set after generating payslip) |

---

## 📝 Testing Flow

### PHASE 1: Authentication & User Setup

#### Step 1.1: ADMIN User Auto-Created
**Good News!** The ADMIN user is automatically created when the Authentication Service starts.

**Default Credentials:**
- **Email:** `admin@company.com`
- **Password:** `admin123`

The admin user is created automatically on first startup. If it already exists, it won't create a duplicate.

#### Step 1.2: Login as ADMIN
**Request:**
```
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
    "email": "admin@company.com",
    "password": "admin123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "admin@company.com",
    "role": "ADMIN",
    "userId": 1,
    "employeeId": null
}
```

**Action:** Copy the `token` and set it as `admin_token` in Postman environment.

#### Step 1.3: Create HR User
**Request:**
```
POST {{base_url}}/api/auth/users
Authorization: Bearer {{admin_token}}
Content-Type: application/json

{
    "email": "hr@company.com",
    "password": "hr123",
    "role": "HR"
}
```

**Response:**
```json
{
    "id": 2,
    "email": "hr@company.com",
    "role": "HR",
    "active": true
}
```

#### Step 1.4: Login as HR
**Request:**
```
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
    "email": "hr@company.com",
    "password": "hr123"
}
```

**Action:** Copy the token and set as `hr_token`, save `userId` as `hr_user_id`.

#### Step 1.5: Create EMPLOYEE User
**Request:**
```
POST {{base_url}}/api/auth/users
Authorization: Bearer {{admin_token}}
Content-Type: application/json

{
    "email": "employee@company.com",
    "password": "emp123",
    "role": "EMPLOYEE"
}
```

#### Step 1.6: Login as EMPLOYEE
**Request:**
```
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
    "email": "employee@company.com",
    "password": "emp123"
}
```

**Action:** Copy the token and set as `employee_token`, save `userId` as `employee_user_id`.

---

### PHASE 2: Employee Service Setup

#### Step 2.1: Create Designation (ADMIN only)
**Request:**
```
POST {{base_url}}/api/employees/designations
Authorization: Bearer {{admin_token}}
Content-Type: application/json

{
    "name": "Software Engineer",
    "baseSalary": 50000.00,
    "taxPercentage": 15.00,
    "bonusPercentage": 10.00,
    "active": true
}
```

**Response:**
```json
{
    "id": 1,
    "name": "Software Engineer",
    "baseSalary": 50000.00,
    "taxPercentage": 15.00,
    "bonusPercentage": 10.00,
    "active": true
}
```

**Action:** Save `id` as `designation_id` in environment.

#### Step 2.2: Get All Designations
**Request:**
```
GET {{base_url}}/api/employees/designations
Authorization: Bearer {{admin_token}}
```

#### Step 2.3: Create Employee Profile
**Request:**
```
POST {{base_url}}/api/employees
Authorization: Bearer {{hr_token}}
Content-Type: application/json

{
    "userId": {{employee_user_id}},
    "name": "John Doe",
    "phoneNumber": "+1234567890",
    "dateOfBirth": "1990-01-15",
    "address": "123 Main St, City, State",
    "department": "Engineering",
    "designation": {
        "id": {{designation_id}}
    },
    "status": "ACTIVE"
}
```

**Response:**
```json
{
    "id": 1,
    "userId": 3,
    "name": "John Doe",
    "department": "Engineering",
    "status": "ACTIVE"
}
```

**Action:** Save `id` as `employee_id` in environment.

#### Step 2.4: Get Employee by ID
**Request:**
```
GET {{base_url}}/api/employees/{{employee_id}}
Authorization: Bearer {{hr_token}}
```

#### Step 2.5: Get All Employees
**Request:**
```
GET {{base_url}}/api/employees
Authorization: Bearer {{hr_token}}
```

---

### PHASE 3: Attendance Service

#### Step 3.1: Mark Attendance
**Request:**
```
POST {{base_url}}/api/attendance/mark?employeeId={{employee_id}}&date=2024-01-15
Authorization: Bearer {{employee_token}}
```

**Response:**
```json
{
    "id": 1,
    "employeeId": 1,
    "date": "2024-01-15",
    "status": "PRESENT"
}
```

#### Step 3.2: Get Attendance by Employee and Date Range
**Request:**
```
GET {{base_url}}/api/attendance/employee/{{employee_id}}?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {{employee_token}}
```

#### Step 3.3: Create Leave Request
**Request:**
```
POST {{base_url}}/api/attendance/leave-requests
Authorization: Bearer {{employee_token}}
Content-Type: application/json

{
    "employeeId": {{employee_id}},
    "startDate": "2024-02-01",
    "endDate": "2024-02-05",
    "leaveType": "UNPAID",
    "reason": "Personal emergency"
}
```

**Response:**
```json
{
    "id": 1,
    "employeeId": 1,
    "startDate": "2024-02-01",
    "endDate": "2024-02-05",
    "leaveType": "UNPAID",
    "status": "PENDING"
}
```

**Action:** Save `id` as `leave_request_id`.

#### Step 3.4: Get Pending Leave Requests
**Request:**
```
GET {{base_url}}/api/attendance/leave-requests/pending
Authorization: Bearer {{hr_token}}
```

#### Step 3.5: Approve Leave Request (HR approves EMPLOYEE leave)
**Request:**
```
PUT {{base_url}}/api/attendance/leave-requests/{{leave_request_id}}/approve
Authorization: Bearer {{hr_token}}
```

**Response:**
```json
{
    "id": 1,
    "status": "APPROVED",
    "approvedBy": 2,
    "approvedAt": "2024-01-20T10:30:00"
}
```

#### Step 3.6: Create HR Meeting Request
**Request:**
```
POST {{base_url}}/api/attendance/hr-meetings
Authorization: Bearer {{employee_token}}
Content-Type: application/json

{
    "employeeId": {{employee_id}},
    "subject": "Salary Discussion",
    "description": "I would like to discuss my salary",
    "preferredDateTime": "2024-02-10T14:00:00"
}
```

#### Step 3.7: Get Pending HR Meeting Requests
**Request:**
```
GET {{base_url}}/api/attendance/hr-meetings/pending
Authorization: Bearer {{hr_token}}
```

#### Step 3.8: Approve HR Meeting Request
**Request:**
```
PUT {{base_url}}/api/attendance/hr-meetings/1/approve?scheduledDateTime=2024-02-10T14:00:00
Authorization: Bearer {{hr_token}}
```

---

### PHASE 4: Payroll Service

#### Step 4.1: Grant Bonus
**Request:**
```
POST {{base_url}}/api/payroll/bonuses
Authorization: Bearer {{hr_token}}
Content-Type: application/json

{
    "employeeId": {{employee_id}},
    "amount": 5000.00,
    "description": "Performance bonus Q1",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
}
```

#### Step 4.2: Generate Payslip
**Request:**
```
POST {{base_url}}/api/payroll/payslips/generate?employeeId={{employee_id}}&payPeriod=2024-01
Authorization: Bearer {{hr_token}}
```

**Response:**
```json
{
    "id": 1,
    "employeeId": 1,
    "payPeriod": "2024-01",
    "baseSalary": 50000.00,
    "totalBonuses": 5000.00,
    "unpaidLeaveDeduction": 0.00,
    "taxAmount": 7500.00,
    "netSalary": 47500.00
}
```

**Action:** Save `id` as `payslip_id`.

#### Step 4.3: Get Payslip by ID
**Request:**
```
GET {{base_url}}/api/payroll/payslips/{{payslip_id}}
Authorization: Bearer {{hr_token}}
```

#### Step 4.4: Get Payslips by Employee
**Request:**
```
GET {{base_url}}/api/payroll/payslips/employee/{{employee_id}}
Authorization: Bearer {{employee_token}}
```

#### Step 4.5: Get Payslips by Pay Period
**Request:**
```
GET {{base_url}}/api/payroll/payslips/period/2024-01
Authorization: Bearer {{hr_token}}
```

---

### PHASE 5: Notification Service

#### Step 5.1: Create Announcement (HR only)
**Request:**
```
POST {{base_url}}/api/notifications/announcements
Authorization: Bearer {{hr_token}}
Content-Type: application/json

{
    "title": "Company Holiday",
    "content": "Office will be closed on February 14th for company holiday",
    "targetAudience": "ALL",
    "sendEmail": false,
    "expiresAt": "2024-02-15T00:00:00"
}
```

#### Step 5.2: Get Active Announcements
**Request:**
```
GET {{base_url}}/api/notifications/announcements
Authorization: Bearer {{employee_token}}
```

#### Step 5.3: Get Notifications by Employee
**Request:**
```
GET {{base_url}}/api/notifications/employee/{{employee_id}}
Authorization: Bearer {{employee_token}}
```

---

### PHASE 6: Audit Log Service

#### Step 6.1: Get Audit Logs by Performed By
**Request:**
```
GET {{base_url}}/api/audit-logs/performed-by/{{hr_user_id}}
Authorization: Bearer {{admin_token}}
```

#### Step 6.2: Get Audit Logs by Action
**Request:**
```
GET {{base_url}}/api/audit-logs/action/LEAVE_APPROVED
Authorization: Bearer {{admin_token}}
```

#### Step 6.3: Get Audit Logs by Service Name
**Request:**
```
GET {{base_url}}/api/audit-logs/service/Payroll Service
Authorization: Bearer {{admin_token}}
```

---

## 🧪 Testing Scenarios

### Scenario 1: Complete Employee Lifecycle
1. Create ADMIN user (database)
2. Login as ADMIN
3. Create HR user
4. Create Designation
5. Create Employee
6. Mark Attendance
7. Create Leave Request
8. Approve Leave (as HR)
9. Generate Payslip
10. View Audit Logs

### Scenario 2: Role-Based Authorization
1. Try to create HR user as EMPLOYEE (should fail)
2. Try to approve leave as EMPLOYEE (should fail)
3. Try to generate payslip as EMPLOYEE (should fail)
4. Verify only HR can approve EMPLOYEE leaves
5. Verify only ADMIN can approve HR leaves

### Scenario 3: Payroll Calculation
1. Create employee with designation
2. Grant bonus
3. Create unpaid leave
4. Approve unpaid leave
5. Generate payslip
6. Verify: Net Salary = Base + Bonus - Unpaid Leave Deduction - Tax

### Scenario 4: Inter-Service Communication
1. Generate payslip (Payroll → Employee Service → Attendance Service)
2. Approve leave (Attendance → Notification Service → Audit Log Service)
3. Verify all services are communicating correctly

---

## 🔍 Troubleshooting

### Issue: 401 Unauthorized
- **Cause:** Missing or invalid JWT token
- **Solution:** Login again and update token in environment

### Issue: 404 Not Found
- **Cause:** Service not registered with Eureka or wrong endpoint
- **Solution:** 
  - Check Eureka dashboard: http://localhost:8761
  - Verify service is running
  - Check endpoint URL

### Issue: 500 Internal Server Error
- **Cause:** Database connection or service communication issue
- **Solution:**
  - Check database is running
  - Check service logs
  - Verify all services are registered with Eureka

### Issue: Services not communicating
- **Cause:** Eureka not running or service discovery issue
- **Solution:**
  - Start Eureka Server first
  - Wait 30 seconds for services to register
  - Check Eureka dashboard

---

## 📊 Expected Results Checklist

- [ ] All 7 services registered in Eureka
- [ ] Can login and get JWT token
- [ ] Can create users with proper roles
- [ ] Can create designations (ADMIN only)
- [ ] Can create employee profiles
- [ ] Can mark attendance
- [ ] Can create and approve leave requests
- [ ] Can generate payslips with correct calculations
- [ ] Can create announcements
- [ ] Can view audit logs
- [ ] Role-based authorization working
- [ ] Inter-service communication working

---

## 💡 Tips

1. **Use Postman Collections**: Create a collection for each service
2. **Use Pre-request Scripts**: Automatically set tokens from login responses
3. **Use Tests Tab**: Automatically save tokens and IDs to environment
4. **Check Logs**: Monitor service logs while testing
5. **Start Fresh**: Clear databases between test runs if needed

---

## 📝 Postman Pre-request Script Example

For login requests, add this to automatically save token:

```javascript
// In Tests tab of login request
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("admin_token", response.token);
    pm.environment.set("admin_user_id", response.userId);
}
```

---

## 🎯 Quick Test Sequence

**Minimum viable test (5 minutes):**
1. Login as ADMIN
2. Create Designation
3. Create Employee
4. Mark Attendance
5. Generate Payslip

If all these work, your system is functioning correctly!

