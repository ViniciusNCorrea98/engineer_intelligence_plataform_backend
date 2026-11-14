# Visão do Domínio

## Objetivo

Definir os conceitos fundamentais da EIP sem assumir a tecnologia ou o formato físico de persistência.

## Conceitos centrais

- **Project:** unidade monitorada e fronteira de configuração.
- **DevelopmentActivity:** unidade de trabalho de engenharia, humana, automatizada ou mista.
- **DevelopmentSession:** período ou sessão de execução associado a uma atividade.
- **Source:** ferramenta ou sistema que originou os dados.
- **Event:** fato ocorrido, com timestamp e proveniência.
- **AIExecution:** execução de modelo/agente associada a uma atividade, quando aplicável.
- **Trace / Span:** estrutura de observabilidade para operações correlacionadas.
- **CodeChange:** alteração de código relacionada a uma atividade.
- **QualityResult:** resultado de testes, análise estática, revisão ou pipeline.
- **EngineeringDecision:** decisão técnica com contexto, alternativas e justificativa.
- **KnowledgeDocument:** documento contextual versionado ou referenciado.
- **EngineeringInsight:** interpretação derivada de evidências e contexto.
- **Recommendation:** proposta de ação gerada e avaliada.
- **Outcome:** resultado observado após uma ação ou decisão.

## Relações de alto nível

Um projeto contém atividades. Uma atividade pode possuir sessões, eventos, execuções de IA, alterações de código, resultados de qualidade, decisões e documentos relacionados. Eventos são originados por fontes e podem ser correlacionados por identificadores. Insights e recomendações referenciam as evidências e documentos utilizados.

## Regras conceituais

- Uma atividade pode ser humana, assistida por IA ou automatizada.
- Nem todo evento precisa estar associado a uma atividade no momento da ingestão; correlação pode ocorrer posteriormente.
- Uma execução de IA não equivale necessariamente a uma atividade completa.
- Um documento pode contextualizar várias atividades; uma atividade pode ter vários documentos.
- Uma métrica derivada deve guardar versão da definição e janela temporal.
- O histórico deve permitir distinguir estado observado na época e estado corrigido posteriormente.
