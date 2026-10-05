package br.edu.fiap.marketplace.exception;

public class ConfirmacaoPagamentoNaoEncontrado extends RecursoNaoEncontradoException {
    public ConfirmacaoPagamentoNaoEncontrado(Long id) {
        super("A confirmação de pagamento com id " + id + " não foi encontrada.");
    }
}
