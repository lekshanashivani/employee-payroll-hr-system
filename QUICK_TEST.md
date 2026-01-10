# Quick Connection Test

## Test 1: Is API Gateway Running?

Open browser and go to:
```
http://localhost:8080
```

**Expected:** Should load (even if shows error page)
**If fails:** API Gateway is not running

## Test 2: Is Eureka Running?

Open browser and go to:
```
http://localhost:8761
```

**Expected:** Eureka dashboard with service list
**If fails:** Eureka is not running

## Test 3: Test Login Endpoint Directly

Open browser console (F12) and paste:
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    email: 'admin@company.com',
    password: 'admin123'
  })
})
.then(response => {
  console.log('Status:', response.status);
  return response.json();
})
.then(data => {
  console.log('Response:', data);
  if (data.token) {
    console.log('✅ Login successful!');
  }
})
.catch(error => {
  console.error('❌ Error:', error);
  console.log('API Gateway is not accessible. Please start it.');
});
```

## Test 4: Check Services in Eureka

1. Go to http://localhost:8761
2. Click on "Instances currently registered with Eureka"
3. Should see:
   - `api-gateway`
   - `authentication-service`
   - Other services (if started)

## What to Do If Tests Fail

### Test 1 Fails (API Gateway)
```bash
cd api-gateway
mvn spring-boot:run
```

### Test 2 Fails (Eureka)
```bash
cd eureka-server
mvn spring-boot:run
```

### Test 3 Fails (Connection Error)
1. Check if API Gateway is running (Test 1)
2. Check if Eureka is running (Test 2)
3. Check browser console for CORS errors
4. Verify port 8080 is not blocked by firewall

### Test 4 Shows No Services
- Services are not registered
- Check service logs for errors
- Ensure services are connecting to Eureka at http://localhost:8761



