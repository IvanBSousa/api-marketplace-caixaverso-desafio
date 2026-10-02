package br.edu.fiap.marketplace.exception;

/** Erro 404 usado quando o usuário solicitado não existe. */
public class ProdutoNaoEncontradoException extends RecursoNaoEncontradoException {
    public ProdutoNaoEncontradoException(Long id) {
        super("Produto não encontrado: " + id);
    }
}
