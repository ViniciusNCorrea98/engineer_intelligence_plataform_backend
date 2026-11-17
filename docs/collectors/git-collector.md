# Coletor Git

O primeiro coletor executável da EIP observa o `HEAD` de um repositório Git local e envia um evento `git_commit_created` para a API autenticada de ingestão. Ele é intencionalmente *one-shot*: pode ser executado após um commit, em um hook controlado ou em uma etapa de CI.

## Pré-requisitos

- Java 21, Maven e Git disponíveis no `PATH`.
- Backend EIP em execução.
- Projeto com uma `ProjectSource` cujo `sourceKey` seja `git`.
- `ServiceIdentity` ativa, credencial emitida e grant `EVENTS_INGEST` para a fonte Git.

Copie `.env.git-collector.example` para um arquivo local ignorado pelo Git e configure:

| Variável | Obrigatória | Uso |
|---|---:|---|
| `EIP_API_URL` | não | Base da API; padrão `http://localhost:8080` |
| `EIP_PROJECT_ID` | sim | UUID do projeto proprietário da evidência |
| `EIP_SOURCE_KEY` | não | Fonte autorizada; padrão `git` |
| `EIP_SERVICE_TOKEN` | sim | Credencial opaca da `ServiceIdentity` |
| `EIP_GIT_REPOSITORY_PATH` | não | Repositório local; padrão é o diretório atual |
| `EIP_GIT_REPOSITORY_REFERENCE` | sim | Identificador estável do repositório, preferencialmente sua URL canônica |
| `EIP_ACTIVITY_ID` | não | Atividade declarada para correlação |
| `EIP_CORRELATION_CONFIDENCE` | não | Confiança entre `0` e `1`; exige uma atividade |

No PowerShell, carregue as variáveis e execute:

```powershell
Get-Content .env.git-collector | Where-Object { $_ -and -not $_.StartsWith('#') } | ForEach-Object { $name, $value = $_ -split '=', 2; [Environment]::SetEnvironmentVariable($name, $value, 'Process') }
mvn -q exec:java '-Dexec.mainClass=io.eip.collector.git.GitCollectorApplication'
```

Uma execução bem-sucedida informa somente o identificador da evidência, o SHA e se o evento já existia. O token nunca é impresso.

## Evidência produzida

O coletor envia o contrato HTTP `1.0` aceito pela API atual:

```json
{
  "schemaVersion": "1.0",
  "projectId": "7a6757c2-7e2e-4b4a-9cb5-955e46d3ed17",
  "sourceKey": "git",
  "eventId": "git_commit_created:<repository-hash>:<commit-sha>",
  "eventType": "git_commit_created",
  "occurredAt": "2026-11-17T12:00:00Z",
  "observedAt": "2026-11-17T12:00:03Z",
  "repositoryReference": "https://example.com/organization/repository.git",
  "attributes": {
    "commit_sha": "<commit-sha>",
    "tree_sha": "<tree-sha>",
    "parent_shas": [],
    "branch": "main"
  }
}
```

O `eventId` e a chave de idempotência combinam tipo, hash da referência do repositório e SHA do commit. Isso mantém retries determinísticos e evita colisões quando dois repositórios do mesmo projeto contêm o mesmo commit.

Por minimização de dados, a primeira versão não coleta autor, e-mail nem mensagem do commit. Em `HEAD` destacado, `branch` é omitido. Se uma atividade for configurada, a correlação entra como `DECLARED` e continua sujeita à confirmação na plataforma.
