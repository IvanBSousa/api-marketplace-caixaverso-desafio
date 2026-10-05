package br.edu.fiap.marketplace.exception;

public class ProdutoInativoException extends ConflitoDeNegocioException {
    public ProdutoInativoException(Long id) {
        super("O produto com id " + id + " está inativo.");
    }
}
