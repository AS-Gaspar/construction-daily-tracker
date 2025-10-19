#!/bin/bash

# Construction Daily Tracker - Server Startup Script
# This script loads environment variables from .env.local and starts the server

echo "🏗️  Construction Daily Tracker Server"
echo "======================================"
echo ""

# Check if .env.local exists
if [ -f ".env.local" ]; then
    echo "✓ Loading environment variables from .env.local"
    export $(cat .env.local | grep -v '^#' | xargs)
else
    echo "⚠️  .env.local not found, using default API key"
    export API_KEY="change-me-in-production"
fi

echo "✓ API Key configured: ${API_KEY:0:8}..."
echo ""
echo "Starting server on http://0.0.0.0:8080"
echo "Press Ctrl+C to stop"
echo ""

./gradlew :server:run
