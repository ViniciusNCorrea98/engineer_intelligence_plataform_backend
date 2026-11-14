# Roadmap Inicial

O roadmap é uma proposta de sequência, não uma promessa de prazo. Cada fase deve ter escopo e critérios de aceite próprios.

## F0 — Descoberta e fundação

- validar visão, usuários e perguntas prioritárias;
- fechar conceitos do domínio;
- registrar ADRs;
- definir contrato inicial de eventos;
- definir classificação de dados e limites de coleta;
- escolher stack e estrutura inicial.

**Saída:** documentação coerente, decisões registradas e backlog priorizado.

## F1 — Project Registry

- cadastro e edição de projetos;
- repositórios, stacks e ambientes;
- registro de fontes e coletores;
- autenticação de identidades de serviço.

**Saída:** projetos e fontes configuráveis e isolados.

## F2 — Ingestion & Collection

- API de ingestão;
- validação e versionamento de schemas;
- idempotência;
- primeira integração Git;
- primeira integração de build/testes;
- telemetria de ingestão.

**Saída:** eventos confiáveis e rastreáveis.

## F3 — Evidence Data Platform

- modelo canônico;
- atividades e sessões;
- correlação explícita;
- persistência de evidências;
- captura de documentos e metadados.

**Saída:** histórico consultável com proveniência.

## F4 — Engineering Analytics

- catálogo e versionamento de métricas;
- métricas de build/testes e Git;
- métricas iniciais de IA quando disponíveis;
- relatórios com amostra, janela e limitações.

**Saída:** indicadores reproduzíveis.

## F5 — Knowledge Management & AI Intelligence

- registro de decisões;
- relações documento-evidência;
- insights e recomendações;
- ferramentas controladas de consulta;
- respostas com referências internas e limitações.

**Saída:** perguntas históricas respondidas com evidências.

## F6 — Interface e avaliação

- dashboards;
- consulta em linguagem natural;
- avaliação de respostas;
- benchmarking de modelos/agentes;
- registro do resultado de recomendações.

**Saída:** ciclo contínuo de aprendizado.

## Critério de passagem entre fases

Não avançar apenas porque a funcionalidade existe. Validar qualidade dos dados, contratos, testes, segurança e utilidade para uma pergunta real.
