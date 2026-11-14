# Questões em Aberto

As respostas devem ser registradas como decisões de produto/domínio ou ADRs, conforme a natureza.

## Produto

1. Quem é o usuário primário: desenvolvedor individual, líder técnico ou equipe?
2. Quais são as primeiras perguntas que precisam ser respondidas?
3. Qual é a unidade de trabalho prioritária: issue, tarefa, feature, sessão ou atividade criada na EIP?

## Atividades e correlação

4. Como uma atividade nasce e quando é considerada concluída?
5. Uma atividade pode abranger vários repositórios?
6. Como representar trabalho exploratório sem issue?
7. Como diferenciar tempo ativo humano, tempo de execução do agente e tempo calendário?
8. Como resolver correlações ambíguas?

## Coleta e IA

9. Quais ferramentas de IA serão integradas primeiro?
10. Quais metadados são efetivamente expostos por cada ferramenta?
11. O conteúdo de prompts/respostas será coletado? Em quais condições?
12. Como registrar intervenções humanas?
13. Como comparar tarefas equivalentes entre modelos?

## Conhecimento

14. Quais tipos de documentos serão ingeridos inicialmente?
15. Como versionar documentos externos e documentos alterados?
16. Quem valida insights e recomendações?
17. Como registrar rejeição de uma recomendação?
18. Como determinar quando um conhecimento está desatualizado?

## Plataforma

19. Qual volume esperado de eventos e retenção?
20. Quais requisitos de disponibilidade e recuperação?
21. Quais provedores de IA podem receber dados e sob quais políticas?

## Questões resolvidas

| Questão | Decisão |
|---|---|
| Tenancy | `Organization` é a fronteira de tenancy; uso individual ocorre por Organização Pessoal criada automaticamente no cadastro. Workspace está fora do MVP. |
| Propriedade e transferência | Projeto pertence a uma única organização. Transferência entre organizações está fora do MVP; transferência interna exige permissão e auditoria. |
| Papéis e permissões | RBAC simples com papéis organizacionais e de projeto; `VIEWER` não acessa conteúdo sensível por padrão. |
| Coletores | ServiceIdentity é separada de usuário humano, escopada por projeto, fonte e permissão. |
| SSO | Postergado, mantendo possibilidade de integração futura. |

As decisões correspondentes estão registradas nos ADR-006 a ADR-012 e preservam as referências `TD-F0-02-001` a `TD-F0-02-007`.
