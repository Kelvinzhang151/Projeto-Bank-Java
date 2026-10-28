package br.com.bytebank.strategy;

import br.com.bytebank.exception.NegocioException;
import br.com.bytebank.model.Conta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia de saque: subtrai o valor do saldo, mas so se houver saldo suficiente.
 */
@Component
public class SaqueStrategy implements OperacaoStrategy {

    @Override
    public void executar(Conta conta, BigDecimal valor) {
        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new NegocioException("ERRO: SALDO INSUFICIENTE");
        }
        conta.debitar(valor);
    }
}
