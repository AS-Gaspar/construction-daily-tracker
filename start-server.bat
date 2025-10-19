@echo off
REM Construction Daily Tracker - Server Startup Script (Windows)
REM This script loads environment variables from .env.local and starts the server

echo.
echo Construction Daily Tracker Server
echo ======================================
echo.

REM Check if .env.local exists
if exist ".env.local" (
    echo Loading environment variables from .env.local

    REM Read .env.local and set environment variables
    for /f "usebackq tokens=1,2 delims==" %%a in (".env.local") do (
        if not "%%a"=="" if not "%%a:~0,1%"=="#" (
            set "%%a=%%b"
        )
    )

    echo API Key configured
) else (
    echo .env.local not found, using default API key
    set API_KEY=change-me-in-production
)

echo.
echo Starting server on http://0.0.0.0:8080
echo Press Ctrl+C to stop
echo.

gradlew.bat :server:run
