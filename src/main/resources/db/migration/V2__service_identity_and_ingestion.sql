create table project_sources (
    id uuid primary key,
    project_id uuid not null references projects(id),
    source_key varchar(100) not null,
    created_at timestamp with time zone not null,
    unique (project_id, source_key)
);

create table service_identities (
    id uuid primary key,
    project_id uuid not null references projects(id),
    name varchar(255) not null,
    state varchar(32) not null,
    created_at timestamp with time zone not null
);

create table service_credentials (
    id uuid primary key,
    service_identity_id uuid not null references service_identities(id),
    secret_hash varchar(255) not null,
    state varchar(32) not null,
    created_at timestamp with time zone not null,
    expires_at timestamp with time zone,
    last_used_at timestamp with time zone,
    revoked_at timestamp with time zone
);

create table service_grants (
    id uuid primary key,
    service_identity_id uuid not null references service_identities(id),
    project_source_id uuid not null references project_sources(id),
    scope varchar(64) not null,
    created_at timestamp with time zone not null,
    unique (service_identity_id, project_source_id, scope)
);

create table security_audit_events (
    id uuid primary key,
    actor_type varchar(64) not null,
    actor_id varchar(255),
    action varchar(128) not null,
    target_type varchar(64) not null,
    target_id varchar(255),
    project_id uuid,
    outcome varchar(32) not null,
    reason varchar(2000),
    occurred_at timestamp with time zone not null
);

create table ingested_evidences (
    id uuid primary key,
    project_id uuid not null references projects(id),
    project_source_id uuid not null references project_sources(id),
    service_identity_id uuid not null references service_identities(id),
    credential_id uuid not null references service_credentials(id),
    event_id varchar(255) not null,
    idempotency_key varchar(255) not null,
    schema_version varchar(32) not null,
    event_type varchar(255) not null,
    occurred_at timestamp with time zone not null,
    observed_at timestamp with time zone,
    attributes_json jsonb not null,
    declared_activity_id uuid references development_activities(id),
    repository_reference varchar(1000),
    received_at timestamp with time zone not null,
    unique (service_identity_id, idempotency_key),
    unique (project_id, event_id)
);

create index idx_sources_project on project_sources(project_id);
create index idx_identities_project on service_identities(project_id);
create index idx_evidence_project on ingested_evidences(project_id);
