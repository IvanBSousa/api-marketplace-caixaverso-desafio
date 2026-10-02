package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.repository.CatalogoProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** TODO implementar cadastro, listagem, busca e alterações do catálogo. */
@Service
public class CatalogoProdutoService {

    private final CatalogoProdutoRepository repository;

    public CatalogoProdutoService(CatalogoProdutoRepository repository) {
        this.repository = repository;
    }

    public List<CatalogoProdutoResponse> listar() {
        return repository.findAll().stream().map(CatalogoProdutoResponse::de).toList();
    }
}
