package br.edu.fiap.marketplace.exception;

import java.math.BigDecimal;

public class ValorInformadoInvalidoException extends RuntimeException {
    public ValorInformadoInvalidoException(BigDecimal valor) {super("O valor " + valor + " não pode ser aceito para operação.");}
}
