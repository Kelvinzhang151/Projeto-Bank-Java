package br.com.bytebank.exception;

/**
 * Excecao de regra de negocio (valor invalido, saldo insuficiente, etc.).
 * A mensagem ja vem pronta para ser mostrada ao usuario.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
