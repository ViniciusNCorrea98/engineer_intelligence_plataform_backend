# Escopo do Produto

## Dentro do horizonte do produto

- Registrar projetos, repositórios, stacks, ambientes e fontes de dados.
- Receber eventos de ferramentas de desenvolvimento, testes, CI/CD e agentes de IA.
- Normalizar, validar, deduplicar e correlacionar eventos.
- Armazenar evidências estruturadas e referências a documentos contextuais.
- Calcular métricas reproduzíveis e historicamente comparáveis.
- Registrar decisões técnicas, hipóteses, recomendações e resultados observados.
- Disponibilizar consultas, dashboards e relatórios.
- Oferecer uma camada central de IA que consulta dados autorizados e apresenta respostas fundamentadas.
- Avaliar desempenho de modelos e agentes em tarefas comparáveis.

## Fora do escopo inicial

- Substituir Git, CI/CD, issue trackers, ferramentas de observabilidade ou plataformas de avaliação de LLM.
- Executar alterações autônomas nos repositórios monitorados.
- Prometer medição perfeita de produtividade individual.
- Inferir causalidade a partir de correlações simples.
- Criar um SDK específico para cada linguagem antes de validar os contratos de ingestão.
- Começar com microserviços, event streaming distribuído ou data lake complexo sem necessidade demonstrada.

## Primeiro incremento recomendado

1. Cadastro de projetos.
2. Contrato de evento versionado e API de ingestão.
3. Registro de atividades e sessões.
4. Primeira integração com Git e execução de testes.
5. Registro de execuções de IA com os metadados efetivamente disponíveis.
6. Persistência de documentos e referências.
7. Métricas iniciais com proveniência.
8. Consulta e relatório simples, inicialmente sem agente autônomo.

## Critério de priorização

Uma capacidade deve entrar no MVP quando:
- responde a uma pergunta concreta de engenharia;
- tem fonte de dados identificável;
- pode ser validada;
- possui custo de implementação e manutenção proporcional ao valor;
- não exige pressupor dados que as ferramentas de origem não fornecem.
