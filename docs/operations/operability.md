# Operação e Observabilidade

## Objetivo

Operar a EIP de forma previsível e permitir diagnosticar falhas de ingestão, processamento, analytics e inteligência.

## Sinais operacionais

- disponibilidade da API;
- latência e taxa de erro por endpoint;
- eventos recebidos, aceitos, rejeitados e duplicados;
- atraso entre `occurred_at` e `observed_at`;
- fila/volume de processamento pendente, se houver;
- falhas de normalização e correlação;
- duração de cálculos analíticos;
- latência, custo e falhas de chamadas a modelos;
- falhas de integrações externas.

## Logs

Logs estruturados devem conter timestamp, nível, serviço/módulo, request/correlation ID e código de erro. Evitar tokens, prompts completos, credenciais e conteúdo sensível.

## Saúde

Disponibilizar endpoints de liveness/readiness e verificar dependências essenciais sem expor detalhes internos.

## Backup e recuperação

- definir RPO/RTO antes de produção;
- testar restauração, não apenas criação de backup;
- proteger backups com criptografia e controle de acesso;
- documentar migrações e procedimento de rollback;
- preservar integridade entre eventos, métricas e referências documentais.

## Reprocessamento

Reprocessamentos devem ser idempotentes, auditáveis e executados com versão explícita do normalizador ou cálculo. Não sobrescrever silenciosamente resultados históricos.
