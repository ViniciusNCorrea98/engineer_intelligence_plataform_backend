create table users (
    id uuid primary key,
    email varchar(320) not null unique,
    password_hash varchar(255) not null,
    state varchar(32) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table organization_memberships (
    id uuid primary key,
    user_id uuid not null references users(id),
    organization_id uuid not null references organizations(id),
    role varchar(32) not null,
    state varchar(32) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    unique (user_id, organization_id)
);

-- ADR-011 grants project roles independently from organization roles.
create table project_memberships (
    id uuid primary key,
    user_id uuid not null references users(id),
    project_id uuid not null references projects(id),
    role varchar(32) not null,
    state varchar(32) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    unique (user_id, project_id)
);

create table human_sessions (
    id uuid primary key,
    user_id uuid not null references users(id),
    secret_hash varchar(255) not null,
    csrf_hash varchar(255) not null,
    expires_at timestamp with time zone not null,
    revoked_at timestamp with time zone,
    created_at timestamp with time zone not null
);

create index idx_organization_memberships_user on organization_memberships(user_id);
create index idx_project_memberships_user on project_memberships(user_id);
create index idx_human_sessions_user on human_sessions(user_id);
