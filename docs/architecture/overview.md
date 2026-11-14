# Visão Arquitetural

## Diretriz

A proposta inicial é um **monólito modular**, com inteligência centralizada e coletores independentes por fonte/projeto. A EIP permanece um produto separado dos projetos monitorados.

## Visão lógica

```text
Projetos monitorados
  ├── Git / SCM
  ├── Build e testes
  ├── CI/CD
  ├── Análise estática
  ├── Agentes e ferramentas de IA
  └── Documentos / ADRs
          |
          v
Coletores e integrações
          |
          v
API de ingestão central
          |
          v
Validação -> deduplicação -> normalização -> correlação
          |
          +--> Armazenamento de eventos/evidências
          +--> Armazenamento/referência documental
          +--> Métricas determinísticas
          |
          v
Camada de conhecimento e analytics
          |
          v
Agente central / consultas / dashboards / relatórios
```

## Módulos lógicos

1. **Project Registry:** organizações, memberships, projetos, repositórios, stacks, ambientes e fontes configuradas por projeto.
2. **Collection & Integration:** conectores, coletores, identidades de serviço, credenciais de integração e estado da coleta.
3. **Ingestion:** recepção, validação, idempotência e versionamento de contratos.
4. **Data Platform:** persistência bruta, normalização, correlação e proveniência.
5. **Engineering Analytics:** definições e cálculo determinístico de métricas.
6. **Knowledge Management:** documentos, decisões, insights, recomendações e resultados.
7. **AI Intelligence:** orquestração central, ferramentas de consulta e geração fundamentada.
8. **Evaluation:** avaliação de modelos/agentes e comparação de execuções.
9. **IAM & Governance:** acesso, auditoria, retenção e políticas.

## Proposta tecnológica inicial

| Área | Proposta | Observação |
|---|---|---|
| Backend | Java + Spring Boot | Familiaridade e ecossistema maduro |
| Persistência | PostgreSQL | Adequado para primeira versão e relações |
| API | REST | Simplicidade para primeira ingestão |
| Processamento | tarefas assíncronas no próprio backend | Extrair broker apenas se necessário |
| Instrumentação | OpenTelemetry | Padrão de traces/métricas quando aplicável |
| Observabilidade de LLM | avaliar Langfuse | Integrar sem duplicação indiscriminada |
| Frontend | React + TypeScript | Interface independente |
| Infra local | Docker Compose | Ambiente reproduzível |
| Qualidade | testes automatizados e análise estática | Regras explícitas no pipeline |

Estas são escolhas propostas, não decisões irreversíveis. Registrar ADR antes de alterações estruturais.

## Restrições arquiteturais

- Não criar microserviços no início.
- Não acoplar o backend EIP ao domínio dos projetos monitorados.
- Não exigir que todos os coletores sejam escritos na mesma linguagem.
- Não armazenar conteúdo sensível de prompts/respostas por padrão.
- Não fazer o agente consultar diretamente o banco sem ferramentas e políticas controladas.
- Não misturar ingestão, cálculo analítico e geração de texto em uma única responsabilidade.
- Não aceitar `project_id` ou fonte declarados pelo coletor como prova de autorização; validar a ServiceIdentity e suas concessões.

## Decisões relacionadas a tenancy e acesso

Os ADR-006 a ADR-012 formalizam tenancy organizacional, propriedade de projetos, separação dos conceitos de coleta, ServiceIdentity, credenciais, RBAC e auditoria. Workspace, SSO/OIDC, MFA, ABAC e transferências entre organizações permanecem fora do MVP.

## Evolução

Extrair componentes somente quando volume, isolamento operacional, requisitos de escala ou autonomia de deploy justificarem o custo.
