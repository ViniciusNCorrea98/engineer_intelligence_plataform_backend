# Estratégia de Ambientes

## Ambientes previstos

| Ambiente | Finalidade |
|---|---|
| Local | Desenvolvimento e testes exploratórios |
| Test | Testes automatizados e validação de integração |
| Staging | Validação próxima à produção |
| Production | Operação real |

A criação de todos os ambientes pode ser incremental; a separação lógica de configuração deve existir desde o início.

## Configuração

- Configuração por variáveis de ambiente.
- Arquivos `.env` locais não versionados.
- `.env.example` com nomes e valores fictícios.
- Segredos em gerenciador apropriado fora do repositório.
- Migrações versionadas.
- Versões de runtime e dependências fixadas.

## Paridade

Manter versões compatíveis entre ambientes e documentar diferenças intencionais. Não usar dados reais sensíveis em desenvolvimento sem anonimização e autorização.
