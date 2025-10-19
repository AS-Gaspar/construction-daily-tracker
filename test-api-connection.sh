#!/bin/bash

# Construction Daily Tracker - API Connection Test Script
# Tests the API connection with the configured API key

echo "🧪 Testing API Connection"
echo "========================="
echo ""

# Load API key from .env.local if it exists
if [ -f ".env.local" ]; then
    export $(cat .env.local | grep -v '^#' | grep API_KEY | xargs)
    echo "✓ Loaded API key from .env.local"
else
    API_KEY="change-me-in-production"
    echo "⚠️  Using default API key: $API_KEY"
fi

echo "API Key: ${API_KEY:0:8}..."
echo ""

# Test 1: Check if server is running (root endpoint - no auth required)
echo "Test 1: Checking if server is running..."
RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:8080/ 2>/dev/null)
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✓ Server is running"
    echo "  Response: $BODY"
else
    echo "✗ Server is not responding (HTTP $HTTP_CODE)"
    echo "  Make sure to start the server first:"
    echo "  ./start-server.sh"
    exit 1
fi

echo ""

# Test 2: Test authenticated endpoint without API key (should fail)
echo "Test 2: Testing endpoint without API key (should fail)..."
RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:8080/works 2>/dev/null)
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)

if [ "$HTTP_CODE" = "401" ]; then
    echo "✓ Authentication is working (rejected request without API key)"
else
    echo "⚠️  Unexpected response: HTTP $HTTP_CODE"
fi

echo ""

# Test 3: Test authenticated endpoint with correct API key
echo "Test 3: Testing endpoint with API key..."
RESPONSE=$(curl -s -w "\n%{http_code}" -H "X-API-Key: $API_KEY" http://localhost:8080/works 2>/dev/null)
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✓ API key authentication successful!"
    echo "  Response: $BODY"
    echo ""
    echo "✅ All tests passed! Your API is ready to use."
    echo ""
    echo "📱 Mobile App Configuration:"
    echo "   Server URL: http://10.0.2.2:8080 (for Android emulator)"
    echo "              OR"
    echo "              http://YOUR_COMPUTER_IP:8080 (for physical device)"
    echo "   API Key: $API_KEY"
else
    echo "✗ Authentication failed (HTTP $HTTP_CODE)"
    echo "  Response: $BODY"
    echo ""
    echo "Troubleshooting:"
    echo "1. Make sure the API key in .env.local matches what you're using"
    echo "2. Check if the server loaded the correct environment variable"
    echo "3. Verify the server was started with: ./start-server.sh"
fi

echo ""
