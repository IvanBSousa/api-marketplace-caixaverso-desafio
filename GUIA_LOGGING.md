# 📋 GUIA DE LOGGING - IDENTIFICAR ERROS NA API

## 🎯 O que foi implementado

A aplicação agora registra logs em **3 arquivos diferentes** dentro da pasta `logs/` na raiz do projeto:

```
logs/
├── application.log      (logs gerais da aplicação)
├── service.log          (logs específicos da lógica de negócio)
├── error.log            (apenas erros)
└── [data].log.gz        (arquivos antigos compactados)
```

---

## 📍 LOCAL DOS LOGS

Os logs são salvos em:
```
C:\Desenvolvimento\java\cursos_alura_fiap\api-marketplace-caixaverso-desafio\logs\
```

---

## 🔍 COMO VER OS LOGS

### Opção 1: Via IDE (JetBrains IntelliJ)

1. Abra a aplicação na IDE
2. Vá para **View** → **Tool Windows** → **Run**
3. Os logs aparecerão em tempo real na aba inferior
4. Procure por linhas com:
   - `ERROR` em vermelho (erros)
   - `WARN` em amarelo (avisos)
   - `INFO` em verde (informações)
   - `DEBUG` (detalhes da execução)

### Opção 2: Via Terminal / Prompt de Comando

```powershell
# Ver logs em tempo real (Windows)
Get-Content -Path "logs/application.log" -Wait -Tail 50

# Ver logs de erro apenas
Get-Content -Path "logs/error.log" -Tail 100

# Ver logs de serviço apenas
Get-Content -Path "logs/service.log" -Tail 50
```

### Opção 3: Via Visual Studio Code

1. Abra a pasta do projeto
2. Navegue até a pasta `logs/`
3. Clique no arquivo `application.log`
4. O arquivo se abre no editor

---

## 🐛 FLUXO COMPLETO DE LOGS - Exemplo Real

### Quando você faz: `POST /api/pagamentos`

**Console da aplicação mostrará:**

```
2026-10-02 16:25:00.123 [http-nio-8082-exec-1] INFO  br.edu.fiap.marketplace.controller.ConfirmacaoPagamentoController - POST /api/pagamentos - Recebendo requisição
2026-10-02 16:25:00.124 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.controller.ConfirmacaoPagamentoController - Dados: carrinhoId=1, usuarioId=1, idPagamento=PAY-2026-000123

2026-10-02 16:25:00.125 [http-nio-8082-exec-1] INFO  br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - === INICIANDO CRIAÇÃO DE PAGAMENTO ===
2026-10-02 16:25:00.126 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Request recebido - carrinhoId: 1, usuarioId: 1, idPagamento: PAY-2026-000123

2026-10-02 16:25:00.127 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Validando existência do carrinho ID: 1
2026-10-02 16:25:00.128 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Carrinho encontrado: ID=1, usuário=1, status=ABERTO, produto=Notebook Dell

2026-10-02 16:25:00.129 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Validando existência do usuário ID: 1
2026-10-02 16:25:00.130 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Usuário encontrado: ID=1, email=mariana@example.com, ativo=true

2026-10-02 16:25:00.131 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Validando unicidade do idPagamento: PAY-2026-000123
2026-10-02 16:25:00.132 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - idPagamento é único: PAY-2026-000123

2026-10-02 16:25:00.133 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Validando pertencimento - carrinho.usuarioId=1, usuarioId=1
2026-10-02 16:25:00.134 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Validação de pertencimento OK

2026-10-02 16:25:00.135 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Validando status do carrinho: ABERTO
2026-10-02 16:25:00.136 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Carrinho está aberto

2026-10-02 16:25:00.137 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Calculando valor total do carrinho
2026-10-02 16:25:00.138 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Cálculo: 299.90 × 2 = 599.80
2026-10-02 16:25:00.139 [http-nio-8082-exec-1] INFO  br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Valor total calculado: R$ 599.80 (preço: R$ 299.90, quantidade: 2)

2026-10-02 16:25:00.140 [http-nio-8082-exec-1] DEBUG br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - Objeto ConfirmacaoPagamento criado: ...
2026-10-02 16:25:00.141 [http-nio-8082-exec-1] INFO  br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService - === PAGAMENTO CRIADO COM SUCESSO - ID: 20 ===

2026-10-02 16:25:00.142 [http-nio-8082-exec-1] INFO  br.edu.fiap.marketplace.controller.ConfirmacaoPagamentoController - Pagamento criado com sucesso - ID: 20

```

---

## ⚠️ EXEMPLO: ERRO 500 - Como Encontrar a Causa

Se você receber **500 Internal Server Error**, os logs mostram EXATAMENTE o que deu errado:

### Cenário: NullPointerException

**erro.log mostraria:**

