# ADR-007 — Propriedade Exclusiva de Projeto por Organization

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-002

## Contexto

Projetos são a unidade monitorada e a fronteira de configuração e autorização da EIP. A propriedade precisa ser inequívoca para preservar isolamento e auditoria.

## Decisão

Cada `Project` pertence exclusivamente a uma `Organization`. Usuários recebem acesso por memberships e papéis; eles não são proprietários diretos do projeto.

Projetos podem possuir vários repositórios. Na primeira versão, um repositório possui uma única associação ativa a um projeto dentro da mesma organização. Transferências entre projetos da mesma organização devem respeitar permissões e gerar auditoria. Transferências entre organizações estão fora do MVP.

## Consequências

- A propriedade, o isolamento e a administração de projetos são consistentes para indivíduos e equipes.
- A revogação de um usuário não afeta identidades de serviço autorizadas do projeto.
- A associação de um repositório a múltiplos projetos permanece fora do escopo até existir um caso de uso validado.
- Uma futura transferência entre organizações exigirá decisão específica sobre autorização, histórico, fontes e identidades de serviço.

