# Development Standards

## Architecture and organization

EIP is a modular monolith. Packages are organized by business capability: `project`, `activity`, `security`, and `ingestion`; `common` holds cross-cutting HTTP errors only. A module may contain its entity, application service, repository, and controller while the module is small. Split into explicit domain, application, infrastructure, and interface packages only when that improves a real dependency boundary.

Controllers are HTTP adapters: they validate DTOs and map responses, but do not implement domain policy. Services coordinate use cases and transactions. Entities protect state transitions and persistence invariants. Repositories only retrieve or persist aggregates. Migrations are the database contract and must not be replaced by automatic DDL.

Dependencies flow from interfaces to services and domain/persistence, never from entities to controllers. New abstractions require at least two concrete use cases or a demonstrated boundary; generic wrappers and speculative layers are prohibited.

Use singular, capability-based package names; `PascalCase` for types, `camelCase` for members, and descriptive verb-first service operations. DTOs are immutable records at the HTTP boundary.

## Java and Spring Boot

Use constructor injection. Validate external input with Jakarta Validation at controllers and retain domain checks in entities/services when an invariant can also be reached internally. Return safe, consistent errors through `ApiExceptionHandler`; never expose exception details, stack traces, credentials, or raw sensitive payloads.

Mark read-only queries with `@Transactional(readOnly = true)`. Write operations must be transactional around their consistency boundary. Use `REQUIRES_NEW` only when an audit or idempotent persistence outcome must survive an outer transaction failure, and document the reason in code.

Flyway migrations are append-only, named `V<version>__<purpose>.sql`, and reviewed with the entity mapping. Application configuration uses environment variables for deploy-specific values. Secrets are never committed, logged, returned after their single permitted disclosure, or included in migrations.

## Quality and tests

Tests are named for observable behavior. Unit tests cover entity invariants and calculations; integration tests cover persistence, transactions, validation, HTTP contracts, authorization, tenancy isolation, and idempotency. Concurrent ingestion must prove that only one effect is persisted.

Before a delivery is complete, run `mvn test`, inspect changed migrations and contracts, and verify no behavioral change was introduced accidentally. Use PostgreSQL integration tests whenever JSONB, uniqueness, transaction isolation, or SQL behavior matters; H2 is useful for isolated fast checks but is not a substitute for PostgreSQL.

## Security and observability

Organization and project boundaries are always validated server-side. A collector authenticates as `ServiceIdentity`, not as a human user, and must be granted both the project source and required scope. Credentials are opaque, high-entropy values stored only as hashes; rotate or revoke instead of editing them.

Audit sensitive administrative actions and meaningful authorization/authentication denials. Keep security audit, domain history, and operational logs distinct. Logs should be structured and correlate requests when infrastructure support exists, but must never contain bearer tokens, credential hashes, or sensitive evidence payloads.

## Evolution

Implement vertical slices: contract, validation, authorization, persistence, and automated proof together. Preserve API and event compatibility; incompatible event changes require a new schema major version. Update an ADR only for a durable architectural choice, and update the directly affected operational documentation with implementation changes.

Record debt when a necessary functional refactor is discovered during unrelated work. Do not hide it in comments or perform it opportunistically. Avoid dependencies, brokers, services, and abstractions until evidence justifies them.
