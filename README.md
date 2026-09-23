# CleanArchitecture.DDD.Java

A Java/Spring Boot port of [`mustafizurrohman/CleanArchitecture.DDD`](https://github.com/mustafizurrohman/CleanArchitecture.DDD).

The source repository is a .NET reference implementation demonstrating Onion/Clean Architecture, DDD/value objects, Entity Framework persistence, MediatR pipelines, Polly resiliency, Swagger/OpenAPI, Hangfire jobs, validation, structured logging, health checks, and convention-based dependency injection. This port preserves those architectural ideas while translating them into idiomatic Spring rather than mechanically copying .NET APIs.

## Runtime baseline

| Component | Version | Notes |
|---|---:|---|
| Spring Boot | **4.1.1** | Current stable Spring Boot release when this port was produced |
| Java | **26** | Newest Java version officially supported by Spring Boot 4.1.1 |
| Java 27 | Experimental profile | Java 27 is the newest Java SE release, but Boot 4.1.1 documents compatibility only through Java 26 |
| springdoc-openapi | **3.1.1** | Boot 4 OpenAPI/Swagger UI support |
| Resilience4j | **2.4.0** | Retry and circuit breaker |
| JobRunr | **8.8.2** | Persistent background jobs |
| ArchUnit | **1.5.0** | Architecture enforcement; supports Java 27 class files |
| PostgreSQL | **18** | Optional production-style database profile |

The default Maven build targets Java 26. To try Java 27 anyway:

```bash
mvn -Pjava-27-experimental clean verify
```

## Architecture

```text
                          ┌───────────────────────────┐
                          │ cleanarchitecture-api     │
                          │ REST / Problem Details   │
                          └─────────────┬─────────────┘
                                        │
                         depends on     │
                          ┌─────────────▼─────────────┐
                          │ cleanarchitecture-        │
                          │ application               │
                          │ commands / queries / ports│
                          └─────────────┬─────────────┘
                                        │
                          ┌─────────────▼─────────────┐
                          │ cleanarchitecture-domain  │
                          │ aggregates / value objects│
                          └─────────────┬─────────────┘
                                        │
                          ┌─────────────▼─────────────┐
                          │ cleanarchitecture-core    │
                          │ shared kernel             │
                          └───────────────────────────┘

 cleanarchitecture-infrastructure ──► application ──► domain ──► core
              ▲
              │
             api
```

`core` and `domain` are framework-free. Spring, JPA, HTTP, resilience, and background-job concerns stay in the outer modules.

## .NET → Java/Spring mapping

| Source implementation concept | Java/Spring implementation |
|---|---|
| Onion / Clean Architecture | Maven modules + ports/adapters + ArchUnit dependency rules |
| DDD entities / aggregates | `Customer` aggregate root |
| `ValueOf` / strongly typed IDs | Java records such as `CustomerId` and `Email` |
| Domain events | Framework-free events + Spring event publisher adapter |
| Entity Framework Core | Spring Data JPA + Hibernate |
| EF global filters | Hibernate `@SQLRestriction` soft-delete filter |
| EF value converters | JPA `AttributeConverter<Email, String>` |
| EF optimistic concurrency | JPA `@Version` |
| FluentValidation | Jakarta Bean Validation in the mediator pipeline and API boundary |
| MediatR | Application `Mediator` abstraction + Spring adapter |
| MediatR transaction behavior | `TransactionTemplate` around commands and read-only queries |
| MediatR performance behavior | Timed mediator dispatch with slow-use-case logging |
| AutoMapper | Explicit mapper methods (`CustomerView.from`, persistence mappings) to keep mappings visible and type-safe |
| Polly | Resilience4j retry + circuit breaker |
| `IHttpClientFactory` | Spring `RestClient` with JDK HTTP client timeouts |
| Swagger / Swashbuckle | springdoc-openapi 3 + Swagger UI |
| Serilog structured logging | SLF4J/MDC + Spring Boot Logstash structured logging |
| Hangfire | JobRunr Spring Boot 4 starter |
| ProblemDetails middleware | Spring MVC `ProblemDetail` + `@RestControllerAdvice` |
| Health checks | Spring Boot Actuator liveness/readiness + Prometheus endpoint |
| Scrutor | Spring component scanning + explicit configuration beans |

## Modules

- **`cleanarchitecture-core`** — shared kernel: `AggregateRoot`, `DomainEvent`, domain exceptions, `PageResult`.
- **`cleanarchitecture-domain`** — framework-free customer aggregate, IDs/value objects, status and domain events.
- **`cleanarchitecture-application`** — CQRS commands/queries, handlers, DTOs, mediator contracts and output ports.
- **`cleanarchitecture-infrastructure`** — JPA adapter, mediator implementation, transaction boundaries, domain-event publishing, Resilience4j HTTP adapter and JobRunr jobs.
- **`cleanarchitecture-api`** — Spring Boot entry point, REST controllers, request validation, OpenAPI, correlation IDs and RFC-style Problem Details.
- **`code-integrity-tests`** — ArchUnit rules enforcing dependency direction.

## Implemented behavior

The sample domain intentionally stays small because the source project itself uses simplified use cases to demonstrate infrastructure patterns. The Java port provides:

- create, fetch, list, change-email, deactivate and soft-delete customer use cases;
- normalized/validated `Email` and strongly typed `CustomerId` value objects;
- domain events that are published by application handlers;
- an after-commit `CustomerCreated` listener that enqueues a persistent JobRunr welcome job;
- JPA optimistic locking and Hibernate soft-delete filtering;
- H2 for zero-setup local development and PostgreSQL 18 via a profile;
- resilient outbound HTTP with a retry, circuit breaker, fallback and explicit timeouts;
- request correlation IDs propagated into structured JSON logging;
- consistent 400/404/409/422 Problem Details responses;
- Actuator health/liveness/readiness/metrics/Prometheus endpoints;
- unit, integration and architectural tests.

## Build

Prerequisites:

- JDK 26
- Maven 3.6.3+

Run the complete verification suite:

```bash
mvn clean verify
```

The build includes domain tests, application handler tests, Spring Boot integration tests, and ArchUnit dependency checks.

## Run locally with H2

Package the application:

```bash
mvn clean package
```

Then run:

```bash
java -jar cleanarchitecture-api/target/cleanarchitecture-api-1.0.0-SNAPSHOT.jar
```

H2 is in-memory, so no database setup is required.

Useful endpoints:

- API: `http://localhost:8080/api/v1/customers`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`
- Liveness: `http://localhost:8080/actuator/health/liveness`
- Readiness: `http://localhost:8080/actuator/health/readiness`
- Prometheus: `http://localhost:8080/actuator/prometheus`
- JobRunr dashboard: `http://localhost:8000`

## Run with PostgreSQL 18

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Then run the packaged application with the PostgreSQL profile:

```bash
SPRING_PROFILES_ACTIVE=postgres \
java -jar cleanarchitecture-api/target/cleanarchitecture-api-1.0.0-SNAPSHOT.jar
```

Optional overrides:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

The Compose file uses PostgreSQL 18's `/var/lib/postgresql` volume location.

## Example calls

Create a customer:

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ada Lovelace","email":"ada@example.com"}'
```

List customers:

```bash
curl 'http://localhost:8080/api/v1/customers?page=0&size=20'
```

Change an email:

```bash
curl -X PUT http://localhost:8080/api/v1/customers/{id}/email \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada.lovelace@example.com"}'
```

Deactivate:

```bash
curl -X PATCH http://localhost:8080/api/v1/customers/{id}/deactivate
```

Soft-delete:

```bash
curl -X DELETE http://localhost:8080/api/v1/customers/{id}
```

Exercise the resilient outbound HTTP adapter:

```bash
curl http://localhost:8080/api/v1/diagnostics/outbound
```

## CI

`.github/workflows/ci.yml` runs the supported Java 26 build on every push and pull request. It also runs a non-blocking Java 27 compatibility job using the experimental Maven profile.

## Design decisions

This port deliberately avoids putting Spring annotations in the domain. Application handlers are constructed as ordinary Java classes and wired in the infrastructure configuration. The mediator owns validation, transaction boundaries and timing, so controllers remain thin and use cases can be unit-tested without a Spring context.

Persistence uses separate JPA entities rather than annotating domain aggregates. On updates the adapter mutates the existing managed JPA entity so its `@Version` value is preserved; rebuilding a fresh entity on each save would undermine optimistic locking.

Domain events are published inside the command transaction, while the JobRunr listener uses `AFTER_COMMIT`. A background job is therefore queued only after the database transaction succeeds.
