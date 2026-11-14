# ADR-011 — RBAC Simples com Papéis Organizacionais e de Projeto

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-006

## Contexto

A EIP precisa controlar administração organizacional, acesso a projetos e conteúdo sensível sem introduzir um modelo genérico de permissões antes de haver necessidade comprovada.

## Decisão

Adotar RBAC simples com papéis distintos por escopo:

- `ORG_ADMIN`: administra organização, memberships e projetos; possui acesso administrativo aos projetos da organização conforme políticas de sensibilidade.
- `PROJECT_ADMIN`: administra o projeto concedido, seus memberships, repositórios, fontes e identidades de serviço do próprio projeto.
- `ENGINEER`: consulta e contribui nos projetos concedidos, sem administrar credenciais ou permissões.
- `VIEWER`: possui acesso somente de leitura aos projetos concedidos e não acessa conteúdo sensível por padrão; esse acesso exige autorização específica futura.
- `SERVICE_IDENTITY`: não possui papel humano; atua somente nos escopos técnicos concedidos.

## Consequências

- Papéis organizacionais e de projeto são avaliados separadamente.
- Acesso a conteúdo sensível não é inferido apenas do papel `VIEWER`.
- Grupos, papéis customizados, ABAC e políticas mais granulares permanecem possibilidades de evolução, sem implementação no MVP.
- SSO/OIDC e MFA são postergados, mantendo-se a possibilidade de integração futura.

