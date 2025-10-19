# API Connection Guide

This guide explains how to connect the Construction Daily Tracker mobile app to your backend API.

## Overview

The app is now fully configured to connect to the API with the following features:
- ✅ Secure API key storage using Android EncryptedSharedPreferences (AES256-GCM)
- ✅ Automatic credential loading on app startup
- ✅ HTTP client with automatic API key header injection
- ✅ Repository pattern for all API endpoints
- ✅ Settings screen for configuring server URL and API key

## Quick Start

### 1. Start the Server

First, make sure your backend server is running:

```bash
# Start the Ktor server
./gradlew :server:run

# Server will start on http://localhost:8080
# Check it's running by visiting http://localhost:8080/
```

### 2. Get Your API Key

The server uses static API key authentication. By default:
- **Development API Key**: Check your server environment or use the default from `server/.env`
- **Default**: `change-me-in-production`

You can set a custom API key via environment variable:
```bash
export API_KEY="your-secret-key-here"
./gradlew :server:run
```

### 3. Configure the Mobile App

When you first launch the app, you'll see the Settings screen.

#### For Android Emulator:
- **Server URL**: `http://10.0.2.2:8080`
  - This is the special IP that Android emulator uses to access localhost on your development machine
- **API Key**: Enter your API key (e.g., `change-me-in-production`)

#### For Physical Android Device:
- **Server URL**: `http://YOUR_COMPUTER_IP:8080`
  - Find your computer's IP address:
    - Windows: `ipconfig`
    - macOS/Linux: `ifconfig` or `ip addr`
  - Example: `http://192.168.1.100:8080`
- **API Key**: Enter your API key

#### For Web (Development):
- **Server URL**: `http://localhost:8080`
- **API Key**: Enter your API key

### 4. Save and Connect

1. Enter the server URL and API key in the Settings screen
2. Tap "Save Configuration"
3. The app will:
   - Securely store your credentials (encrypted on Android)
   - Initialize the HTTP client
   - Automatically load all data from the API
   - Remember your settings for future app launches

## How It Works

### Architecture

```
┌─────────────────────┐
│   Settings Screen   │ ← User enters URL and API key
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│   AppViewModel      │ ← Manages all data and API calls
│  configureApi()     │
└──────────┬──────────┘
           │
           ├─────────────────────────────────┐
           │                                 │
           ▼                                 ▼
┌─────────────────────┐         ┌─────────────────────┐
│ CredentialStorage   │         │   ApiClient         │
│ (Encrypted Storage) │         │  (HTTP Client)      │
└─────────────────────┘         └──────────┬──────────┘
                                           │
                        ┌──────────────────┼──────────────────┐
                        │                  │                  │
                        ▼                  ▼                  ▼
              ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
              │WorkRepository│   │RoleRepository│   │  ... etc     │
              └──────────────┘   └──────────────┘   └──────────────┘
                        │                  │                  │
                        └──────────────────┼──────────────────┘
                                           │
                                           ▼
                                ┌─────────────────────┐
                                │  Backend API        │
                                │  (Ktor Server)      │
                                └─────────────────────┘
```

### Key Components

1. **CredentialStorage** (`composeApp/src/commonMain/kotlin/.../data/CredentialStorage.kt`)
   - Expect/actual pattern for platform-specific storage
   - Android: Uses EncryptedSharedPreferences with AES256-GCM
   - Web: In-memory storage (can be enhanced with localStorage)

2. **ApiClient** (`composeApp/src/commonMain/kotlin/.../network/ApiClient.kt`)
   - Configures Ktor HTTP client
   - Automatically adds `X-API-Key` header to all requests
   - Sets up JSON serialization
   - Configures timeouts (30s request, 10s connect)

3. **Repositories** (`composeApp/src/commonMain/kotlin/.../network/`)
   - `WorkRepository`: Manages construction sites
   - `RoleRepository`: Manages job roles
   - `EmployeeRepository`: Manages employees
   - `DayAdjustmentRepository`: Manages daily adjustments
   - `PayrollRepository`: Manages monthly payrolls

4. **AppViewModel** (`composeApp/src/commonMain/kotlin/.../AppViewModel.kt`)
   - Loads saved credentials on initialization
   - Creates repositories and HTTP client
   - Manages all API calls and app state
   - Automatically refreshes data from server

## API Endpoints

