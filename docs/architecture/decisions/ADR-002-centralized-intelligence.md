# ADR-002 — Inteligência Centralizada

- **Status:** Proposto
- **Data:** 2026-11-13

## Contexto

A plataforma acompanha múltiplos projetos e stacks. Distribuir agentes decisórios por repositório fragmentaria políticas, memória, avaliação e governança.

## Decisão proposta

Manter a camada de análise e inteligência centralizada na EIP. Projetos monitorados fornecem dados por coletores; não precisam hospedar a inteligência principal.

## Consequências

- Uma base histórica transversal pode comparar projetos.
- Políticas de acesso e avaliação ficam centralizadas.
- Integrações precisam preservar isolamento entre projetos.
- A indisponibilidade da EIP afeta consultas inteligentes, mas não deve interromper os projetos monitorados.
