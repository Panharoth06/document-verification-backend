# Document Verification API

## Quick Start with Docker

Requirements:
- Docker 20+
- Docker Compose plugin (or docker-compose v1)

```bash
# 1. Clone the repo
git clone https://github.com/yourusername/your-repo.git
cd your-repo

# 2. (optional) Customize passwords / db name in docker-compose.yml

# 3. Start everything
docker compose up -d --build

# 4. Check logs
docker compose logs backend -f

# API should now be running at: http://localhost:8080
# Swagger/OpenAPI (if enabled): http://localhost:8080/swagger-ui.html