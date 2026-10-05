package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.AtualizarQuantidadeRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.exception.ProdutoNaoEncontradoException;
import br.edu.fiap.marketplace.repository.CatalogoProdutoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CatalogoProdutoResponse atualizar(Long id, CatalogoProdutoRequest request) {

        CatalogoProduto produto = repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setAtivo(request.ativo());

        CatalogoProduto produtoAtualizado = repository.save(produto);


        return CatalogoProdutoResponse.de(repository.save(produtoAtualizado));
    }

    @Transactional(readOnly = true)
    public CatalogoProdutoResponse buscar(Long id) {
        return repository.findById(id)
                .map(CatalogoProdutoResponse::de)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Transactional
    public CatalogoProdutoResponse reporEstoque(Long id, AtualizarQuantidadeRequest request) {
        CatalogoProduto produto = repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        produto.reporEstoque(request.quantidade());

        return CatalogoProdutoResponse.de(repository.saveAndFlush(produto));
    }
}
