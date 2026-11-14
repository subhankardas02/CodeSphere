# CodeSphere

CodeSphere is a browser-based C++ code execution platform I'm building using Java and Spring Boot.

The idea is simple: write code in the browser, provide custom input, submit it, and get the output without needing a local C++ compiler.

I built the backend around an asynchronous execution system where submitted programs are handled by background workers and executed inside Docker containers.

## Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** React, TypeScript, Monaco Editor
- **Database:** PostgreSQL
- **Queue & Job Management:** Redis
- **Code Execution:** Docker, GCC

## What I've Built

- A browser-based code editor using Monaco Editor.
- C++ code submission with custom standard input.
- Compilation and execution inside Docker containers.
- Execution timeout handling and error reporting.
- An asynchronous job queue using Redis.
- Multiple Spring Boot worker processes that can execute different jobs concurrently.
- Execution status tracking and history stored in PostgreSQL.
- Redis-based job leases to detect inactive workers.
- Job recovery when a worker stops unexpectedly.

## How It Works

1. The frontend sends the submitted code and input to the Spring Boot API.
2. The API creates an execution record in PostgreSQL.
3. A job is added to the Redis queue.
4. An available worker picks up the job.
5. The worker starts a Docker container and compiles and runs the C++ program.
6. The result is saved in PostgreSQL.
7. The frontend retrieves the execution status and displays the result.

Multiple workers can process different submissions at the same time. Redis is also used to track worker leases and recover jobs left behind after a worker failure.

## Running Locally

### Requirements

- Java 21+
- Maven
- Node.js and npm
- Docker Desktop

### 1. Clone the repository

```bash
git clone https://github.com/subhankardas02/CodeSphere.git
cd CodeSphere
```

### 2. Start PostgreSQL

```bash
docker run -d --name codesphere-postgres -p 5433:5432 -e POSTGRES_DB=codesphere -e POSTGRES_USER=root -e POSTGRES_PASSWORD=YOUR_PASSWORD -v codesphere_pgdata:/var/lib/postgresql/data postgres:17
```

### 3. Start Redis

```bash
docker run -d --name codesphere-redis -p 6379:6379 redis:7-alpine
```

### 4. Start the backend API

Open a terminal:

```powershell
cd backend

$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_PASSWORD"

mvn spring-boot:run
```

### 5. Start a worker

Open another terminal:

```powershell
cd backend

$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_PASSWORD"

mvn spring-boot:run "-Dspring-boot.run.profiles=worker"
```

You can start additional workers by running the same command in separate terminals.

### 6. Start the frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the local URL printed by Vite.

## Current Status

The basic code execution workflow is working. I've also tested multiple workers processing submissions concurrently and tested job recovery after stopping a worker.

I'm currently working on improving the platform and adding more features.

## What's Next

- Real-time collaborative code editing.
- Better container security and resource limits.
- Running workers across multiple machines.
- Better monitoring of jobs and workers.

## Author

**Subhankar Das**

- [GitHub](https://github.com/subhankardas02)
- [LinkedIn](https://www.linkedin.com/in/subhankardas02/)