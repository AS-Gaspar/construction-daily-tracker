# Quick Start Guide - Construction Daily Tracker

This guide will help you get the API server running and connect your mobile app in 5 minutes.

## 📋 Prerequisites

- Java 11 or higher installed
- PostgreSQL running on localhost:5432 (or update `.env.local` with your database URL)

## 🚀 Step 1: Start the Server

### Option A: Using the startup script (Recommended)

**Linux/macOS:**
```bash
./start-server.sh
```

**Windows:**
```cmd
start-server.bat
```

This script will:
- ✅ Automatically load your API key from `.env.local`
- ✅ Start the server on http://0.0.0.0:8080
- ✅ Display the configured API key (first 8 characters)

### Option B: Manual startup

**Linux/macOS:**
```bash
# Load environment variables
export $(cat .env.local | grep -v '^#' | xargs)

# Start server
./gradlew :server:run
```

**Windows:**
```cmd
# Set environment variables manually
set API_KEY=c3837ce4f3a62a5cafa92c99db47e26b0e9acaeeb0fc890b54b0060976217e5c
set DB_URL=jdbc:postgresql://localhost:5432/construction_tracker
set DB_USER=postgres
set DB_PASSWORD=postgres

# Start server
gradlew.bat :server:run
```

## ✅ Step 2: Verify the Connection

Run the test script to verify everything is working:

```bash
./test-api-connection.sh
```

This will test:
1. ✓ Server is running
2. ✓ Authentication is working
3. ✓ API key is correctly configured

Expected output:
```
✅ All tests passed! Your API is ready to use.

📱 Mobile App Configuration:
   Server URL: http://10.0.2.2:8080 (for Android emulator)
              OR
              http://YOUR_COMPUTER_IP:8080 (for physical device)
   API Key: c3837ce4...
```

## 📱 Step 3: Configure the Mobile App

### Your API Key

Your API key is stored in `.env.local`:
```
API_KEY=c3837ce4f3a62a5cafa92c99db47e26b0e9acaeeb0fc890b54b0060976217e5c
```

**IMPORTANT:** Use this exact API key in your mobile app!

### Server URL

Choose the correct URL based on your setup:

| Device Type | Server URL |
|-------------|------------|
| Android Emulator | `http://10.0.2.2:8080` |
| Physical Device (same WiFi) | `http://YOUR_COMPUTER_IP:8080` |
| iOS Simulator | `http://localhost:8080` |
| Web Browser | `http://localhost:8080` |

### Find Your Computer's IP Address

**Linux/macOS:**
```bash
# WiFi
ifconfig | grep "inet " | grep -v 127.0.0.1

# Or
ip addr show | grep "inet "
```

**Windows:**
```cmd
ipconfig | findstr IPv4
```

Look for something like `192.168.1.xxx` or `10.0.0.xxx`

### Configure in the App

1. Open the mobile app
2. Enter the Server URL and API Key in the Settings screen
3. Tap "Save Configuration"
4. The app should connect and load data!

## 🧪 Manual Testing (Optional)

You can also test the API manually using curl:

```bash
# Get your API key
API_KEY=$(grep API_KEY .env.local | cut -d '=' -f2)

# Test the API
curl -H "X-API-Key: $API_KEY" http://localhost:8080/works

# Should return: []  (empty array if no works created yet)
```

## ❌ Troubleshooting

### Problem: "Server is not responding"

**Solution:**
1. Make sure PostgreSQL is running
2. Check database credentials in `.env.local`
3. Start the server with `./start-server.sh`

### Problem: "Authentication failed" or "Invalid API key"

**Solution:**
1. Check the API key in `.env.local` matches what you're using in the app
2. Make sure you started the server with `./start-server.sh` (which loads the .env file)
3. Verify the key is correct:
   ```bash
   grep API_KEY .env.local
   ```

### Problem: "Connection timeout" from mobile app

**Solution:**
1. **Android Emulator:** Use `http://10.0.2.2:8080` (NOT localhost)
2. **Physical Device:**
   - Use your computer's IP address (e.g., `http://192.168.1.100:8080`)
   - Make sure your phone is on the same WiFi network
   - Check your computer's firewall isn't blocking port 8080

### Problem: Can't connect from physical device

**Solution:**
1. Find your computer's IP address (see above)
2. Use that IP instead of localhost or 10.0.2.2
3. Make sure both devices are on the same WiFi network
4. Test connectivity:
   ```bash
   # On your phone's browser, visit:
   http://YOUR_COMPUTER_IP:8080/

   # Should show: "Construction Daily Tracker API"
   ```

## 🔐 Security Notes

### For Development:
- ✅ The default API key in `.env.local` is fine for local development
- ✅ The key is already in `.gitignore` so it won't be committed

### For Production:
- ⚠️ Change the API key before deploying
- ⚠️ Use HTTPS in production (not HTTP)
- ⚠️ Use environment variables on the server, not .env files

## 📚 Next Steps

Once connected, you can:
- ✅ Create construction sites (Works)
- ✅ Add job roles
- ✅ Add employees
- ✅ Track daily adjustments
- ✅ Generate monthly payrolls

## 🆘 Still Having Issues?

If you're still having trouble:

1. **Check server logs** - The terminal running the server shows detailed logs
2. **Test with curl** - Verify the API works before testing with the app
3. **Run the test script** - `./test-api-connection.sh` shows detailed diagnostics
4. **Check the API_CONNECTION_GUIDE.md** for more detailed troubleshooting

---

## Quick Reference

**Start Server:**
```bash
./start-server.sh
```

**Test Connection:**
```bash
./test-api-connection.sh
```

**Your API Key:**
```
c3837ce4f3a62a5cafa92c99db47e26b0e9acaeeb0fc890b54b0060976217e5c
```

**Server URLs:**
- Android Emulator: `http://10.0.2.2:8080`
- Physical Device: `http://YOUR_IP:8080`
- Local Browser: `http://localhost:8080`
