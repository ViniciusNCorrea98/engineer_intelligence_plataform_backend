# ADR-009 — ServiceIdentity Escopada por Projeto, Fonte e Permissão

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-004

## Contexto

Coletores executam em ambientes distintos e não devem usar sessões ou credenciais humanas. Uma credencial global aumentaria o impacto de comprometimento e permitiria acesso além do necessário.

## Decisão

Uma `ServiceIdentity` pertence a um único `Project` e recebe concessões explícitas para um ou mais `ProjectSource` daquele projeto, com escopos de permissão definidos.

O escopo inicial de ingestão é `events:ingest`, sempre limitado ao projeto e às fontes concedidas. A futura API deve autenticar a identidade e validar que o projeto e a fonte declarados no evento correspondem às suas concessões; `project_id` enviado pelo coletor não é prova de autorização.

## Consequências

- Coletores especializados podem possuir identidades independentes no mesmo projeto.
- Suspensão ou revogação de uma identidade não afeta os demais coletores.
- Não haverá identidade de serviço reutilizável entre projetos no MVP.
- O vínculo por família de `event_type` pode ser acrescentado quando os schemas iniciais forem formalizados.

