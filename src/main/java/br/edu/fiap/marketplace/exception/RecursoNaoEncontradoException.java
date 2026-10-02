package br.edu.fiap.marketplace.exception;

/** Classe-base das falhas que deverão produzir HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

