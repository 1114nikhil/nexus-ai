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

# Nexus AI — Architecture Decision Record (ADR) - Chunk 2

## Chunk 2: Machine Learning & Cross-Language Integration

### ADR-012: Real ML Model Over Hardcoded Rules
**Context:** The initial FastAPI service used `if/else` rules to classify tickets. This is not scalable, cannot learn from data, and would fail in an interview as "fake AI."
**Decision:** Train a real Scikit-Learn model (TF-IDF + Logistic Regression) on synthetic ticket data and serve it via FastAPI.
**Consequences:** The system now has a genuine ML inference path. The model is saved with `joblib` and loaded once at startup, so requests are fast. This is the foundation for later chunk (embeddings, RAG).

### ADR-013: TF-IDF + Logistic Regression for Text Classification
**Context:** We needed a text classifier that is fast, interpretable, and works with a small dataset.
**Decision:** Use `TfidfVectorizer` (converts text to numeric features) + `LogisticRegression` (classifies based on features). The pipeline is saved as a single artifact (`model.pkl`).
**Consequences:** Training is fast (seconds). Inference is instant. The model is explainable — you can inspect which words drive a `CRITICAL` prediction. For later scaling, we can swap in a transformer model without changing the API contract.

### ADR-014: Model Served Via FastAPI (Cross-Language Integration)
**Context:** Spring Boot is the primary backend. The AI service is Python. They must communicate without sharing a runtime.
**Decision:** The Python service exposes `POST /classify` over HTTP. Spring Boot calls it via a Port/Adapter pair:
- Port: `TicketClassifier` (in `nexus-application`)
- Adapter: `HttpTicketClassifier` (in `nexus-infrastructure`)
  **Consequences:** The Java backend remains independent of Python. If we later replace Python with a different ML service, only the adapter changes. The port contract is documented by the request/response JSON shapes.

### ADR-015: Use `RestTemplate` with `Map` for Python Calls (Spring Boot 4)
**Context:** Spring Framework 7's `RestClient.Builder.configureMessageConverters()` has a bug: calling it disables the default JSON converter registration (spring-framework#36747). Sending a Java `record` body to FastAPI resulted in `422 Unprocessable Content: {"loc":["body"],"msg":"Field required"}` because the body was not serialized.
**Decision:** Use `RestTemplate` with `Map<String, String>` body. `RestTemplate` registers default converters in its constructor unconditionally, avoiding the bug. `Map` avoids any record serialization edge cases.
**Consequences:** The Python service receives a valid `{"text": "..."}` body. This is the only reliable way to call Python from Spring Boot 4 until the `RestClient` bug is fixed upstream.

### ADR-016: Graceful Degradation in AI Classifier
**Context:** The AI service is external. If it's down, slow, or returns an error, ticket creation should not fail.
**Decision:** `HttpTicketClassifier` catches all exceptions, logs them, and returns `MEDIUM` as a fallback. The user still gets a ticket, but with a conservative priority.
**Consequences:** The system is resilient. The business can manually triage `MEDIUM` tickets if the AI is unavailable. Later, we can add a retry queue or circuit breaker for better resilience.

### ADR-017: Gradle Clean Build for Stale Classes
**Context:** After changing `HttpTicketClassifier` from `RestClient` to `RestTemplate`, the old `.class` file kept running because Gradle's incremental compiler did not detect the change.
**Decision:** Always use `./gradlew clean :nexus-api:bootRun` when the running behavior does not match the source code.
**Consequences:** Clean builds take a few seconds longer, but they guarantee that the running JVM matches the source.

---

## Challenges & Root Causes (Chunk 2 Debugging Log)

### Challenge 1: Python 3.14 and Scikit-Learn Compilation Failure
**Symptom:** `pip install -r requirements.txt` failed with `C1083: Cannot open compiler generated file` during `scikit-learn` build.
**Root Cause:** `scikit-learn==1.5.2` does not have a pre-compiled wheel for Python 3.14. pip fell back to building from source, which requires a complete MSVC toolchain on Windows.
**Fix:** Upgrade to `scikit-learn>=1.9.0`, which has a pre-compiled wheel for Python 3.14. Also upgraded `pydantic` to `>=2.13.0` for the same reason.
**Lesson:** Always match library versions to your Python version. Check PyPI for `cp314` wheels before pinning a version.

### Challenge 2: FastAPI 500 Error — Pydantic Type Mismatch
**Symptom:** `POST /classify` returned `500 Internal Server Error` with `pydantic_core.ValidationError: confidence — Input should be a valid string [type=string_type, input_value=0.29, input_type=float]`.
**Root Cause:** The `ClassifyResponse` model declared `confidence: str`, but the code passed a `float`.
**Fix:** Change `confidence: str` to `confidence: float` in `main.py`.
**Lesson:** Pydantic is strict about types. Match the model declaration to the actual value type.

### Challenge 3: Model Filename Typo — `model.pk1` vs `model.pkl`
**Symptom:** The model was saved as `model.pk1` (digit `1`) but the convention is `model.pkl` (letter `l`). The code loaded and saved consistently, but the naming was fragile.
**Fix:** Standardize to `model.pkl` everywhere.
**Lesson:** Standardize artifact names. A typo in a filename can cause hours of confusion when the model "doesn't load."

### Challenge 4: FastAPI 422 — Body Missing from Spring Boot
**Symptom:** Spring Boot log: `AI Service unavailable, defaulting to MEDIUM: 422 Unprocessable Content: {"loc":["body"],"msg":"Field required"}`. Python log: `POST /classify 422`.
**Root Cause:** Spring Framework 7's `RestClient.configureMessageConverters()` disables default converter registration (spring-framework#36747). The Java `record` body was never serialized, so FastAPI received an empty body.
**Fix:** Switch to `RestTemplate` with `Map<String, String>` body.
**Lesson:** When an external service says `body missing`, it usually means your HTTP client is not sending JSON. Check the client's converter pipeline, not the server's parser.

### Challenge 5: Stale `.class` File in Gradle
**Symptom:** After changing the code from `RestClient` to `RestTemplate`, the log still printed `ClassifyRequest[text=...]` (old code), and Python still returned 422.
**Root Cause:** Gradle's incremental compiler did not detect the change, so the old `.class` file was still being loaded by Spring Boot.
**Fix:** Run `./gradlew clean :nexus-api:bootRun` to force recompilation.
**Lesson:** When the runtime behavior does not match the source code, always suspect a stale build. `clean` is cheap insurance.

### Challenge 6: Controller Echoing Request Instead of Saved Entity
**Symptom:** Postman response showed `"priority": "LOW"` even though the AI returned `CRITICAL`. The DB had the correct value, but the API response was wrong.
**Root Cause:** The `TicketController` was constructing the response from `request.priority()`, not from the saved ticket.
**Fix:** After `createTicketUseCase.execute(command)`, fetch the saved ticket using `getTicketUseCase.execute(ticketId)` and map the response from that.
**Lesson:** The API response should always reflect the persisted state, not the input. This is the difference between a demo and a production API.

---
