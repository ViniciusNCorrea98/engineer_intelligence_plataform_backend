# ADR-001 — Monólito Modular como Arquitetura Inicial

- **Status:** Proposto
- **Data:** 2026-11-13

## Contexto

A EIP inicia com volume e requisitos operacionais ainda desconhecidos. Distribuir módulos em serviços separados adicionaria custos de deploy, observabilidade, rede, consistência e operação antes de existir evidência de necessidade.

## Decisão proposta

Começar com um backend em monólito modular, com fronteiras internas explícitas entre Registry, Ingestion, Data Platform, Analytics, Knowledge, AI Intelligence e IAM.

## Consequências positivas

- Menor complexidade operacional.
- Transações locais mais simples.
- Facilidade de desenvolvimento e testes integrados.
- Menor custo inicial de infraestrutura.

## Consequências negativas

- Necessidade de disciplina para preservar fronteiras.
- Escala independente de módulos não estará disponível inicialmente.
- Falhas e deploys compartilham o mesmo processo.

## Critérios para reavaliar

Extrair um módulo quando houver necessidade comprovada de escala independente, isolamento, disponibilidade, ciclo de deploy ou tecnologia específica.
