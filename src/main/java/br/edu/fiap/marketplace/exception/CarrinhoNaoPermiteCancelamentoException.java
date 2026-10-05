package br.edu.fiap.marketplace.exception;

public class CarrinhoNaoPermiteCancelamentoException extends ConflitoDeNegocioException {
    public CarrinhoNaoPermiteCancelamentoException(Long id) {
        super("O carrinho id " + id + "não pode ser cancelado.");
    }
}