```
2026-10-02 16:26:15.500 [http-nio-8082-exec-2] ERROR br.edu.fiap.marketplace.exception.GlobalExceptionHandler - === ERRO NÃO TRATADO ===
java.lang.NullPointerException: Cannot invoke method on null object
    at br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService.validarCarrinhoAberto(ConfirmacaoPagamentoService.java:165)
    at br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService.criarConfirmacaoPagamento(ConfirmacaoPagamentoService.java:69)
    at br.edu.fiap.marketplace.controller.ConfirmacaoPagamentoController.criar(ConfirmacaoPagamentoController.java:94)
    ...

2026-10-02 16:26:15.501 [http-nio-8082-exec-2] ERROR br.edu.fiap.marketplace.exception.GlobalExceptionHandler - Tipo: java.lang.NullPointerException
2026-10-02 16:26:15.502 [http-nio-8082-exec-2] ERROR br.edu.fiap.marketplace.exception.GlobalExceptionHandler - Mensagem: Cannot invoke method on null object
2026-10-02 16:26:15.503 [http-nio-8082-exec-2] ERROR br.edu.fiap.marketplace.exception.GlobalExceptionHandler - Caminho: /api/pagamentos
2026-10-02 16:26:15.504 [http-nio-8082-exec-2] ERROR br.edu.fiap.marketplace.exception.GlobalExceptionHandler - Método: POST
```

**O stack trace revela:**
1. **Arquivo**: `ConfirmacaoPagamentoService.java`
2. **Linha**: 165 (método `validarCarrinhoAberto`)
3. **Problema**: NullPointerException
4. **Causa**: Tentou chamar método em objeto null

---

## 📊 NÍVEIS DE LOG

| Nível | Cor | Quando aparece | Exemplo |
|-------|-----|-----------------|---------|
| **DEBUG** | Azul | Informações detalhadas (dev) | Validação de cada campo |
| **INFO** | Verde | Eventos importantes | Pagamento criado com sucesso |
| **WARN** | Amarelo | Alertas (não é erro) | Recurso não encontrado |
| **ERROR** | Vermelho | Erro grave | Exception não tratada |

---

## 🔧 CONFIGURAÇÃO DOS LOGS

Os logs são configurados em **2 arquivos**:

### 1. `src/main/resources/application.properties`

```properties
logging.level.root=INFO
logging.level.br.edu.fiap.marketplace=DEBUG
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=10
```

- `root=INFO` → Mostra apenas INFO, WARN, ERROR (menos verboso)
- `br.edu.fiap.marketplace=DEBUG` → Mostra DEBUG também para nosso código
- `logs/application.log` → Arquivo onde salvar os logs
- `max-size=10MB` → Rotaciona após 10MB
- `max-history=10` → Mantém 10 arquivos antigos

### 2. `src/main/resources/logback-spring.xml`

Configuração avançada de Logback:
- Define quais pacotes usam qual appender
- Separa logs por severidade (ERROR em arquivo próprio)
- Compacta arquivos antigos em `.gz`

---

## 📈 EXEMPLO PRÁTICO: RASTREANDO ERRO 500

### Situação: POST falhou com 500

**Passo 1**: Abrir `logs/error.log`

```
2026-10-02 19:16:35 [http-nio-8082-exec-3] ERROR ... GlobalExceptionHandler - === ERRO NÃO TRATADO ===
Caused by: java.lang.NullPointerException
    at ... ConfirmacaoPagamentoService.validarCarrinhoExiste:103
```

**Passo 2**: Analisar a linha 103 de `ConfirmacaoPagamentoService`

```java
103 | logger.debug("Validando existência do carrinho ID: {}", carrinhoId);
104 | return carrinhoRepository.findById(carrinhoId)
105 |    .orElseThrow(() -> {
106 |        logger.warn("Carrinho não encontrado: ID={}", carrinhoId);
107 |        return new RecursoNaoEncontradoException(...);
108 |    });
```

**Passo 3**: Verificar logs de DEBUG antes do erro

```
... DEBUG ... Validando existência do carrinho ID: null
```

**Conclusão**: `carrinhoId` está vindo como `null` do controller!

**Solução**: Verificar se `@NotNull` está em `ConfirmacaoPagamentoRequest.carrinhoId()`

---

## 🚀 DICAS IMPORTANTES

1. **Sempre comece pelo `error.log`** se algo falhar
2. **Stack trace completo está em `error.log`**, nunca em `application.log`
3. **Logs giram a cada 10MB** (não crescem indefinidamente)
4. **Arquivos antigos compactam em `.gz`** (economiza espaço)
5. **Procure por `===` para marcar início/fim de operações grandes**

---

## 🔐 Segurança

**Os logs NUNCA incluem:**
- Senhas ou hashes
- Tokens JWT
- Dados sensíveis de clientes

**Os logs SEMPRE incluem:**
- IDs de recursos
- Timestamps
- Stack traces (apenas em error.log)

---

## 📞 Próximos Passos

1. ✅ Execute a aplicação
2. ✅ Faça requisições (POST, GET)
3. ✅ Abra `logs/application.log`
4. ✅ Identifique o exato ponto de falha
5. ✅ Corrija o problema baseado no log

---

**Arquivos do logging:**
- `logback-spring.xml` - Configuração detalhada
- `application.properties` - Configuração simples
- `logs/*` - Arquivos de log em tempo de execução

---

**Data**: 2026-10-02
**Status**: ✅ Logging completo implementado

