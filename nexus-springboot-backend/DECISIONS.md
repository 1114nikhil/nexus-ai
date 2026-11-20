# Architecture Decision Record (ADR)

## ADR-001: Modular Monolith with Clean Architecture
**Date:** 2026-11-17
**Status:** Accepted
**Context:** Building Nexus AI as a new project. Microservices-first risks drawing wrong boundaries before the domain is understood.
**Decision:** Start as a modular monolith (Gradle multi-module) with Clean Architecture layering (domain, application, infrastructure, api). Extract services later when boundaries are proven.
**Consequences:** Compile-time enforcement of module boundaries; can extract the AI service or ticket service later without rewrite. Costs: slightly more Gradle setup upfront.

## ADR-002: Disable bootJar until Week 3
**Date:** 2026-11-17
**Status:** Accepted (temporary)
**Context:** nexus-api has the Spring Boot plugin applied, but no @SpringBootApplication main class yet.
**Decision:** `tasks.named('bootJar') { enabled = false }` until Week 3.
**Consequences:** `./gradlew build` stays green; remove this block when NexusApplication.java is written.

# Nexus AI — Architecture Decision Record (ADR)

## Week 1: Foundation & Domain

### ADR-001: Modular Monolith over Microservices
**Context:** Building Nexus AI from scratch. Microservices prematurely force distributed system complexity (network calls, eventual consistency, distributed tracing) before domain boundaries are proven.
**Decision:** Build as a Gradle multi-module monolith with Clean Architecture layers. Extract to microservices later when boundaries are stable.
**Consequences:** Compile-time enforcement of boundaries; can extract services later without a rewrite.

### ADR-002: Clean Architecture Module Boundaries
**Context:** Domain logic must be testable without infrastructure and independent of framework choices.
**Decision:**
- `nexus-domain`: Pure Java, zero dependencies (no Spring/JPA).
- `nexus-application`: Use cases and ports (interfaces). Depends only on domain.
- `nexus-infrastructure`: JPA adapters, Flyway, external clients. Implements application ports.
- `nexus-api`: Spring Boot composition root, REST controllers.
  **Consequences:** `Ticket` entity has no `@Entity`. `TicketJpaEntity` lives in infrastructure with `@Entity`.

### ADR-003: UUIDs for Identity, Generated in the Domain
**Context:** Long IDs require DB to generate (`@GeneratedValue`), meaning the entity has no identity until after persistence. UUIDs can be created in the domain before touching the DB.
**Decision:** Generate `UUID.randomUUID()` in `Ticket.create()`. No `@GeneratedValue` on the JPA entity.
**Consequences:** Entities are complete at creation. Safe for future distributed nodes. URLs don't leak record counts.

### ADR-004: Disable `bootJar` temporarily
**Context:** `nexus-api` has the Spring Boot plugin, but the main class (`NexusApplication`) doesn't exist yet.
**Decision:** `tasks.named('bootJar') { enabled = false }` until Week 3.
**Consequences:** `./gradlew build` stays green, making new failures obvious.

---

## Week 2: Persistence & Integration Testing

### ADR-005: Flyway for Schema Management, `validate` for Hibernate
**Context:** Hibernate's `ddl-auto: update` silently changes production schemas, hiding breaking changes.
**Decision:** Flyway owns the schema via versioned SQL migrations. Hibernate set to `ddl-auto: validate` (fails startup if entities don't match DB).
**Consequences:** Schema changes are explicit and version-controlled. Validation catches mismatches at boot, not at runtime.

### ADR-006: Separate `TicketJpaEntity` from Domain `Ticket`
**Context:** Clean Architecture forbids frameworks in the domain.
**Decision:** `TicketJpaEntity` is a pure data carrier with `@Entity`, a protected no-arg constructor, and no business logic. `JpaTicketRepository` converts between domain and entity using `toEntity()` / `toDomain()`.
**Consequences:** `Ticket.reconstitute()` added for rehydrating from DB without re-running creation validation.

### ADR-007: Explicit `TIMESTAMP` vs `TIMESTAMP_UTC` mapping
**Context:** Hibernate 6+ changed `Instant` to map to `TIMESTAMP_UTC` (PostgreSQL `timestamptz`). Mismatches cause `DataException` during validation/insert.
**Decision:** Migration uses `TIMESTAMP` (no TZ), `Instant` mapped to match. One consistent convention across the system.
**Consequences:** Silent data corruption from timestamp type mismatches avoided.

### ADR-008: Testcontainers for Integration Tests
**Context:** H2/embedded DBs mask PostgreSQL-specific behaviors (dialects, constraints, types).
**Decision:** Use Testcontainers with PostgreSQL 16 for infrastructure-layer tests. `@ServiceConnection` injects connection details. `@AutoConfigureTestDatabase(replace = NONE)` prevents embedded DB substitution.
**Consequences:** Tests verify real PostgreSQL behavior.

### ADR-009: Flyway in Test Slices (`@ImportAutoConfiguration`)
**Context:** `@DataJpaTest` is a slice — it excludes Flyway auto-configuration. Schema validation failed with "missing table [tickets]" because migrations never ran.
**Decision:** Add `@ImportAutoConfiguration(FlywayAutoConfiguration.class)` to the integration test. Depend on `spring-boot-starter-flyway` (Spring Boot 4 moved Flyway auto-config to a separate starter).
**Consequences:** Slices must explicitly import non-JPA auto-configurations they depend on.

### ADR-010: `@ServiceConnection` over `@DynamicPropertySource`
**Context:** Manual `@DynamicPropertySource` and `@ServiceConnection` both injecting datasource properties causes precedence confusion.
**Decision:** Use `@ServiceConnection` alone. Delete `@DynamicPropertySource`.
**Consequences:** Spring Boot 4 idiom; cleaner tests.

---

## Environment & Infrastructure Gotchas (Interview Lessons)

### Testcontainers on macOS
- Docker socket is at `~/.docker/run/docker.sock`, not `/var/run/docker.sock`.
- Fix: set `DOCKER_HOST` or `~/.testcontainers.properties`.
- Docker Engine 29 raised the minimum API version; Testcontainers 2.0.5 required Docker Desktop ≥4.99.
- **Lesson:** Integration failures can be runtime version mismatches, not code bugs.

### PostgreSQL Docker Password Persistence
- `POSTGRES_PASSWORD` only takes effect on first init (empty data volume).
- Changing env vars on restart doesn't update the stored password.
- Fix: `ALTER USER ... WITH PASSWORD ...` or destroy the volume.
- **Lesson:** Docker database state is not declarative across restarts.

### PostgreSQL `@DataJpaTest` Dialect
- Explicit `spring.jpa.database-platform: org.hibernate.dialect.PostgreSQLDialect` prevents bootstrapping failures in slices (even when Hibernate 7 would auto-detect).
- **Lesson:** In sliced test contexts, provide explicit config for anything that depends on runtime metadata.