The app connects to these endpoints:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/works` | GET | Get all construction sites |
| `/works` | POST | Create new work site |
| `/roles` | GET | Get all job roles |
| `/roles` | POST | Create new role |
| `/employees` | GET | Get all employees |
| `/employees` | POST | Create new employee |
| `/day-adjustments` | GET | Get all adjustments |
| `/day-adjustments` | POST | Create new adjustment |
| `/monthly-payrolls` | GET | Get all payrolls |
| `/monthly-payrolls/generate` | POST | Generate payroll |

All requests automatically include the `X-API-Key` header.

## Testing the Connection

### 1. Test Server Availability

```bash
# From terminal
curl http://localhost:8080/

# You should see a welcome message
```

### 2. Test API Endpoint

```bash
# Test with API key
curl -H "X-API-Key: change-me-in-production" http://localhost:8080/works

# Should return: []  (empty array if no works created yet)
```

### 3. Test from Mobile App

1. Open the app
2. Enter server URL and API key in Settings
3. Tap "Save Configuration"
4. Go to "Works" screen
5. Try adding a new work site
6. If successful, you're connected!

## Troubleshooting

### "Failed to load data" Error

**Possible causes:**
1. Server is not running
   - Solution: Start the server with `./gradlew :server:run`

2. Wrong server URL
   - Android emulator: Use `http://10.0.2.2:8080` (not localhost)
   - Physical device: Use your computer's local IP
   - Web: Use `http://localhost:8080`

3. Wrong API key
   - Check your server's environment variable or default key
   - Default is usually `change-me-in-production`

4. Network connectivity
   - Ensure device and server are on the same network (for physical devices)
   - Check firewall settings

### Connection Timeout

**Possible causes:**
1. Server is slow to respond
   - Check server logs
   - Increase timeout in ApiClient.kt (currently 30s)

2. Network issues
   - Check WiFi connection
   - Try pinging the server from your device

### API Key Not Saved

**On Android:**
- Credentials are encrypted and persisted
- They survive app restarts
- To clear: Go to Settings → Save with empty values

**On Web:**
- Credentials are in-memory only
- Will be lost on page reload
- TODO: Implement localStorage persistence

## Security Notes

### For Development:
- Default API key is fine for local development
- Use `http://` for local testing

### For Production:
1. **Always use HTTPS** to protect API key in transit
2. **Change the default API key** before deploying
3. **Rotate API keys periodically**
4. **Never commit API keys to git**
5. **Use environment variables** on the server
6. **On Android**, credentials are encrypted using Android Keystore (AES256-GCM)

## Advanced Configuration

### Custom Default Server URL

Edit `composeApp/src/androidMain/kotlin/.../data/ApiKeyManager.kt`:

```kotlin
companion object {
    private const val DEFAULT_SERVER_URL = "http://your-server.com:8080"
}
```

### Custom Timeouts

Edit `composeApp/src/commonMain/kotlin/.../network/ApiClient.kt`:

```kotlin
install(HttpTimeout) {
    requestTimeoutMillis = 60_000  // 60 seconds
    connectTimeoutMillis = 15_000  // 15 seconds
    socketTimeoutMillis = 60_000   // 60 seconds
}
```

### Adding New Endpoints

1. Add method to appropriate repository:
```kotlin
suspend fun getEmployeeByName(name: String): Employee {
    return client.get("/employees/search") {
        parameter("name", name)
    }.body()
}
```

2. Call from ViewModel:
```kotlin
fun searchEmployee(name: String) {
    viewModelScope.launch {
        val employee = employeeRepository?.getEmployeeByName(name)
        // Handle result...
    }
}
```

## Next Steps

Now that your app is connected to the API:

1. ✅ Test all CRUD operations (Create, Read, Update, Delete)
2. ✅ Test payroll generation
3. ✅ Test day adjustments
4. ✅ Verify calculations are correct
5. 🚀 Deploy to production with HTTPS and secure API key

## Additional Resources

- Server API documentation: See `CLAUDE.md` in the repository
- Testing guide: See `TESTING.md`
- Android setup: See `ANDROID_API_SETUP.md`

## Support

If you encounter issues:
1. Check server logs: `./gradlew :server:run` output
2. Check app logs: Use Android Studio Logcat or browser console
3. Verify API key is correct
4. Ensure server and app are on compatible network
