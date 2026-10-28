package br.com.bytebank.strategy;

import br.com.bytebank.model.Conta;

import java.math.BigDecimal;

/**
 * PADRAO STRATEGY: define "o que" toda operacao bancaria faz,
 * e cada classe que implementa decide "como" fazer.
 * Depositar e sacar sao duas estrategias diferentes para o mesmo contrato.
 */
public interface OperacaoStrategy {

    void executar(Conta conta, BigDecimal valor);
}
