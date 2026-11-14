# Identidade e Controle de Acesso

## Objetivo

Garantir que usuários e integrações acessem somente projetos, fontes, documentos e resultados autorizados.

## Modelo aprovado

- `Organization` é a fronteira de tenancy e propriedade.
- Todo usuário possui uma Organização Pessoal criada automaticamente ao concluir seu cadastro.
- A criação do usuário e de sua Organização Pessoal deverá ser consistente e atômica na futura implementação.
- `Project` pertence exclusivamente a uma organização.
- Usuários humanos recebem acesso por memberships organizacionais e de projeto.
- Identidades de serviço representam coletores e automações; não usam sessões ou credenciais humanas.

Workspace não faz parte do MVP. Transferência entre organizações está fora do MVP; transferências dentro da mesma organização exigem permissão e auditoria.

## Papéis e escopos

| Papel | Escopo | Responsabilidades |
|---|---|---|
| `ORG_ADMIN` | Organization | administra memberships, projetos e operações organizacionais autorizadas |
| `PROJECT_ADMIN` | Project | administra memberships do projeto, repositórios, fontes e identidades de serviço do próprio projeto |
| `ENGINEER` | Project | consulta e contribui em recursos autorizados, sem administrar credenciais ou permissões |
| `VIEWER` | Project | consulta recursos não sensíveis autorizados; conteúdo sensível é negado por padrão |
| `SERVICE_IDENTITY` | Project e ProjectSource concedidos | envia evidências apenas nos escopos técnicos autorizados |

Papéis organizacionais e de projeto são distintos. RBAC simples é o modelo inicial; grupos, papéis customizados, ABAC e outras políticas permanecem possibilidades futuras.

## Identidades de serviço e coletores

Uma `ServiceIdentity` pertence a um único projeto. Ela pode receber concessões explícitas para um ou mais `ProjectSource` desse projeto, com escopos definidos; o escopo inicial é `events:ingest`.

`Collector` identifica o software ou integração que coleta e transmite dados. `ServiceIdentity` é o principal autenticado. A identidade e o Collector não são equivalentes: o primeiro recebe autorização, enquanto o segundo fornece metadados de integração e execução.

- Coletores autenticam-se com credenciais próprias, nunca credenciais humanas.
- O servidor autentica a ServiceIdentity e valida seu projeto, ProjectSources e escopos antes de aceitar uma requisição.
- `project_id` ou fonte declarados no payload não provam autorização.
- Uma identidade não pode ser reutilizada entre projetos no MVP.
- Suspensão ou revogação de uma identidade não afeta os demais coletores do projeto.

## Credenciais de serviço

Credenciais são opacas, de alta entropia, reveladas uma única vez e armazenadas somente como hash. Cada credencial possui estado e metadados de criação, expiração quando aplicável, último uso, rotação e revogação.

- A rotação permite sobreposição controlada entre a credencial anterior e a nova.
- A revogação invalida o uso imediatamente.
- Segredos não podem constar de eventos, documentos, logs ou registros de auditoria.

## Diretrizes de autorização e auditoria

- Aplicar autorização no backend em toda operação.
- Restringir consultas por organização e projeto autorizados.
- Registrar operações administrativas, concessões, revogações e acessos sensíveis em auditoria de segurança.
- Separar autenticação de autorização.
- SSO/OIDC e MFA estão postergados, sem impedir uma integração futura.

## Referências

- ADR-006 — Organization como Fronteira de Tenancy sem Workspace no MVP (`TD-F0-02-001`).
- ADR-007 — Propriedade Exclusiva de Projeto por Organization (`TD-F0-02-002`).
- ADR-008 — Separação entre Repository, Source, ProjectSource, Collector e ServiceIdentity (`TD-F0-02-003`).
- ADR-009 — ServiceIdentity Escopada por Projeto, Fonte e Permissão (`TD-F0-02-004`).
- ADR-010 — Credenciais Opacas, Rotacionáveis e Armazenadas como Hash (`TD-F0-02-005`).
- ADR-011 — RBAC Simples com Papéis Organizacionais e de Projeto (`TD-F0-02-006`).
