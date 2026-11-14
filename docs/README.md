# Engineering Intelligence Platform (EIP) — Documentação

Esta pasta reúne a documentação de produto, domínio, arquitetura, dados, operação e evolução da **Engineering Intelligence Platform (EIP)**.

> **Princípio central:** dados estruturados são a fonte de evidências; documentos são a fonte de contexto. A plataforma deve relacionar os dois para construir e consultar um histórico de conhecimento de engenharia.

## Como navegar

| Diretório | Conteúdo |
|---|---|
| `project/` | Visão, objetivos, escopo, princípios e glossário do produto |
| `goals/` | Resultados esperados e indicadores de sucesso |
| `domain/` | Conceitos, entidades, relações e conhecimento de engenharia |
| `architecture/` | Visão arquitetural, componentes, fluxos e decisões |
| `backend/` | Diretrizes de implementação do backend |
| `analytics/` | Métricas, definições e regras de interpretação |
| `iam/` | Identidade, autenticação e autorização |
| `security/` | Segurança, privacidade e proteção dos dados |
| `operations/` | Execução, observabilidade, backup e recuperação |
| `envs/` | Ambientes e configuração |
| `plans-to-be-executed/` | Roadmap e planos incrementais de execução |

## Estado dos documentos

Esta é uma **baseline inicial de descoberta e arquitetura**, não uma especificação fechada de implementação. As afirmações estão classificadas implicitamente como:

- **Diretriz aprovada:** princípio explicitamente estabelecido para o produto.
- **Proposta inicial:** recomendação arquitetural para orientar a primeira versão.
- **Pendente de decisão:** assunto que precisa ser validado antes de se tornar contrato definitivo.

Não transforme propostas ou exemplos em requisitos irrevogáveis sem registrar a decisão correspondente.

## Ordem recomendada de leitura

1. `project/vision.md`
2. `project/product-principles.md`
3. `project/scope.md`
4. `domain/engineering-knowledge-model.md`
5. `architecture/overview.md`
6. `architecture/data-architecture.md`
7. `architecture/event-contract.md`
8. `analytics/metric-catalog.md`
9. `plans-to-be-executed/roadmap.md`
10. `architecture/decisions/`

## Decisões aprovadas de tenancy e acesso

As decisões de F0-02.1 estão formalizadas nos ADR-006 a ADR-012, em `architecture/decisions/`. Elas preservam as referências rastreáveis `TD-F0-02-001` a `TD-F0-02-007` e definem Organization, propriedade de projetos, ServiceIdentity, credenciais, RBAC e auditoria.

## Primeira entrega executável

O repositório contém um monólito Spring Boot para a primeira fatia vertical: organizações, projetos, atividades de desenvolvimento e associações de evidências. A persistência de desenvolvimento usa PostgreSQL e as migrações são aplicadas por Flyway.

```powershell
docker compose up -d
mvn spring-boot:run
```

Os testes usam H2 isolado e podem ser executados com `mvn test`.

O recorte atual oferece criação manual de atividade com referência externa opcional, conclusão com atividade sucessora opcional, reabertura justificada com registro de auditoria e associações de evidência que começam em `DECLARED`. Uma associação só recebe relação canônica após decisão `CONFIRMED`; `REJECTED` requer justificativa.

O cabeçalho `X-Organization-Id` é apenas um contexto de tenancy para a API de desenvolvimento; ele não autentica o chamador. A API não deve ser exposta fora de ambiente local antes da implementação da autenticação e da autorização previstas em `iam/identity-and-access.md`.

## Regras de manutenção

- Atualize a documentação junto com mudanças relevantes de domínio, contratos ou arquitetura.
- Registre decisões arquiteturais duradouras em ADRs.
- Preserve a distinção entre dado observado, dado derivado, hipótese e recomendação.
- Toda métrica deve ter definição, unidade, janela temporal, origem e limitações.
- Toda conclusão analítica deve informar a evidência que a sustenta.
- Não registre segredos, tokens, credenciais ou conteúdo sensível bruto.
- Quando uma decisão mudar, registre a motivação e o impacto histórico; não reescreva silenciosamente o passado.
