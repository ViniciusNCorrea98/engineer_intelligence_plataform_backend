# ADR-008 — Separação entre Repository, Source, ProjectSource, Collector e ServiceIdentity

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-003

## Contexto

Repositórios, ferramentas de origem, integrações de coleta e principais autorizados possuem responsabilidades distintas. Confundi-los prejudicaria proveniência, autorização e evolução das integrações.

## Decisão

Manter conceitos separados:

- `Repository` identifica um repositório de código monitorado por um projeto.
- `Source` identifica a ferramenta ou sistema que origina evidências.
- `ProjectSource` representa o vínculo e a configuração de uma fonte em um projeto.
- `Collector` identifica a integração ou software que extrai e transmite dados.
- `ServiceIdentity` é o principal não humano autenticado que representa uma instalação ou execução de coletor.

O Collector declara metadados de software e execução; ele não concede autorização por si só. A ServiceIdentity recebe autorização para ProjectSources e escopos específicos.

## Consequências

- A proveniência distingue origem do fato, transmissor e identidade autorizada.
- Um projeto pode configurar a mesma tecnologia de fonte de modo independente de outros projetos.
- O modelo possui mais entidades explícitas, justificadas por responsabilidades diferentes.

