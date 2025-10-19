# Construction Daily Tracker - UI Guide

## Overview

The Android UI has been successfully created with all major screens and functionality for managing construction work tracking, payroll, and employees.

## Screens Implemented

### 1. **Home Screen** (`HomeScreen.kt`)
- Dashboard with menu cards for easy navigation
- Quick access to all main features
- Clean, card-based interface

### 2. **Settings Screen** (`SettingsScreen.kt`)
- Configure API server URL and API key
- Secure API key input with show/hide toggle
- Initial setup screen when app first launches
- Example values provided for emulator and physical devices

### 3. **Employees** (`EmployeeListScreen.kt`, `EmployeeDetailScreen.kt`)
- List all employees with their roles, works, and daily rates
- Add new employees
- Edit existing employees (TODO: implement update API call)
- Empty state with helpful messages
- Validation for all required fields

### 4. **Works Management** (`WorksScreen.kt`)
- List all construction sites
- Add new construction sites
- Simple dialog-based creation
- Edit works (TODO: implement update API call)

### 5. **Roles Management** (`RolesScreen.kt`)
- List all job roles (Carpenter, Electrician, etc.)
- Add new roles
- Simple dialog-based creation
- Edit roles (TODO: implement update API call)

### 6. **Daily Adjustments** (`DayAdjustmentsScreen.kt`)
- Track daily work adjustments
- Add positive adjustments (+1 for Saturday work)
- Add negative adjustments (-0.5 for half-day absence)
- Color-coded cards (green for positive, red for negative)
- Sorted by date (most recent first)
- Notes field for additional context

### 7. **Payroll** (`PayrollScreen.kt`)
- View all monthly payrolls
- Grouped by payroll period (6th to 5th)
- Generate new payroll for a period
- Shows base workdays, final worked days, and total payment
- Closed/Open status indicator
- Period totals

## Navigation

- **Simple state-based navigation** using `NavigationState`
- No external dependencies required
- Back button support on all screens
- Type-safe screen definitions in `Screen.kt`

## Architecture

### ViewModel (`AppViewModel.kt`)
- Centralized state management
- Handles all API calls via repositories
- Loading states and error handling
- Dialog state management

### Repositories (`Repositories.kt`, `WorkRepository.kt`)
- Clean API abstraction
- Ktor HTTP client with automatic API key injection
- Typed request/response handling
- Repositories for:
  - Works
  - Roles
  - Employees
  - Day Adjustments
  - Payrolls

### Network Layer (`ApiClient.kt`)
- Configured Ktor client
- Automatic API key header injection
- JSON serialization/deserialization
- Timeout configuration

## How to Run

### 1. Start the Server
```bash
./gradlew :server:run
```

### 2. Run on Android Emulator

**Option A: Android Studio**
1. Open project in Android Studio
2. Create/start an emulator (API 35)
3. Select `composeApp` configuration
4. Click Run

**Option B: Command Line**
```bash
# Build and install
./gradlew :composeApp:installDebug

# APK location
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### 3. First-Time Setup
1. App will show Settings screen on first launch
2. Configure server URL:
   - **For emulator**: `http://10.0.2.2:8080`
   - **For physical device**: `http://192.168.x.x:8080` (your computer's IP)
3. Enter API key from `.env.local`:
   ```
   c3837ce4f3a62a5cafa92c99db47e26b0e9acaeeb0fc890b54b0060976217e5c
   ```
4. Click "Save Settings"

### 4. Using the App
1. **Add Works and Roles first** (required for employees)
2. **Add Employees** with their daily rates
3. **Track Daily Work** by adding adjustments
4. **Generate Payroll** for a period (6th to 5th)

## Features

### Implemented ✅
- Complete UI for all major screens
- API integration with Ktor
- State management with ViewModel
- Navigation system
- Loading states and error handling
- Form validation
- Empty states with helpful messages
- Dialogs for quick actions
- Settings persistence (ready for platform-specific storage)

### TODO / Future Enhancements 🔨
- [ ] Edit functionality for Works and Roles
- [ ] Update employee functionality
- [ ] Delete operations
- [ ] Persist API settings (currently resets on app restart)
- [ ] Date picker for adjustments and payroll
- [ ] Search/filter employees
- [ ] Payroll export functionality
- [ ] Employee work history
- [ ] Dashboard statistics
- [ ] Pull-to-refresh
- [ ] Error snackbars/toasts
- [ ] Confirmation dialogs for destructive actions
- [ ] Dark theme support

## Known Issues

### Deprecation Warnings (non-critical)
- `Divider` renamed to `HorizontalDivider` (Material3)
- `menuAnchor()` needs updated parameters
- These don't affect functionality, just need API updates

### API Persistence
- Settings need to be re-entered after app restart
- Need to implement platform-specific storage:
  - Android: Already has `ApiKeyManager` with EncryptedSharedPreferences
  - Integration pending

## File Structure

```
composeApp/src/commonMain/kotlin/org/gaspar/construction_daily_tracker/
├── App.kt                          # Main app entry point
├── AppViewModel.kt                 # Central state management
├── navigation/
│   ├── NavigationState.kt         # Navigation state holder
│   └── Screen.kt                  # Screen definitions
├── network/
│   ├── ApiClient.kt               # HTTP client configuration
│   ├── Repositories.kt            # Data repositories
│   └── WorkRepository.kt          # Work repository example
└── ui/
    ├── HomeScreen.kt              # Dashboard
    ├── SettingsScreen.kt          # API configuration
    └── screens/
        ├── EmployeeListScreen.kt
        ├── EmployeeDetailScreen.kt
        ├── DayAdjustmentsScreen.kt
        ├── PayrollScreen.kt
        ├── WorksScreen.kt
        └── RolesScreen.kt
```

## Testing

The APK has been successfully built and is ready for testing on an emulator or physical device.

**Build output:**
```
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

## Next Steps

1. Test the app on an emulator/device
2. Implement remaining TODO items (edit/delete operations)
3. Connect to actual API endpoints and verify data flow
4. Add platform-specific settings persistence
5. Implement date pickers for better UX
6. Add error handling UI (snackbars/toasts)
7. Consider adding more advanced features (search, filters, export)

---

**Generated:** 2025-10-19
**Status:** ✅ All major screens implemented and building successfully
