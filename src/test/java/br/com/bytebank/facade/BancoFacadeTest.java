package br.com.bytebank.facade;

import br.com.bytebank.exception.NegocioException;
import br.com.bytebank.model.Conta;
import br.com.bytebank.repository.ContaRepository;
import br.com.bytebank.strategy.DepositoStrategy;
import br.com.bytebank.strategy.SaqueStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BancoFacadeTest {

    private final BancoFacade banco =
            new BancoFacade(new ContaRepository(), new DepositoStrategy(), new SaqueStrategy());

    private Conta contaCom(String saldo) {
        return banco.abrirConta("Ana", new BigDecimal(saldo));
    }

    @Test
    void depositoDeveSomarAoSaldo() {
        Conta conta = contaCom("100");
        banco.operar(conta.getId(), "DEPOSITAR", new BigDecimal("50"));
        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
    }

    @Test
    void saqueDeveSubtrairDoSaldo() {
        Conta conta = contaCom("80");
        banco.operar(conta.getId(), "SACAR", new BigDecimal("30"));
        assertEquals(new BigDecimal("50.00"), conta.getSaldo());
    }

    @Test
    void saqueSemSaldoDeveDarErroENaoAlterarConta() {
        Conta conta = contaCom("20");
        NegocioException erro = assertThrows(NegocioException.class,
                () -> banco.operar(conta.getId(), "SACAR", new BigDecimal("40")));
        assertEquals("ERRO: SALDO INSUFICIENTE", erro.getMessage());
        assertEquals(new BigDecimal("20.00"), conta.getSaldo());
    }

    @Test
    void tipoInvalidoDeveDarErro() {
        Conta conta = contaCom("100");
        NegocioException erro = assertThrows(NegocioException.class,
                () -> banco.operar(conta.getId(), "PIX", new BigDecimal("10")));
        assertEquals("ERRO: OPERACAO INVALIDA", erro.getMessage());
    }

    @Test
    void valorZeroOuNegativoDeveDarErro() {
        Conta conta = contaCom("100");
        NegocioException erro = assertThrows(NegocioException.class,
                () -> banco.operar(conta.getId(), "SACAR", BigDecimal.ZERO));
        assertEquals("ERRO: VALOR INVALIDO", erro.getMessage());
        assertThrows(NegocioException.class,
                () -> banco.operar(conta.getId(), "DEPOSITAR", new BigDecimal("-5")));
        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
    }

    @Test
    void contaInexistenteDeveDarErro() {
        NegocioException erro = assertThrows(NegocioException.class,
                () -> banco.operar(999L, "DEPOSITAR", new BigDecimal("10")));
        assertEquals("ERRO: CONTA NAO ENCONTRADA", erro.getMessage());
    }

    @Test
    void tipoAceitaMinusculasEEspacos() {
        Conta conta = contaCom("80");
        banco.operar(conta.getId(), " sacar ", new BigDecimal("30"));
        assertEquals(new BigDecimal("50.00"), conta.getSaldo());
    }
}
