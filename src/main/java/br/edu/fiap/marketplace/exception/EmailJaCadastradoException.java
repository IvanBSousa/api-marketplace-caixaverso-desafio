package br.edu.fiap.marketplace.exception;

/** Erro 409 que impede duas credenciais com o mesmo e-mail. */
public class EmailJaCadastradoException extends ConflitoDeNegocioException {
    public EmailJaCadastradoException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}
