# Catálogo Inicial de Métricas

Este catálogo é exploratório. Cada métrica só deve ser implementada após validar disponibilidade, semântica e qualidade da fonte.

## Desenvolvimento

| Métrica | Definição inicial | Fontes candidatas | Cuidados |
|---|---|---|---|
| Lead time | Tempo entre início definido e entrega definida | issue tracker, Git, CI | Definir precisamente início/fim |
| Tempo de implementação | Tempo ativo atribuído à atividade | sessões/telemetria | Não confundir com tempo calendário |
| Ciclos de revisão | Número de ciclos de feedback | PR/review | Depende da ferramenta |
| Retrabalho | Alteração corretiva após validação/review | Git, PR, issues | Exige regra operacional clara |
| Complexidade | Métrica de complexidade do código | análise estática | Comparar mesma ferramenta/versão |
| Cobertura | Cobertura reportada pela suíte | testes | Cobertura não equivale a qualidade |
| Falhas de CI | Execuções malsucedidas | CI | Separar falha de código e infraestrutura |

## IA

| Métrica | Definição inicial | Fonte | Cuidados |
|---|---|---|---|
| Tokens de entrada/saída | Uso reportado pelo provedor | API/telemetria da ferramenta | Nem toda CLI expõe os dados |
| Custo por execução | Custo conhecido da execução | provedor + tabela de preços versionada | Registrar moeda e versão do preço |
| Latência | Duração observada | trace/collector | Definir início e fim |
| Tool calls | Quantidade de chamadas a ferramentas | trace/logs | Nem sempre disponível |
| Retries | Repetições observadas | trace/logs | Distinguir retry automático e manual |
| Intervenção humana | Intervenções registradas | eventos/sessões | Definir taxonomia |
| Taxa de sucesso | Execuções que atendem critérios | avaliação | Não usar apenas exit code |

## Qualidade e resultado

- taxa de aprovação de testes;
- defeitos encontrados após entrega;
- reversões e correções;
- critérios de aceite atendidos;
- qualidade da solução segundo avaliação definida;
- custo de manutenção observado posteriormente.

## Eficiência

- custo de IA por atividade concluída;
- tokens por atividade;
- tempo humano versus tempo de agente versus tempo calendário;
- iterações até aprovação;
- qualidade por unidade de custo;
- comparação entre atividades assistidas e não assistidas, com controle de contexto.

## Contrato obrigatório de uma métrica

Cada `MetricDefinition` deve especificar:
- identificador e nome;
- descrição e pergunta respondida;
- fórmula;
- unidade;
- granularidade;
- janela temporal;
- filtros e população;
- fontes requeridas;
- tratamento de ausências;
- versão;
- limitações e vieses conhecidos.

## Regras

- Não usar commits ou linhas de código como produtividade isolada.
- Não comparar projetos sem explicitar diferenças de stack, tamanho, tipo de trabalho e maturidade.
- Ausência de medição deve resultar em `unknown`/nulo ou cobertura incompleta.
- Exibir tamanho da amostra e período.
