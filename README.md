# ByteBank — Padrões de Projeto com Spring Boot

Projeto da atividade de **Padrões de Projeto (GoF) + Spring Framework**.
É uma API simples de conta bancária (abrir conta, depositar e sacar) em que cada padrão tem um motivo claro de existir.

## Padrões usados

| Padrão | Onde está | Por que usei |
|---|---|---|
| **Singleton** | `ContaRepository` (e todos os beans do Spring) | Precisa existir uma única lista de contas na aplicação inteira |
| **Strategy** | `OperacaoStrategy`, `DepositoStrategy`, `SaqueStrategy` | Depositar e sacar são "jeitos diferentes" de fazer uma operação, com o mesmo contrato |
| **Facade** | `BancoFacade` | Junta repositório + estratégias em poucos métodos, e o controller não precisa conhecer os detalhes |

Do **Spring Framework** o projeto usa: injeção de dependência pelo construtor, `@Component`, `@Service`, `@Repository`,
`@RestController` e `@RestControllerAdvice` (tratamento central de erros).

## Estrutura

```
controller/   ContaController          -> recebe as requisições HTTP
facade/       BancoFacade              -> FACADE: orquestra tudo
strategy/     OperacaoStrategy         -> STRATEGY: contrato
              DepositoStrategy         -> STRATEGY: depositar
              SaqueStrategy            -> STRATEGY: sacar
repository/   ContaRepository          -> SINGLETON: guarda as contas em memória
model/        Conta                    -> dados da conta (id, titular, saldo)
exception/    NegocioException         -> erro de regra de negócio
              ApiExceptionHandler      -> transforma o erro em resposta HTTP 400
```

## Como uma requisição funciona

```
POST /contas/1/operacoes   {"tipo":"SACAR","valor":30}
        |
   ContaController          recebe o JSON
        |
   BancoFacade.operar()     1) escolhe a estratégia pelo tipo
        |                   2) valida o valor
        |                   3) busca a conta no repositório
        |
   SaqueStrategy.executar() confere o saldo e debita
        |
   ContaController          devolve a conta atualizada em JSON
```

Se algo der errado em qualquer ponto, uma `NegocioException` é lançada, o saldo **não** é alterado e o
`ApiExceptionHandler` responde com a mensagem de erro.

## Como rodar

Requisitos: Java 17+ e Maven.

```bash
mvn test              # roda os testes
mvn spring-boot:run   # sobe a API em http://localhost:8080
```

## Como usar a API

```bash
# abrir uma conta com R$ 100
curl -X POST localhost:8080/contas -H 'Content-Type: application/json' \
     -d '{"titular":"Ana","saldoInicial":100}'

# depositar R$ 50
curl -X POST localhost:8080/contas/1/operacoes -H 'Content-Type: application/json' \
     -d '{"tipo":"DEPOSITAR","valor":50}'

# sacar R$ 30
curl -X POST localhost:8080/contas/1/operacoes -H 'Content-Type: application/json' \
     -d '{"tipo":"SACAR","valor":30}'

# consultar a conta
curl localhost:8080/contas/1
```

## Mensagens de erro

| Situação | Resposta |
|---|---|
| Tipo diferente de `DEPOSITAR` e `SACAR` | `ERRO: OPERACAO INVALIDA` |
| Valor menor ou igual a zero | `ERRO: VALOR INVALIDO` |
| Saque maior que o saldo | `ERRO: SALDO INSUFICIENTE` |
| Conta que não existe | `ERRO: CONTA NAO ENCONTRADA` |

A ordem de verificação é sempre: tipo, depois valor, depois saldo.

## Roteiro para explicar o projeto

**O que o projeto faz?**
É uma API de conta bancária. Dá para abrir uma conta, depositar, sacar e consultar o saldo. Quando a operação é inválida, o programa devolve uma mensagem de erro e não mexe no saldo.

**Qual padrão você usou e por quê?**
Três. O *Strategy* separa depositar e sacar em classes diferentes que seguem o mesmo contrato (`OperacaoStrategy`). O *Facade* (`BancoFacade`) esconde a complexidade: o controller só chama `operar()`. O *Singleton* aparece no `ContaRepository`: existe uma única instância, então a lista de contas é uma só.

**Onde está o Singleton, se não tem `getInstance()`?**
O Spring cria cada bean uma única vez por padrão (escopo singleton). Quando duas classes pedem o `ContaRepository`, recebem o mesmo objeto. Eu não precisei escrever o `getInstance()` à mão, o Spring faz isso.

**O que é injeção de dependência aqui?**
O `BancoFacade` não faz `new ContaRepository()`. Ele recebe o repositório e as estratégias pelo construtor, e o Spring entrega os objetos prontos.

**Como adicionar uma nova operação, como transferir?**
Criar uma classe `TransferenciaStrategy` que implementa `OperacaoStrategy` e incluir o caso `"TRANSFERIR"` no `escolherEstrategia` do `BancoFacade`. As classes de depósito e saque não mudam.

**Por que o saldo é `BigDecimal` e não `double`?**
Porque `double` tem erro de arredondamento (0.1 + 0.2 não dá exatamente 0.3), e em dinheiro isso não pode acontecer.

**Por que as contas somem quando reinicio a aplicação?**
Elas ficam só na memória, num `Map`. Para guardar de forma permanente, o próximo passo seria trocar o `ContaRepository` por um banco de dados com Spring Data JPA.

**Para que serve o `ApiExceptionHandler`?**
Em vez de tratar o erro em cada método, o `@RestControllerAdvice` captura as `NegocioException` num lugar só e responde com HTTP 400 e a mensagem.
