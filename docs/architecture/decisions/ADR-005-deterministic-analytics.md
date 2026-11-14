# ADR-005 — Cálculos Analíticos Determinísticos

- **Status:** Proposto
- **Data:** 2026-11-13

## Contexto

Respostas históricas precisam ser reproduzíveis e auditáveis. Cálculos delegados livremente a LLMs podem variar entre execuções.

## Decisão proposta

Métricas, agregações e comparações numéricas devem ser calculadas por código determinístico e definições versionadas. A IA interpreta os resultados, explica limitações e propõe hipóteses.

## Consequências

- Resultados reproduzíveis.
- Necessidade de catálogo e versionamento de métricas.
- A IA não deve inventar valores ausentes nem recalcular silenciosamente métricas fora das regras.
