# ADR-010 — Credenciais Opacas, Rotacionáveis e Armazenadas como Hash

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-005

## Contexto

Identidades de serviço precisam de uma autenticação simples de operar, revogável e auditável, sem depender de credenciais humanas ou infraestrutura adicional não justificada.

## Decisão

Usar credenciais opacas de alta entropia para ServiceIdentity. O segredo é revelado uma única vez e armazenado somente como hash, acompanhado de identificador seguro e metadados de ciclo de vida.

Cada credencial pode ter criação, expiração, último uso, revogação e rotação registrados. A rotação cria uma nova credencial com período controlado de sobreposição; a revogação invalida o uso imediatamente. Segredos não podem ser registrados em logs, eventos, documentos ou auditoria.

## Consequências

- Revogação e rotação são diretas e auditáveis.
- Cada coletor precisa receber, proteger e renovar sua própria credencial.
- JWT sem consulta de revogação, mTLS e federação de workload não são adotados no MVP; poderão ser avaliados se requisitos operacionais os justificarem.

