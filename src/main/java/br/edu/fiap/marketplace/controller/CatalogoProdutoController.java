package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.service.CatalogoProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<CatalogoProduto> listar() {
        return service.
    }
}
