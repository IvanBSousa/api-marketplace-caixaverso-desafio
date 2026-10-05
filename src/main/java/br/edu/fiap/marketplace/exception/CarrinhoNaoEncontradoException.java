package br.edu.fiap.marketplace.exception;

public class CarrinhoNaoEncontradoException extends RecursoNaoEncontradoException {
    public CarrinhoNaoEncontradoException(Long id) {
        super("Carrinho com id " + id + " não encontrado");
    }
}
