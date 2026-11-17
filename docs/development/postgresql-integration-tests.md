# Testes de integração com PostgreSQL

Os testes PostgreSQL usam um container e um volume exclusivos, definidos em `docker-compose.test.yml`. Eles não usam o serviço ou o volume de desenvolvimento.

1. Copie `.env.test.example` para `.env.test` e defina uma senha local exclusiva para testes.
2. Inicie a base isolada:

   ```powershell
   docker compose --env-file .env.test -f docker-compose.test.yml -p eip-integration-test up -d --wait
   ```

3. Carregue as variáveis no processo atual e execute o perfil PostgreSQL:

   ```powershell
   Get-Content .env.test | Where-Object { $_ -and -not $_.StartsWith('#') } | ForEach-Object { $name, $value = $_ -split '=', 2; [Environment]::SetEnvironmentVariable($name, $value, 'Process') }
   mvn test '-Dspring.profiles.active=postgres'
   ```

O perfil aceita `EIP_TEST_DATABASE_URL`, `EIP_TEST_DATABASE_USERNAME` e `EIP_TEST_DATABASE_PASSWORD`. As variáveis `EIP_DATABASE_*` continuam como compatibilidade para execução configurada externamente.

O PostgreSQL aplica usuário e senha somente na primeira inicialização do volume. Se as credenciais de `.env.test` forem alteradas depois disso, descarte exclusivamente o volume de testes e recrie o serviço:

```powershell
docker compose --env-file .env.test -f docker-compose.test.yml -p eip-integration-test down -v
docker compose --env-file .env.test -f docker-compose.test.yml -p eip-integration-test up -d --wait
```

O comando `down -v` remove permanentemente os dados do banco de integração; não o execute contra o Compose de desenvolvimento.
