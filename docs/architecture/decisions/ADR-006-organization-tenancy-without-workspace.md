# ADR-006 — Organization como Fronteira de Tenancy sem Workspace no MVP

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-001

## Contexto

A EIP deve atender tanto uso individual quanto equipes e organizações, preservando isolamento, propriedade e administração consistentes entre projetos.

## Decisão

Adotar `Organization` como fronteira de tenancy. Todo projeto pertence a uma única organização. `Workspace` não fará parte do MVP.

Ao concluir o cadastro, todo usuário terá uma Organização Pessoal criada automaticamente. Projetos individuais pertencem a essa organização, que utiliza o mesmo modelo de tenancy e autorização das demais organizações. A criação do usuário e de sua Organização Pessoal deverá ser uma operação consistente e atômica na futura implementação.

## Consequências

- Uso individual e colaborativo compartilham o mesmo modelo de propriedade.
- Memberships organizacionais passam a ser necessários desde o início.
- Não existe um modelo especial de propriedade direta por usuário.
- `Workspace` poderá ser introduzido futuramente se houver necessidade comprovada de agrupamento ou políticas próprias dentro de uma organização.
- A evolução para Workspace exigirá associar os projetos existentes a um workspace padrão, mas não altera a propriedade organizacional.

## Fora do MVP

- Workspace.
- Transferência de projetos entre organizações.

