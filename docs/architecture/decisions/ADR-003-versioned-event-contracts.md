# ADR-003 — Contratos de Eventos Versionados

- **Status:** Proposto
- **Data:** 2026-11-13

## Contexto

Coletores de diferentes linguagens e ferramentas precisam enviar dados compatíveis ao backend central.

## Decisão proposta

Adotar um envelope comum com `schema_version`, `event_id`, `project_id`, `event_type`, origem, timestamps, correlação e atributos tipados.

## Consequências

- Evolução explícita de contratos.
- Validação e rejeição rastreáveis.
- Idempotência viabilizada por identificador estável.
- Cada tipo de evento exige schema e documentação próprios.
