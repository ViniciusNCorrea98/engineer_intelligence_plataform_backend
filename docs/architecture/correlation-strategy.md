# Estratégia de Correlação

## Objetivo

Relacionar fatos de ferramentas diferentes ao mesmo trabalho de engenharia, sem presumir que todos os sistemas compartilham identificadores nativamente.

## Eixos de correlação

1. `project_id`: fronteira do projeto.
2. `activity_id`: unidade de trabalho.
3. `session_id`: sessão de trabalho ou execução.
4. `trace_id` / `span_id`: rastreamento técnico de operações.
5. Referências externas: commit SHA, pull request, pipeline run, task ID, conversation/session ID da ferramenta, quando disponíveis.

## Estratégia

- Priorizar identificadores explícitos propagados pelo coletor.
- Preservar identificadores nativos da fonte.
- Uma associação declarada por coletor deve iniciar em `DECLARED`; ela preserva fonte, repositório de origem, evidência, autor declarador, momento e confiança.
- Somente uma decisão explícita `CONFIRMED` estabelece a relação canônica. Uma decisão `REJECTED` não estabelece relação e exige justificativa.
- Permitir associação posterior, registrando regra, momento e confiança da associação.
- Não correlacionar somente por proximidade temporal como se fosse certeza.
- Registrar correlações inferidas separadamente das correlações explícitas.
- Permitir que uma atividade contenha trabalho humano e de IA.

## Qualidade da correlação

Uma relação inferida deve conter:
- método;
- evidências usadas;
- confiança;
- data;
- possibilidade de revisão humana.

## Recorte implementado

- Atividades são criadas manualmente na EIP e podem carregar referência opcional a issue externa. Integrações nativas com issue trackers não fazem parte deste recorte.
- Uma atividade concluída pode criar uma atividade sucessora. Reabertura é excepcional, exige justificativa e gera registro de auditoria de domínio.
- A associação de evidência preserva `repository_reference`, permitindo que uma mesma atividade reúna evidências de diferentes repositórios do projeto.

## Questões pendentes

- Quais ferramentas permitem propagar `activity_id`?
- Qual granularidade de sessão é útil para cada fonte?
- Como as correlações inferidas serão calculadas, explicadas e revisadas?
