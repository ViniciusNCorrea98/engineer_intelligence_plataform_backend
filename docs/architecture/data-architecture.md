# Arquitetura de Dados

## Camadas

### 1. Raw / Inbound
Representa o payload recebido, metadados da requisição, contrato e origem. Deve permitir auditoria e reprocessamento controlado. Conteúdo sensível deve ser filtrado antes da persistência.

### 2. Canonical / Normalized
Representa eventos e entidades em um modelo interno comum, preservando o vínculo com o payload e a fonte originais.

### 3. Evidence / Operational
Representa atividades, execuções, alterações, resultados de qualidade, traces, custos e demais fatos consultáveis.

### 4. Analytics
Representa observações de métricas calculadas por definições versionadas e janelas explícitas.

### 5. Knowledge
Representa documentos, decisões, insights, recomendações e resultados avaliados.

## Princípios de persistência

- PostgreSQL como armazenamento inicial.
- Separar payload bruto, modelo normalizado e projeções analíticas logicamente.
- Usar JSONB para atributos específicos de fontes quando a estrutura variar; não transformar todo o domínio em JSON genérico.
- Índices devem refletir consultas reais e ser adicionados com evidência de necessidade.
- Dados derivados devem registrar a versão da regra que os produziu.
- Deduplicação deve usar `event_id` e/ou chave de idempotência definida pelo contrato.
- Não descartar eventos desconhecidos sem registrar rejeição e motivo.

## Documentos

A primeira versão pode armazenar metadados, conteúdo textual sanitizado e hash/versionamento no PostgreSQL. Se volume ou tipo de arquivo justificar, usar armazenamento de objetos, mantendo no banco metadados e referências.

Documentos devem ter:
- identificador;
- título e tipo;
- projeto/escopo;
- origem e URI, quando aplicável;
- versão/hash;
- datas de criação e captura;
- classificação de sensibilidade;
- texto extraído, quando permitido;
- relações com decisões, atividades e insights.

## Retenção

Definir políticas distintas para eventos brutos, dados normalizados, documentos, logs técnicos e conteúdo de IA. A retenção deve considerar necessidade de auditoria, custo, privacidade e possibilidade de reprocessamento.
