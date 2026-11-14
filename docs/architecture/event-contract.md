# Contrato de Eventos — Baseline

## Objetivo

Definir um envelope comum para eventos produzidos por coletores heterogêneos. O envelope é estável; os atributos específicos variam por `event_type` e `schema_version`.

## Exemplo ilustrativo

```json
{
  "event_id": "0192f4e0-0000-7000-8000-000000000001",
  "schema_version": "1.0",
  "project_id": "smart-process",
  "event_type": "test_execution_completed",
  "source": {
    "name": "maven",
    "version": "3.9.9",
    "collector": "eip-maven-collector",
    "collector_version": "0.1.0"
  },
  "occurred_at": "2026-11-13T18:00:00Z",
  "observed_at": "2026-11-13T18:00:03Z",
  "correlation": {
    "activity_id": "activity-456",
    "session_id": "session-123",
    "trace_id": null
  },
  "attributes": {
    "tests_total": 120,
    "tests_passed": 118,
    "tests_failed": 2,
    "duration_ms": 18400
  }
}
```

## Campos do envelope

| Campo | Obrigatório | Descrição |
|---|---:|---|
| `event_id` | sim | Identificador único/idempotente do evento |
| `schema_version` | sim | Versão do contrato |
| `project_id` | sim | Projeto ao qual o evento pertence |
| `event_type` | sim | Tipo semântico do evento |
| `source.name` | sim | Fonte de origem |
| `source.collector` | recomendado | Coletor que transmitiu o evento |
| `occurred_at` | sim | Instante do fato na origem |
| `observed_at` | recomendado | Instante em que a EIP observou o evento |
| `correlation` | não | Identificadores conhecidos de correlação |
| `attributes` | sim | Atributos tipados definidos para o tipo de evento |

## Regras

- Timestamps em ISO 8601 com timezone.
- `event_id` deve permanecer estável em retries.
- Reenvio do mesmo evento não pode duplicar efeito.
- Atributos desconhecidos podem ser preservados conforme política de compatibilidade.
- Campos não coletados devem ser omitidos ou `null`; nunca preencher com zero por conveniência.
- O servidor valida projeto, fonte, tamanho e versão do schema.
- Eventos inválidos devem gerar resposta/rejeição rastreável.
- Segredos e dados pessoais devem ser removidos no coletor ou na borda de ingestão.
- Mudanças incompatíveis exigem nova versão major do schema.

## Primeiros tipos candidatos

- `git_commit_created`
- `git_pull_request_updated`
- `build_execution_completed`
- `test_execution_completed`
- `static_analysis_completed`
- `ai_execution_started`
- `ai_execution_completed`
- `llm_interaction_recorded`
- `tool_execution_recorded`
- `code_change_recorded`
- `engineering_decision_recorded`
- `document_captured`

A lista não é fechada. Cada tipo precisa de schema próprio e documentação de origem/limitações.
