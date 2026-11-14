# Ciclo de Vida do Conhecimento

## Estados sugeridos

- `captured`: informação recebida ou documento registrado.
- `normalized`: conteúdo validado e normalizado.
- `correlated`: relacionado a entidades relevantes.
- `analyzed`: analisado por regra determinística ou IA.
- `review_required`: necessita validação humana.
- `accepted`: insight/recomendação revisado e aceito.
- `rejected`: rejeitado com justificativa, quando aplicável.
- `superseded`: substituído por conhecimento mais recente.
- `archived`: mantido para consulta histórica, sem uso como recomendação atual.

Esses estados são uma proposta inicial; não precisam ser implementados integralmente no MVP.

## Versionamento e correção

- Preservar o registro original recebido.
- Registrar correções como novas versões ou eventos de correção.
- Manter a definição da métrica usada no momento do cálculo.
- Quando um documento mudar, preservar sua versão ou hash e a referência à versão analisada.
- Permitir que insights sejam reavaliados quando novas evidências surgirem.

## Validade

Conhecimento técnico pode envelhecer. Registrar, quando aplicável, projeto/stack, versão tecnológica, período de validade, condições e data de revisão.
