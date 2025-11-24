## SMTP Environment Setup

The notification service requires valid SMTP credentials at runtime so that the
`/actuator/health` check passes (`mail` indicator). Provide the following
variables before starting Docker Compose:

```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=notifications@transport.com
```

### Option 1 — `.env` file (recommended)

1. Duplicate this snippet into a new `.env` file at the project root (same
   folder as `docker-compose.yml`).  
2. Replace the placeholder values with your SMTP provider details.  
   - Gmail users must create an **App Password** if 2FA is enabled.  
3. Save the file and run `docker-compose up -d ...`. Docker Compose loads the
   `.env` automatically.

> Keep the `.env` out of version control; do not commit real credentials.

### Option 2 — Export variables in the shell

PowerShell:

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-email@example.com"
$env:MAIL_PASSWORD="your-app-password"
$env:MAIL_FROM="notifications@transport.com"
docker-compose up -d notification-service
```

Git Bash / WSL:

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@example.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM=notifications@transport.com
docker-compose up -d notification-service
```

After supplying the credentials, restart the stack:

```powershell
docker-compose down
docker-compose up -d service-registry zookeeper kafka postgres-notification notification-service
```

Then confirm:

```powershell
docker exec notification-service curl -s http://localhost:8086/actuator/health
```

`status` should now report `UP` and the container will become `healthy`.

