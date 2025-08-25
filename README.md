<p>
  <a href="https://github.com/coreyyy34/uni-tracker.nz/actions/workflows/ci.yml?query=branch%3Amain">
    <img src="https://img.shields.io/github/actions/workflow/status/coreyyy34/uni-tracker.nz/ci.yml?branch=main&label=CI%20%7C%20main&logo=github&style=flat-square" alt="CI - main">
  </a>
  <a href="https://github.com/coreyyy34/uni-tracker.nz/actions/workflows/ci.yml?query=branch%3Adev">
    <img src="https://img.shields.io/github/actions/workflow/status/coreyyy34/uni-tracker.nz/ci.yml?branch=dev&label=CI%20%7C%20dev&logo=github&style=flat-square" alt="CI - dev">
  </a>
</p>

![test](frontend/public/logo-long.svg)

Uni-Tracker is a full-stack web application designed to help students organize and track their studies. Built with a Kotlin Spring Boot backend and a Next.js TypeScript frontend, it offers a clean, responsive interface to manage courses, monitor assignment deadlines, and organize study materials like notes and links in one place.

## Project Structure

```
Uni-Tracker/
├── backend/            # Kotlin Spring Boot backend services
│   ├── shared/         # Shared utilities, models, and configurations
│   ├── auth/           # Authentication service
│   ├── api/            # API service
│   └── README.md       # Backend documentation
```

## Prerequisites

- Java JDK 24+ (for backend)
- Gradle (for backend builds)

## Running the Backend

Navigate to the `backend` directory:

```bash
cd backend
```

### Start Services

- **Auth Service**:
  ```bash
  ./gradlew auth:bootRun
  ```

- **API Service**:
  ```bash
  ./gradlew api:bootRun
  ```

### Running Tests

- **Shared Module**:
  ```bash
  ./gradlew shared:test
  ```

- **Auth Service**:
  ```bash
  ./gradlew auth:test
  ```

- **API Service**:
  ```bash
  ./gradlew api:test
  ```

### Building JARs

To package the Auth and API services:

```bash
./gradlew api:bootJar auth:bootJar
```

Output JARs are located in:
- `backend/api/build/libs/`
- `backend/auth/build/libs/`
