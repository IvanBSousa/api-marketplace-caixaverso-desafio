package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.AtualizarQuantidadeRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.service.CatalogoProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** TODO criar as rotas públicas de leitura e protegidas de alteração do catálogo. */
@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Catálogo")
public class CatalogoProdutoController {

    private final CatalogoProdutoService service;

    public CatalogoProdutoController(CatalogoProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CatalogoProdutoResponse>> listar() {

        return ResponseEntity.ok(service.listar());

    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter produto por id",
            description = "Retorna o produto pelo ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = CatalogoProdutoResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<CatalogoProdutoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<CatalogoProdutoResponse> cadastrar(@Valid @RequestBody CatalogoProdutoRequest request) {

        CatalogoProdutoResponse response = service.cadastrar(request);

        return ResponseEntity
                .created(URI.create("/api/produtos" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoProdutoResponse> atualizar(@PathVariable("id") Long id, @Valid @RequestBody CatalogoProdutoRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));

    }

    @PatchMapping("/{id}/estoque")
    @Operation(
            summary = "Ajustar o estoque de um produto",
            description = "Ajusta a quantidade de um produto no estoque pelo ID do produto")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Confirmação de pagamento encontrada",
                    content = @Content(schema = @Schema(implementation = CatalogoProdutoResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados informados inválidos"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<CatalogoProdutoResponse> reporEstoque(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarQuantidadeRequest request) {
        return ResponseEntity.ok(service.reporEstoque(id, request));
    }
}
