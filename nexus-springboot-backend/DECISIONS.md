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