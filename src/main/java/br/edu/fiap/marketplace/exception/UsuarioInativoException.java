package br.edu.fiap.marketplace.exception;

public class UsuarioInativoException extends ConflitoDeNegocioException {
    public UsuarioInativoException(Long id) {
        super("O usuário com id " + id + " está inativo.");
    }
}
