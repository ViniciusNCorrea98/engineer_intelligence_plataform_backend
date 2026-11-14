# Entidades e Relacionamentos Conceituais

Este modelo é inicial e deve ser refinado conforme os casos de uso forem validados.

| Entidade | Responsabilidade | Relações principais |
|---|---|---|
| Organization | Representar a fronteira de tenancy e propriedade | possui memberships e projetos |
| User | Representar uma identidade humana | possui memberships em organizações e projetos |
| OrganizationMembership | Conceder papel organizacional a um usuário | pertence a usuário e organização |
| Project | Identificar projeto monitorado | pertence exclusivamente a organização; possui atividades, fontes e ambientes |
| ProjectMembership | Conceder papel de projeto a um usuário | pertence a usuário e projeto |
| Repository | Identificar repositório de código monitorado | possui uma associação ativa com um projeto no MVP |
| Source | Identificar ferramenta ou sistema originador de evidências | é configurada como ProjectSource |
| ProjectSource | Configurar e vincular uma fonte a um projeto | pertence a projeto e referencia uma fonte |
| Collector | Identificar software ou integração de coleta | transmite dados de fontes; não concede autorização |
| ServiceIdentity | Representar o principal não humano de um coletor | pertence a projeto; recebe concessões para ProjectSources |
| ServiceCredential | Representar credencial opaca de uma ServiceIdentity | pertence a uma identidade; possui ciclo de vida e hash do segredo |
| AuditEvent | Registrar operação relevante de segurança e administração | referencia ator, recurso, escopo e resultado |
| DevelopmentActivity | Representar unidade de trabalho | agrega sessões, eventos e resultados |
| DevelopmentSession | Representar sessão/intervalo | pertence a atividade |
| Event | Registrar fato ocorrido | possui fonte e correlações opcionais |
| AIExecution | Registrar execução de IA | ligada a atividade/sessão quando conhecido |
| AITrace | Agrupar operações correlacionadas | contém spans |
| AISpan | Representar operação | pertence a trace |
| LLMInteraction | Registrar interação com modelo | pertence a execução/span |
| TokenUsage | Registrar consumo conhecido | associado à interação |
| ToolExecution | Registrar chamada de ferramenta | associada a execução/span |
| CodeChange | Registrar alteração | associada a atividade e referências Git |
| QualityResult | Registrar validação | associado a atividade/commit/pipeline |
| EngineeringDecision | Registrar decisão | relacionada a atividade/documentos |
| KnowledgeDocument | Armazenar ou referenciar contexto | pode relacionar-se a múltiplas entidades |
| MetricDefinition | Definir cálculo e semântica | versionada |
| MetricObservation | Registrar valor calculado/observado | referencia definição e evidências |
| EngineeringInsight | Registrar análise | referencia evidências e documentos |
| Recommendation | Registrar ação sugerida | ligada a insight e avaliação |
| Outcome | Registrar resultado posterior | ligado a decisão/recomendação/atividade |
| AIEvaluation | Avaliar modelo/agente | referencia tarefa, execução e critérios |

## Identificadores e correlação

Usar identificadores estáveis e não depender exclusivamente de nomes de branch, mensagens de commit ou nomes de arquivo. Identificadores externos devem ser preservados junto com identificadores internos.

## Propriedade, acesso e coleta

- Todo usuário possui uma Organização Pessoal criada automaticamente no cadastro; ela segue o mesmo modelo das demais organizações.
- Um projeto pertence a uma única organização. Acesso humano é concedido por memberships organizacionais e de projeto.
- Um projeto pode possuir vários repositórios. No MVP, um repositório possui uma única associação ativa com um projeto dentro da mesma organização.
- `Source` representa a origem do fato; `ProjectSource` representa sua configuração em um projeto; `Collector` representa o software que transmite dados; `ServiceIdentity` representa o principal autorizado.
- Uma ServiceIdentity pertence a um projeto e somente pode atuar nas ProjectSources e escopos explicitamente concedidos.
- Eventos preservam tanto a fonte/Collector declarados quanto a identidade autenticada, quando aplicável.
- Transferência entre organizações está fora do MVP. Transferência dentro da mesma organização exige autorização e auditoria.

As definições de tenancy, propriedade e identidade de serviço estão formalizadas nos ADR-006 a ADR-010.

## Temporalidade

Distinguir:
- `occurred_at`: quando o fato aconteceu na origem;
- `observed_at`: quando a EIP recebeu/observou o fato;
- `processed_at`: quando o evento foi normalizado/processado;
- `recorded_at`: quando o registro foi persistido.

A distinção permite medir atraso de ingestão e reconstruir o histórico.
