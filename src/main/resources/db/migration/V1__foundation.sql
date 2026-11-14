create table organizations (
    id uuid primary key,
    name varchar(255) not null
);

create table projects (
    id uuid primary key,
    organization_id uuid not null references organizations(id),
    name varchar(255) not null
);

create table development_activities (
    id uuid primary key,
    project_id uuid not null references projects(id),
    title varchar(500) not null,
    external_issue_reference varchar(1000),
    status varchar(32) not null,
    created_at timestamp with time zone not null,
    completed_at timestamp with time zone,
    successor_activity_id uuid
);

create table activity_reopen_audits (
    id uuid primary key,
    activity_id uuid not null references development_activities(id),
    actor_id varchar(255) not null,
    justification varchar(2000) not null,
    recorded_at timestamp with time zone not null
);

create table evidence_associations (
    id uuid primary key,
    activity_id uuid not null references development_activities(id),
    source_system varchar(255) not null,
    repository_reference varchar(1000) not null,
    evidence_reference varchar(1000) not null,
    declared_by varchar(255) not null,
    confidence double precision,
    state varchar(32) not null,
    declared_at timestamp with time zone not null,
    decided_at timestamp with time zone,
    decided_by varchar(255),
    decision_reason varchar(2000),
    canonical_relation_established_at timestamp with time zone
);

create index idx_projects_organization on projects(organization_id);
create index idx_activities_project on development_activities(project_id);
create index idx_associations_activity on evidence_associations(activity_id);
