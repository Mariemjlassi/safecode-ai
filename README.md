# SafeCode AI

Security audit tool that scans a Git repository for OWASP ASVS Level 1 vulnerabilities and suggests fixes.

## Project Structure

```
safecode-ai/
├── src/                    # Spring Boot backend (Java 21)
├── pom.xml
├── frontend/               # Angular frontend
│   ├── src/
│   ├── proxy.conf.json     # proxies /api → http://localhost:8081
│   └── package.json
└── README.md
```

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |
| PostgreSQL | 14+ |
| git | any recent |

## Database Setup

```sql
CREATE DATABASE safecode;
```

Update credentials in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/safecode
spring.datasource.username=postgres
spring.datasource.password=mariem
```

## Running the Backend

```bash
# From the project root
mvn spring-boot:run
```

The API will be available at **http://localhost:8081/api**

### Environment variables (optional)

| Variable | Description |
|----------|-------------|
| `SPRING_AI_OPENAI_API_KEY` | OpenAI / watsonx API key for AI-powered fixes |
| `SPRING_AI_OPENAI_BASE_URL` | Base URL (defaults to OpenAI) |
| `SPRING_AI_OPENAI_MODEL` | Model name (default: `gpt-4o-mini`) |
| `SAFECODE_CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins (default: `http://localhost:4200`) |

To run without an AI key (mock mode):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mock-ai
```

## Running the Frontend

```bash
cd frontend
npm install
npm start
```

The Angular dev server starts at **http://localhost:4200** and proxies `/api/**` calls to the backend on port 8081.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/audit` | Run a new audit on a Git repo |
| `GET` | `/api/audit/{id}` | Retrieve a previous audit by ID |
| `POST` | `/api/audit/{id}/fix` | Get fix suggestion for a finding (by `findingId` ref) |
| `POST` | `/api/audit/{id}/fix/{findingId}` | Apply AI fix and re-validate (Secrets Handling only) |

### Example — Run an audit

```bash
curl -X POST http://localhost:8081/api/audit \
  -H "Content-Type: application/json" \
  -d '{"repoUrl": "https://github.com/WebGoat/WebGoat"}'
```
