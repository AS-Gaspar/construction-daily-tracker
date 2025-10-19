# Environment Variables Setup

This document describes the environment variables required to run the Construction Daily Tracker server.

## Required Environment Variables

### API_KEY (Required for Production)
**Description**: The secret key used to authenticate API requests.

**Default**: `"change-me-in-production"` (insecure, for development only)

**How to set**:
```bash
# Generate a secure random key
openssl rand -base64 32

# Set the environment variable
export API_KEY="your-generated-key-here"
```

**Important**:
- ⚠️ NEVER commit this value to git
- ⚠️ Change the default value before deploying to production
- ✅ Use a cryptographically secure random string (min 32 characters)
- ✅ Store securely in your deployment environment

---

### Database Configuration (Optional)

#### DB_URL
**Description**: PostgreSQL database connection URL

**Default**: `"jdbc:postgresql://localhost:5432/construction_tracker"`

**Example**:
```bash
export DB_URL="jdbc:postgresql://your-db-host:5432/construction_tracker"
```

#### DB_USER
**Description**: Database username

**Default**: `"postgres"`

**Example**:
```bash
export DB_USER="your-db-username"
```

#### DB_PASSWORD
**Description**: Database password

**Default**: `"postgres"` (insecure, for development only)

**Example**:
```bash
export DB_PASSWORD="your-secure-db-password"
```

**Important**:
- ⚠️ NEVER commit database credentials to git
- ⚠️ Use strong passwords in production
- ✅ Consider using connection pooling credentials with limited permissions

---

## Setting Up for Development

### Local Development (Unix/macOS/Linux)

Create a `.env.local` file (already in .gitignore):
```bash
export API_KEY="dev-test-key-12345"
export DB_URL="jdbc:postgresql://localhost:5432/construction_tracker"
export DB_USER="postgres"
export DB_PASSWORD="postgres"
```

Load it before running:
```bash
source .env.local
./gradlew :server:run
```

### Local Development (Windows)

Create a `env.local.bat` file:
```batch
set API_KEY=dev-test-key-12345
set DB_URL=jdbc:postgresql://localhost:5432/construction_tracker
set DB_USER=postgres
set DB_PASSWORD=postgres
```

Run it before starting server:
```batch
env.local.bat
gradlew.bat :server:run
```

---

## Setting Up for Production

### Docker
```yaml
# docker-compose.yml
services:
  api:
    environment:
      - API_KEY=${API_KEY}
      - DB_URL=jdbc:postgresql://db:5432/construction_tracker
      - DB_USER=${DB_USER}
      - DB_PASSWORD=${DB_PASSWORD}
```

Then create `.env` file (in .gitignore):
```bash
API_KEY=your-production-key-here
DB_USER=construction_user
DB_PASSWORD=your-secure-password
```

### Cloud Providers

#### Heroku
```bash
heroku config:set API_KEY="your-production-key"
heroku config:set DB_URL="jdbc:postgresql://..."
heroku config:set DB_USER="username"
heroku config:set DB_PASSWORD="password"
```

#### AWS (Elastic Beanstalk)
Set via console or CLI:
```bash
aws elasticbeanstalk update-environment \
  --environment-name production \
  --option-settings \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=API_KEY,Value=your-key \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=DB_PASSWORD,Value=your-password
```

#### Google Cloud Run
```bash
gcloud run deploy construction-tracker \
  --set-env-vars API_KEY=your-key,DB_PASSWORD=your-password
```

---

## Security Best Practices

### ✅ DO
- Use strong, random API keys (min 32 characters)
- Rotate API keys periodically
- Use different keys for development/staging/production
- Store credentials in secure secret managers
- Use HTTPS in production
- Limit database user permissions

### ❌ DON'T
- Commit credentials to version control
- Share API keys via email/chat
- Use default/example values in production
- Log API keys or passwords
- Hardcode credentials in source code

---

## Verification

Check that environment variables are set:
```bash
echo $API_KEY
echo $DB_URL
```

Test the server:
```bash
# Start server
./gradlew :server:run

# Test API (should fail without key)
curl http://localhost:8080/works

# Test API (should succeed with key)
curl -H "X-API-Key: your-api-key" http://localhost:8080/works
```

---

## Troubleshooting

### "Using default API key" warning
- Set `API_KEY` environment variable before starting server
- Verify with `echo $API_KEY`

### "Connection refused" database errors
- Check `DB_URL` is correct
- Verify PostgreSQL is running
- Confirm database exists

### "Authentication failed" database errors
- Check `DB_USER` and `DB_PASSWORD` are correct
- Verify user has necessary permissions

---

**For mobile app setup**, see `ANDROID_API_SETUP.md`
