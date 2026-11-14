# ADR-012 — Auditoria de Segurança Separada de Histórico de Domínio e Logs Operacionais

- **Status:** Aprovado
- **Data:** 2026-11-13
- **Referência:** TD-F0-02-007

## Contexto

Concessões de acesso, credenciais e operações administrativas exigem reconstrução confiável. Histórico de domínio e logs operacionais não possuem, por si, a semântica ou a retenção necessária para essa finalidade.

## Decisão

Manter `AuditEvent` como registro de auditoria de segurança separado de histórico de domínio e logs operacionais.

Um AuditEvent registra, no mínimo: ator; tipo de principal/autenticação; ação e resultado; recurso e escopo; timestamp; request/correlation ID; motivo administrativo quando exigido; diferenças antes/depois sem segredos; e origem técnica relevante conforme a política de privacidade.

Devem ser auditadas a criação e a transferência de projetos dentro da organização, concessões e revogações de acesso, mudanças de papel, criação/suspensão/revogação de identidades de serviço, alterações de escopos, rotação/revogação de credenciais, desativação de fontes e tentativas relevantes de acesso não autorizado.

## Consequências

- Auditoria possui política de proteção e retenção própria.
- Histórico de domínio continua registrando fatos de negócio, como transferência de projeto ou fonte desativada.
- Logs operacionais continuam destinados a diagnóstico técnico, latência, erros e correlação.
- A auditoria não pode registrar segredos ou conteúdo sensível sem autorização e política explícitas.

