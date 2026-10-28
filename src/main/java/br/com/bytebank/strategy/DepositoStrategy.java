package br.com.bytebank.strategy;

import br.com.bytebank.model.Conta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia de deposito: soma o valor ao saldo.
 */
@Component
public class DepositoStrategy implements OperacaoStrategy {

    @Override
    public void executar(Conta conta, BigDecimal valor) {
        conta.creditar(valor);
    }
}
