# Current Development State

## Last functional delivery

The first executable Git collector now completes the initial evidence flow. It reads the current commit from a real local repository, creates a deterministic `git_commit_created` event scoped by repository and commit, authenticates as a `ServiceIdentity`, and sends the event through the ingestion API. Replays resolve to the existing evidence instead of creating duplicates.

## Existing components

- Spring Boot modular monolith with Flyway migrations V1, V2, and V3.
- Organization, Project, DevelopmentActivity, successor/reopen audit, and declared/confirmed/rejected associations.
- ProjectSource, ServiceIdentity, hashed credentials, grants, security audit, and evidence persistence.
- Ingestion endpoint with schema `1.0` validation, source/scope authorization, provenance, and database uniqueness for idempotency.
- HumanUser, organization/project memberships, RBAC roles from ADR-011, hashed password/session/CSRF material, and security audit events for human authentication and administrative operations.
- One-shot Java Git collector with minimal commit metadata, optional declared activity correlation, HTTP/process timeouts, and no collection of author, email, or commit message.

## Test status

`mvn test` passes (11 tests). The suite covers activity behavior, isolation, technical credential validation, revocation/suspension, source/project authorization, contract validation, replay, concurrent idempotency, human registration/login/logout/session expiry, CSRF, protected administrative operations, cross-organization denial, and the real Git-to-HTTP-to-database collector flow. H2 is used by the default suite.

## PostgreSQL validation

Docker Desktop is running and `eip-postgres-1` (PostgreSQL 16) remains published on local port `5433` for development. Its database and volume are not used by integration tests.

`docker-compose.test.yml` defines a separate PostgreSQL 16 service on local port `55432`, with the dedicated `eip-integration-test-postgres-data` volume. The reproducible commands and test-only environment variables are documented in `docs/development/postgresql-integration-tests.md`.

Flyway 10 requires the PostgreSQL database-support module alongside `flyway-core`. `mvn test '-Dspring.profiles.active=postgres'` passes against the isolated PostgreSQL service with migrations V1, V2, and V3 validated. Collector execution and configuration are documented in `docs/collectors/git-collector.md`.
