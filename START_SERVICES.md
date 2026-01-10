# How to Start All Services

## ⚠️ IMPORTANT: Start Services in This Order

### Step 1: Start Eureka Server (MUST BE FIRST)
```bash
cd eureka-server
mvn spring-boot:run
```
**Wait until you see:** "Started EurekaServerApplication"
**Check:** http://localhost:8761 (should show Eureka dashboard)

### Step 2: Start API Gateway (MUST BE SECOND)
```bash
cd api-gateway
mvn spring-boot:run
```
**Wait until you see:** "Started ApiGatewayApplication"
**Check:** http://localhost:8080 (should respond, even if 404)

### Step 3: Start Authentication Service
```bash
cd authentication-service
mvn spring-boot:run
```
**Wait until you see:** "Started AuthenticationServiceApplication"
**Check:** Eureka dashboard should show `authentication-service` registered

### Step 4: Start Other Services (Can be parallel)
Open new terminals for each:

```bash
# Terminal 4: Employee Service
cd employee-service
mvn spring-boot:run

# Terminal 5: Attendance Service
cd attendance-service
mvn spring-boot:run

# Terminal 6: Payroll Service
cd payroll-service
mvn spring-boot:run

# Terminal 7: Notification Service
cd notification-service
mvn spring-boot:run

# Terminal 8: Audit Log Service
cd audit-log-service
mvn spring-boot:run
```

## Quick Verification

### 1. Check Eureka Dashboard
Open: http://localhost:8761
- Should see all services registered (green UP status)

### 2. Test API Gateway
Open browser console (F12) and run:
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'test', password: 'test' })
})
.then(r => console.log('✅ Gateway is running! Status:', r.status))
.catch(e => console.error('❌ Gateway not accessible:', e));
```

### 3. Check Service Logs
Look for these messages:
- ✅ "Started [ServiceName]Application"
- ✅ "Registered with Eureka"
- ❌ Any errors or exceptions

## Common Issues

### "Connection Refused"
- **Cause:** Service not running
- **Fix:** Start the service

### "Service Not Found" in Eureka
- **Cause:** Service didn't register
- **Fix:** Check service logs, restart service

### "404 Not Found" from Gateway
- **Cause:** Service not registered with Eureka
- **Fix:** Check Eureka dashboard, ensure service is UP

### Port Already in Use
- **Cause:** Another instance running
- **Fix:** Kill the process using the port or change port in application.yml

## Minimum Services for Login

To test login, you need at least:
1. ✅ Eureka Server (8761)
2. ✅ API Gateway (8080)
3. ✅ Authentication Service (8091)

Other services can be started later.

## Quick Start Script (Windows PowerShell)

Save as `start-all.ps1`:
```powershell
# Start Eureka
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd eureka-server; mvn spring-boot:run"
Start-Sleep -Seconds 30

# Start API Gateway
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd api-gateway; mvn spring-boot:run"
Start-Sleep -Seconds 20

# Start Authentication Service
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd authentication-service; mvn spring-boot:run"
```

Run: `.\start-all.ps1`



