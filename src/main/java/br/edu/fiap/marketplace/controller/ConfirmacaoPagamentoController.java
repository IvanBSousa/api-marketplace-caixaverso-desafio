package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;


/**
 * Rotas protegidas para criação e consulta de confirmações de pagamento.
 * Requer autenticação via JWT (Bearer token).
 */
@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class ConfirmacaoPagamentoController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmacaoPagamentoController.class);

    private final ConfirmacaoPagamentoService pagamentoService;

    public ConfirmacaoPagamentoController(ConfirmacaoPagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovar(@PathVariable Long id) {
        pagamentoService.aprovar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<Void> recusar(
            @PathVariable Long id) {

        pagamentoService.recusar(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Cria uma nova confirmação de pagamento com status PENDENTE.
     */
    @PostMapping
    @Operation(
            summary = "Criar confirmação de pagamento",
            description = "Cria uma nova confirmação de pagamento com status PENDENTE")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Confirmação de pagamento criada com sucesso",
                content = @Content(schema = @Schema(implementation = ConfirmacaoPagamentoResponse.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Entrada inválida ou campos obrigatórios faltando"),
        @ApiResponse(
                responseCode = "404",
                description = "Carrinho ou usuário não encontrado"),
        @ApiResponse(
                responseCode = "409",
                description = "Conflito: idPagamento duplicado ou carrinho em estado incompatível"),
        @ApiResponse(
                responseCode = "401",
                description = "Token ausente ou inválido")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> criar(
            @Valid @RequestBody ConfirmacaoPagamentoRequest request) {
        logger.info("POST /api/pagamentos - Recebendo requisição");
        logger.debug("Dados: carrinhoId={}, usuarioId={}, idPagamento={}",
                request.carrinhoId(), request.usuarioId(), request.idPagamento());

        var confirmacao = pagamentoService.criarConfirmacaoPagamento(request);
        var response = ConfirmacaoPagamentoResponse.de(confirmacao);
        logger.info("Pagamento criado com sucesso - ID: {}", confirmacao.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtém uma confirmação de pagamento pelo ID.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obter confirmação de pagamento",
            description = "Retorna os detalhes de uma confirmação de pagamento pelo ID")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Confirmação de pagamento encontrada",
                content = @Content(schema = @Schema(implementation = ConfirmacaoPagamentoResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Pagamento não encontrado"),
        @ApiResponse(
                responseCode = "401",
                description = "Token ausente ou inválido")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> obter(@PathVariable Long id) {
        logger.info("GET /api/pagamentos/{} - Buscando pagamento", id);

        var confirmacao = pagamentoService.obterPorId(id);
        var response = ConfirmacaoPagamentoResponse.de(confirmacao);
        logger.info("Pagamento encontrado: ID={}, status={}", confirmacao.getId(), confirmacao.getStatus());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/aprovar")
    @Operation(
            summary = "Aprovar confirmação de pagamento",
            description = "Aprova uma confirmação de pagamento pelo ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Confirmação de pagamento encontrada",
                    content = @Content(schema = @Schema(implementation = ConfirmacaoPagamentoResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pagamento não encontrado"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> confirmarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.aprovar(id));
    }

    @PatchMapping("/{id}/recusar")
    @Operation(
            summary = "Recusar confirmação de pagamento",
            description = "Recusa uma confirmação de pagamento pelo ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recusa de pagamento encontrada",
                    content = @Content(schema = @Schema(implementation = ConfirmacaoPagamentoResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pagamento não encontrado"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> recusarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.recusar(id));
    }
}
