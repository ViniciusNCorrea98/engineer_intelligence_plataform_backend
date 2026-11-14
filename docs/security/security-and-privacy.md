# Segurança e Privacidade

## Princípios

- Menor privilégio.
- Minimização de dados.
- Segredos nunca devem ser persistidos em eventos ou documentos.
- Isolamento de dados entre projetos e usuários autorizados.
- Auditoria de operações relevantes.
- Retenção explícita e descarte seguro.

## Dados de IA

Prompts, respostas e contexto podem conter código proprietário, credenciais ou dados pessoais. Por padrão:
- coletar metadados operacionais antes de coletar conteúdo textual;
- permitir configuração por fonte/projeto;
- sanitizar segredos antes do envio;
- classificar sensibilidade;
- limitar acesso e retenção;
- evitar enviar documentos a provedores externos sem política explícita;
- registrar provedor/modelo e política de tratamento aplicada.

## Ameaças a considerar no desenho

- credencial de coletor comprometida;
- evento adulterado ou duplicado;
- vazamento entre projetos;
- prompt injection em documentos ou conteúdo coletado;
- conteúdo malicioso em logs e arquivos;
- acesso excessivo do agente a ferramentas;
- exposição acidental de segredos em relatórios.

## Controles de desenho

- autenticação e escopo de credenciais;
- validação de payload e limites de tamanho;
- sanitização e classificação;
- autorização por projeto;
- segregação de ferramentas do agente;
- consultas parametrizadas e ferramentas de domínio;
- auditoria e rate limiting;
- gestão de segredos fora do repositório;
- backup criptografado e política de retenção.

## Identidade de serviço e conteúdo sensível

- Cada ServiceIdentity pertence a um único projeto e recebe somente as fontes e os escopos necessários.
- Credenciais de coletores são opacas, rotacionáveis e armazenadas somente como hash; segredos nunca são registrados em logs, eventos, documentos ou auditoria.
- O servidor valida a identidade autenticada contra o projeto e a ProjectSource autorizados. `project_id` informado pelo coletor não comprova autorização.
- `VIEWER` não pode acessar prompts, respostas, documentos ou outros conteúdos classificados como sensíveis por padrão. Esse acesso requer autorização específica definida em evolução posterior.
- SSO/OIDC, MFA, ABAC e credenciais federadas não fazem parte do MVP, mas a documentação não os impede como evoluções futuras.

## Auditoria de segurança

Auditoria de segurança é distinta de histórico de domínio e logs operacionais:

- **Histórico de domínio** registra fatos de negócio, como uma transferência interna de projeto ou a desativação de uma fonte.
- **Logs operacionais** registram diagnóstico técnico, latência, erros e correlação.
- **Auditoria de segurança** registra quem tentou ou executou uma ação sensível, sobre qual recurso e com qual resultado.

Todo `AuditEvent` deve registrar: ator; tipo de principal/autenticação; ação e resultado; recurso e escopo; timestamp; request/correlation ID; motivo administrativo quando exigido; diferenças antes/depois sem segredos; e origem técnica relevante conforme a política de privacidade.

Devem ser auditadas criação e transferência de projetos dentro da organização, concessão e revogação de acesso, alteração de papéis, criação/suspensão/revogação de identidades de serviço, alteração de escopos, rotação/revogação de credenciais, desativação de fontes e tentativas relevantes de acesso não autorizado.

## Prompt injection e conteúdo não confiável

Documentos, logs, issues, prompts e respostas capturados devem ser tratados como dados não confiáveis, nunca como instruções de sistema. O agente deve usar ferramentas de leitura com escopo e não obedecer instruções encontradas no conteúdo consultado.

## Pendente

Definir classificação formal, períodos de retenção, residência dos dados, requisitos legais aplicáveis e política de uso de provedores externos.

## Referências

- ADR-009 — ServiceIdentity Escopada por Projeto, Fonte e Permissão (`TD-F0-02-004`).
- ADR-010 — Credenciais Opacas, Rotacionáveis e Armazenadas como Hash (`TD-F0-02-005`).
- ADR-011 — RBAC Simples com Papéis Organizacionais e de Projeto (`TD-F0-02-006`).
- ADR-012 — Auditoria de Segurança Separada de Histórico de Domínio e Logs Operacionais (`TD-F0-02-007`).
