# Modelo de Conhecimento de Engenharia

## Princípio

A memória da EIP é composta por duas camadas complementares:

- **Memória baseada em evidências:** dados estruturados, eventos, métricas, relações e resultados observados.
- **Memória documental:** documentos que preservam intenção, contexto, justificativas, restrições e explicações.

A camada de conhecimento relaciona ambas, sem confundir evidência com interpretação.

## Categorias de conhecimento

### 1. Conhecimento técnico
Arquiteturas, padrões, tecnologias, integrações, soluções implementadas, limitações e consequências técnicas.

### 2. Conhecimento decisório
ADRs, alternativas consideradas, critérios, justificativas, responsáveis, data, versão e condições que motivaram a decisão.

### 3. Conhecimento operacional
Incidentes, falhas de build, problemas de ambiente, recuperações, causas confirmadas e ações corretivas.

### 4. Conhecimento de desempenho
Tempos, custos, qualidade, retrabalho, estabilidade e evolução de métricas.

### 5. Conhecimento sobre IA
Modelos, versões, prompts, ferramentas, contexto, tokens, latência, custo, intervenções humanas e avaliação de resultado.

## Registro de aprendizado

Sempre que viável, registrar a cadeia:

1. problema ou objetivo;
2. contexto e restrições;
3. alternativas consideradas;
4. recomendação (se houver);
5. decisão humana;
6. execução/implementação;
7. evidências de validação;
8. resultado posterior;
9. avaliação do aprendizado;
10. condições de reutilização e limitações.

## Estrutura de um insight

Um insight persistido deve conter:
- título e descrição;
- pergunta que motivou a análise;
- período e projetos abrangidos;
- evidências utilizadas;
- documentos contextuais utilizados;
- método de análise;
- distinção entre observação, inferência e hipótese;
- limitações e possíveis vieses;
- nível de confiança e justificativa;
- modelo/agente e versão, quando gerado por IA;
- estado de revisão humana;
- data de criação e validade/reavaliação.

## Regras para a IA

- Não afirmar que um documento comprova um evento operacional se não houver evidência.
- Não apresentar uma métrica sem definição e origem.
- Não converter ausência de dados em resultado negativo ou zero.
- Não inferir causalidade a partir de correlação.
- Explicitar tamanho da amostra, período e filtros relevantes.
- Distinguir conhecimento específico de um projeto de padrões observados em vários projetos.
- Tratar recomendações antigas como hipóteses até que seu resultado seja avaliado.

## Ciclo de aprendizado

Recomendação → decisão humana → execução → validação → resultado observado → avaliação → conhecimento reutilizável.

A EIP deve armazenar tanto recomendações aceitas quanto rejeitadas, incluindo justificativas, pois a rejeição também pode ser informativa.
