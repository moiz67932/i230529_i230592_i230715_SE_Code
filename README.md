# PipelineX

PipelineX is a browser-based CRM and sales pipeline management system for small B2B outreach teams. It centralizes lead records, rep assignment, pipeline tracking, activity history, follow-up scheduling, dashboards, analytics, and account-security workflows in one role-based Spring Boot application.

The project was built for CS3009 Software Engineering (Spring 2026) by Team PipelineX:

- Muhammad Moiz (23i-0529) - Team Lead
- Muhammad Taha Faisal (23i-0592)
- Adnan Ahmed (23i-0715)

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- Thymeleaf
- HTMX
- Bootstrap
- Flyway
- PostgreSQL
- Spring Modulith
- JUnit, MockMvc, Testcontainers, and Playwright

## Key Features

### Sprint 1: CRM Foundation

- Secure login with Admin and Rep roles.
- Role-based access control for admin-only and rep-only areas.
- Admin user management for creating and managing sales representatives.
- Admin lead management for creating, editing, listing, searching, and validating leads.
- Lead assignment to active reps with assignment history and timestamps.
- Rep-scoped My Leads workspace so reps only see assigned leads.

### Sprint 2: Outreach Workflow

- Pipeline stage management across New, Contacted, Qualified, Proposal, Won, and Lost.
- Stage-change history for auditability.
- Activity logging for calls, emails, and meetings.
- Follow-up scheduling and completion tracking.
- Overdue follow-up views scoped by role.

### Sprint 3: Productization and Hardening

- Admin dashboard with total leads, unassigned leads, overdue follow-ups, recent activity, stage distribution, and source breakdown.
- Rep dashboard with personal workload and assigned-lead metrics.
- Admin-only analytics page for pipeline and source insight.
- Profile management for full name, email, and password updates.
- Forced password-change flow for temporary credentials.
- Request-level protection that blocks temporary-password users from bypassing the password update page.
- Lead archive and restore workflow for cleaner operational lists.

## How to Run

### Prerequisites

Install:

- Java 21
- Maven
- Docker Desktop or Docker Engine
- Node.js and npm, only if you want to run Playwright tests

### 1. Start PostgreSQL

From the project root:

```bash
docker compose up -d
```

The included `compose.yaml` exposes PostgreSQL on host port `5433`, and `application.yml` now defaults to that same port.

### 2. Start the Application

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080/login
```

Flyway applies the schema and demo seed data automatically.

If your terminal already has old database environment variables set, clear them before running the app. For Windows PowerShell:

```powershell
Remove-Item Env:DATABASE_URL -ErrorAction SilentlyContinue
Remove-Item Env:DATABASE_USERNAME -ErrorAction SilentlyContinue
Remove-Item Env:DATABASE_PASSWORD -ErrorAction SilentlyContinue
```

Or explicitly set them to the Docker Compose database:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5433/pipelinex"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="postgres"
```

## Demo Accounts

All seeded accounts use this password:

```text
password
```

| Role | Email |
| --- | --- |
| Admin | `admin@pipelinex.local` |
| Rep | `rep.noah@pipelinex.local` |
| Rep | `rep.layla@pipelinex.local` |
| Inactive Rep | `rep.inactive@pipelinex.local` |

The seeded active rep accounts require a password change after first login, demonstrating the forced-password security workflow.

## Run Tests

Run the Java test suite:

```bash
mvn test
```

The project includes unit, security, repository, and service/integration tests. Testcontainers-backed PostgreSQL tests run when Docker is available and are skipped automatically when Docker is not available.

## Run Playwright E2E Tests

Install Node dependencies:

```bash
npm install
```

Start the Spring Boot app, then run:

```bash
PLAYWRIGHT_BASE_URL=http://localhost:8080 npm run test:e2e
```

On Windows PowerShell:

```powershell
$env:PLAYWRIGHT_BASE_URL="http://localhost:8080"
npm run test:e2e
```

## Project Documentation

Assignment and deliverable material is included in the repository:

- `docs/Deliverable3_Assignment.md`
- `docs/PRD.docx`
- `docs/pdf_text/` for extracted text from the attached assignment and deliverable PDFs
- Root-level assignment PDF files for Deliverables 1, 2, and 3

## Architecture Notes

- The app follows a modular-monolith structure under `com.pipelinex`.
- Spring Security handles authentication, authorization, login redirects, and forced-password enforcement.
- Flyway migrations in `src/main/resources/db/migration` own schema creation and demo data.
- Thymeleaf templates render the role-specific admin and rep workflows.
- PostgreSQL is the main persistence layer; JPA schema generation is set to `validate` so the database stays migration-driven.
