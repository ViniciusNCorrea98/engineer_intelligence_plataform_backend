# Backlog Inicial de Descoberta

## Produto e domínio

- [ ] Definir usuário primário e principais jornadas.
- [ ] Selecionar 5–10 perguntas históricas prioritárias.
- [x] Definir Organization como fronteira de tenancy e manter Workspace fora do MVP (ADR-006).
- [x] Definir e implementar o ciclo inicial de `DevelopmentActivity`: criação manual, conclusão, sucessora opcional e reabertura justificada/auditada.
- [x] Definir e implementar criação manual de atividades com referência externa opcional.
- [ ] Definir taxonomia de sessões humanas e de IA.
- [ ] Definir como decisões serão registradas e revisadas.

## Dados e integração

- [ ] Inventariar fontes e dados realmente disponíveis.
- [ ] Validar quais metadados o Codex CLI e demais agentes expõem.
- [x] Definir e implementar o envelope inicial de eventos com schema `1.0` e validação na ingestão.
- [x] Definir e implementar idempotência por identidade de serviço e chave, incluindo concorrência e replay.
- [x] Implementar o primeiro coletor Git one-shot, com evento determinístico, autenticação técnica e teste ponta a ponta.
- [x] Definir e implementar correlação declarada, não canônica até confirmação ou rejeição.
- [ ] Definir e implementar correlação inferida.
- [ ] Definir estratégia de documentos e versionamento.
- [ ] Definir retenção e sanitização.

## Analytics

- [ ] Priorizar métricas com fonte verificável.
- [ ] Especificar fórmula, unidade e janela de cada métrica.
- [ ] Definir tratamento de dados ausentes.
- [ ] Definir comparabilidade entre projetos.
- [ ] Criar baseline de avaliação de qualidade das métricas.

## IA e conhecimento

- [ ] Definir ferramentas permitidas ao agente.
- [ ] Definir formato de citação de evidências e documentos.
- [ ] Definir avaliação de groundedness e utilidade.
- [ ] Definir política para prompt injection.
- [ ] Definir quando conteúdo bruto pode ser enviado a provedor externo.
- [ ] Definir fluxo de aprovação/rejeição de recomendações.
- [ ] Definir como resultados posteriores serão registrados.

## Segurança e operação

- [x] Definir identidade de serviço, credenciais de coletores e RBAC inicial (ADR-009 a ADR-011).
- [x] Definir isolamento por Organization e Project (ADR-006 e ADR-007).
- [ ] Definir classificação de dados.
- [ ] Definir backup, RPO e RTO.
- [ ] Definir logs e métricas operacionais.
