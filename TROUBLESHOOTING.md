# Troubleshooting: Cannot Connect to Server

## Quick Checklist

### 1. Verify All Services Are Running

Check if these services are running in order:

1. **Eureka Server** (port 8761)
   - URL: http://localhost:8761
   - Should show dashboard with registered services

2. **API Gateway** (port 8080) - **CRITICAL**
   - This is what the frontend connects to
   - Check: http://localhost:8080
   - Should respond (even if 404, means it's running)

3. **Authentication Service** (port 8091)
   - Should register with Eureka

4. **Employee Service** (port 8092)
   - Should register with Eureka

5. **Other Services** (8093-8096)
   - Attendance, Payroll, Notification, Audit Log

### 2. Start Services in Order

```bash
# Terminal 1: Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 2: API Gateway (WAIT for Eureka to start first)
cd api-gateway
mvn spring-boot:run

# Terminal 3: Authentication Service
cd authentication-service
mvn spring-boot:run

# Terminal 4: Employee Service
cd employee-service
mvn spring-boot:run

# Continue with other services...
```

### 3. Verify API Gateway is Running

Open browser and check:
- http://localhost:8080 - Should not give connection refused
- http://localhost:8761 - Eureka dashboard should show registered services

### 4. Check Eureka Registration

Go to http://localhost:8761 and verify:
- `api-gateway` is registered
- `authentication-service` is registered
- Other services are registered

### 5. Test API Gateway Directly

Try in browser or Postman:
```
GET http://localhost:8080/api/auth/login
```

Should return 405 (Method Not Allowed) or similar - means gateway is working.

### 6. Check Frontend Configuration

Verify `frontend/src/app/services/auth.service.ts` has:
```typescript
private apiUrl = 'http://localhost:8080/api/auth';
```

### 7. Common Issues

**Issue: Connection Refused**
- Solution: API Gateway is not running
- Fix: Start API Gateway service

**Issue: CORS Errors**
- Solution: CORS config should be in API Gateway
- Fix: Restart API Gateway after adding CORS config

**Issue: 404 Not Found**
- Solution: Service not registered with Eureka
- Fix: Check Eureka dashboard, restart service

**Issue: Timeout**
- Solution: Service is slow to respond
- Fix: Check service logs, increase timeout

### 8. Verify Network Connection

In browser console (F12), check:
- Network tab shows requests to `localhost:8080`
- Check if requests are being made
- Look at response status codes

### 9. Check Service Logs

Look for errors in:
- API Gateway logs
- Authentication Service logs
- Eureka Server logs

### 10. Quick Test

Run this in browser console:
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'test', password: 'test' })
})
.then(r => console.log('Status:', r.status))
.catch(e => console.error('Error:', e));
```

If this fails, the API Gateway is not accessible.



