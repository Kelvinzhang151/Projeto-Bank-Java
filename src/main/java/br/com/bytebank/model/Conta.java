package br.com.bytebank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta bancaria simples: tem um numero (id), um titular e um saldo.
 */
public class Conta {

    private final Long id;
    private final String titular;
    private BigDecimal saldo;

    public Conta(Long id, String titular, BigDecimal saldoInicial) {
        this.id = id;
        this.titular = titular;
        this.saldo = saldoInicial.setScale(2, RoundingMode.HALF_UP);
    }

    public void creditar(BigDecimal valor) {
        saldo = saldo.add(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public void debitar(BigDecimal valor) {
        saldo = saldo.subtract(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public String getTitular() {
        return titular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
