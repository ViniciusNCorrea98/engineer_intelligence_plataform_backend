# Current Development State

## Last functional delivery

Human Identity and Administrative Authorization is now implemented on top of the authenticated ingestion baseline. Human users register with a personal organization and an `ORG_ADMIN` membership created atomically. Opaque, server-side sessions are stored as hashes and protect administrative organization, project, source, ServiceIdentity, grant, and membership operations.

## Existing components

- Spring Boot modular monolith with Flyway migrations V1, V2, and V3.
- Organization, Project, DevelopmentActivity, successor/reopen audit, and declared/confirmed/rejected associations.
- ProjectSource, ServiceIdentity, hashed credentials, grants, security audit, and evidence persistence.
- Ingestion endpoint with schema `1.0` validation, source/scope authorization, provenance, and database uniqueness for idempotency.
- HumanUser, organization/project memberships, RBAC roles from ADR-011, hashed password/session/CSRF material, and security audit events for human authentication and administrative operations.

## Test status

`mvn test` passes (10 tests). The suite covers activity behavior, isolation, technical credential validation, revocation/suspension, source/project authorization, contract validation, replay, concurrent idempotency, human registration/login/logout/session expiry, CSRF, protected administrative operations, and cross-organization denial. H2 is used by the default suite.

## PostgreSQL validation

Docker Desktop is running and `eip-postgres-1` (PostgreSQL 16) is published on local port `5433`. The former `5432` route reached a different PostgreSQL authentication context, producing the misleading JDBC password failure. The EIP Compose mapping and default datasource now use `5433`, preserving the existing EIP database.

Flyway 10 also requires the PostgreSQL database-support module; it is now declared alongside `flyway-core`. `mvn test -Dspring.profiles.active=postgres` passes against PostgreSQL 16 with migrations V1, V2, and V3 validated. The next activity can build on the protected administration and validated ingestion baseline rather than reimplementing either authentication flow.
