package br.edu.fiap.marketplace.exception;

/**
 * Indica que o login não pôde autenticar o usuário.
 *
 * <p>A mensagem não revela se o e-mail existe. Isso reduz a enumeração de
 * contas e mantém a mesma resposta para e-mail, senha ou usuário inativo.</p>
 */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}