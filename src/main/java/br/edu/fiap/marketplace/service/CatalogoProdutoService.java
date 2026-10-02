package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.repository.CatalogoProdutoRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public CatalogoProdutoResponse cadastrar(CatalogoProdutoRequest request) {

        CatalogoProduto produto = new CatalogoProduto(
                request.nome(),
                request.descricao(),
                request.preco(),
                request.estoque(),
                request.ativo());

        return CatalogoProdutoResponse.de(repository.save(produto));
    }
}
