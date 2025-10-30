# Android API Setup Guide

This guide explains how to securely store and use the API key in your Android application.

## 🔒 Security Features

The API key is stored using **Android's EncryptedSharedPreferences** with:
- **AES256-GCM** encryption for data
- **Android Keystore** for key management
- Hardware-backed security (when available)

## 📱 Quick Start

### 1. Configure the Android Manifest

Add the Application class to your `AndroidManifest.xml`:

```xml
<application
    android:name=".MainApplication"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    ...>
```

### 2. Set Up API Credentials

On first launch, users need to configure:
- **Server URL**: The address of your API server
- **API Key**: The secret key for authentication

Example URLs:
- Physical device: `http://192.168.1.100:8080`
- Android Emulator: `http://10.0.2.2:8080` (maps to host machine's localhost)
- Production: `https://your-api-server.com`

### 3. Basic Usage

```kotlin
// In your Activity or ViewModel
val apiKeyManager = MainApplication.getApiKeyManager()

// Check if configured
if (!apiKeyManager.hasApiKey()) {
    // Navigate to settings screen
    navigateToSettings()
} else {
    // Create authenticated HTTP client
    val client = ApiClient.create(
        baseUrl = apiKeyManager.getServerUrl(),
        apiKey = apiKeyManager.getApiKey()!!
    )

    // Use with repositories
    val workRepository = WorkRepository(client)
    val works = workRepository.getAllWorks()
}
```

## 📂 Project Structure

```
composeApp/src/
├── androidMain/kotlin/.../
│   ├── data/
│   │   └── ApiKeyManager.kt          # Secure storage manager
│   ├── MainApplication.kt            # App initialization
│   └── Example_ApiUsage.kt           # Complete usage examples
│
└── commonMain/kotlin/.../
    ├── network/
    │   ├── ApiClient.kt              # HTTP client configuration
    │   └── WorkRepository.kt         # Example API repository
    └── ui/
        └── SettingsScreen.kt         # Settings UI for API config
```

## 🔧 Implementation Steps

### Step 1: Initialize ApiKeyManager

The `MainApplication` class automatically initializes the `ApiKeyManager`:

```kotlin
class MainApplication : Application() {
    lateinit var apiKeyManager: ApiKeyManager

    override fun onCreate() {
        super.onCreate()
        apiKeyManager = ApiKeyManager(this)
    }
}
```

### Step 2: Create Settings Screen

Use the provided `SettingsScreen` composable:

```kotlin
@Composable
fun MySettingsScreen() {
    val apiKeyManager = MainApplication.getApiKeyManager()

    SettingsScreen(
        currentServerUrl = apiKeyManager.getServerUrl(),
        currentApiKey = apiKeyManager.getApiKey() ?: "",
        onSave = { serverUrl, apiKey ->
            apiKeyManager.saveServerUrl(serverUrl)
            apiKeyManager.saveApiKey(apiKey)
            // Navigate back or show success message
        },
        onBack = { /* Navigate back */ }
    )
}
```

### Step 3: Create ViewModel with Repository

```kotlin
class WorksViewModel(apiKeyManager: ApiKeyManager) : ViewModel() {
    private val client = ApiClient.create(
        baseUrl = apiKeyManager.getServerUrl(),
        apiKey = apiKeyManager.getApiKey() ?: ""
    )

    private val repository = WorkRepository(client)

    fun loadWorks() = viewModelScope.launch {
        val works = repository.getAllWorks()
        // Update state...
    }
}
```

### Step 4: Use in Composable

```kotlin
@Composable
fun WorksScreen() {
    val apiKeyManager = MainApplication.getApiKeyManager()
    val viewModel = remember { WorksViewModel(apiKeyManager) }

    LaunchedEffect(Unit) {
        viewModel.loadWorks()
    }

    // Display UI...
}
```

## 🛠️ API Key Manager Methods

### Storing Credentials

```kotlin
val apiKeyManager = ApiKeyManager(context)

// Save API key
apiKeyManager.saveApiKey("your-secret-api-key")

// Save server URL
apiKeyManager.saveServerUrl("http://192.168.1.100:8080")
```

### Retrieving Credentials

```kotlin
// Get API key (returns null if not set)
val apiKey: String? = apiKeyManager.getApiKey()

// Get server URL (returns default if not set)
val serverUrl: String = apiKeyManager.getServerUrl()

// Check if configured
val isConfigured: Boolean = apiKeyManager.hasApiKey()
```

### Clearing Credentials

```kotlin
// Clear all stored data (logout)
apiKeyManager.clearAll()
```

## 🌐 HTTP Client Usage

### Creating a Client

```kotlin
val client = ApiClient.create(
    baseUrl = "http://192.168.1.100:8080",
    apiKey = "your-api-key"
)
```

The client automatically:
- Adds `X-API-Key` header to all requests
- Serializes/deserializes JSON
- Sets 30-second timeouts
- Prepends base URL to all requests

### Making API Calls

```kotlin
// GET request
val works: List<Work> = client.get("/works").body()

// POST request
val newWork: Work = client.post("/works") {
    setBody(mapOf("name" to "New Work"))
}.body()

// PUT request
val updated: Work = client.put("/works/1") {
    setBody(mapOf("name" to "Updated Name"))
}.body()

// DELETE request
client.delete("/works/1")
```

## 📱 Testing on Different Environments

### Android Emulator
```kotlin
serverUrl = "http://10.0.2.2:8080"  // Maps to host machine's localhost
```

### Physical Device (Same Network)
```kotlin
serverUrl = "http://192.168.1.100:8080"  // Use computer's local IP
```

Find your computer's IP:
- **Windows**: `ipconfig` (look for IPv4 Address)
- **macOS/Linux**: `ifconfig` or `ip addr` (look for inet)

### Production
```kotlin
serverUrl = "https://your-api-server.com"  // Always use HTTPS in production
```

## 🔐 Security Best Practices

### ✅ DO
- Use HTTPS in production
- Rotate API keys periodically
- Clear credentials on logout
- Handle network errors gracefully
- Test on real devices

### ❌ DON'T
- Hardcode API keys in source code
- Store API keys in plain text
- Commit API keys to version control
- Share API keys between users
- Use HTTP in production

## 🚀 Example: Complete Flow

```kotlin
// 1. Check if API is configured
val apiKeyManager = MainApplication.getApiKeyManager()

if (!apiKeyManager.hasApiKey()) {
    // Show settings screen
    ShowSettingsScreen()
} else {
    // 2. Create client
    val client = ApiClient.create(
        baseUrl = apiKeyManager.getServerUrl(),
        apiKey = apiKeyManager.getApiKey()!!
    )

    // 3. Create repository
    val workRepository = WorkRepository(client)

    // 4. Make API call
    try {
        val works = workRepository.getAllWorks()
        println("Loaded ${works.size} works")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        // Maybe credentials are wrong, show settings again
    }

    // 5. Clean up when done
    client.close()
}
```

## 🐛 Troubleshooting

### "Connection refused" error
- Check if server is running
- Verify the server URL is correct
- For emulator, use `10.0.2.2` instead of `localhost`
- Check firewall settings

### "401 Unauthorized" error
- Verify the API key is correct
- Check if API key matches server configuration
- Ensure `X-API-Key` header is being sent

### "SSL/TLS" errors (HTTPS)
- Use proper SSL certificates in production
- For development, use HTTP instead
- Trust custom certificates if needed

### App crashes on API call
- Check if API key is set before creating client
- Wrap API calls in try-catch blocks
- Ensure network permissions in AndroidManifest

## 📚 Additional Resources

- See `Example_ApiUsage.kt` for complete examples
- Check `SettingsScreen.kt` for UI implementation
- Review `ApiKeyManager.kt` for storage details
- Read `ApiClient.kt` for HTTP configuration

## 🔄 Migration Guide (If You Had Custom Implementation)

If you were storing credentials differently:

```kotlin
// Old approach (insecure)
val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
val apiKey = prefs.getString("api_key", "")

// New approach (secure)
val apiKeyManager = ApiKeyManager(context)
val apiKey = apiKeyManager.getApiKey()

// Migrate data
apiKeyManager.saveApiKey(oldApiKey)
```

---

**Questions?** Check the example files in the project or review the CLAUDE.md file.
