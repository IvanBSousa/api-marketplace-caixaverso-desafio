package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.service.CatalogoProdutoService;
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

    @PostMapping
    public ResponseEntity<CatalogoProdutoResponse> cadastrar(@Valid @RequestBody CatalogoProdutoRequest request) {
        CatalogoProdutoResponse response = service.cadastrar(request);
        return ResponseEntity
                .created(URI.create("/api/produtos" + response.id()))
                .body(response);
    }
}
