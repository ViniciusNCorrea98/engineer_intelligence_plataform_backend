# ADR-004 — PostgreSQL como Persistência Inicial

- **Status:** Proposto
- **Data:** 2026-11-13

## Contexto

O domínio exige relações, consistência, consultas analíticas iniciais e flexibilidade limitada para atributos de fontes.

## Decisão proposta

Usar PostgreSQL como armazenamento inicial, combinando tabelas relacionais e JSONB apenas para atributos variáveis.

## Consequências

- Menos componentes para operar.
- Transações e integridade relacional disponíveis.
- Consultas analíticas iniciais possíveis.
- Volumes muito altos ou consultas especializadas podem exigir tecnologias complementares no futuro.

## Reavaliação

Medir volume, latência, retenção e padrões de consulta antes de introduzir banco analítico ou armazenamento especializado.
