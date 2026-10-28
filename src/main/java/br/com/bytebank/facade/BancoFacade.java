package br.com.bytebank.facade;

import br.com.bytebank.exception.NegocioException;
import br.com.bytebank.model.Conta;
import br.com.bytebank.repository.ContaRepository;
import br.com.bytebank.strategy.DepositoStrategy;
import br.com.bytebank.strategy.OperacaoStrategy;
import br.com.bytebank.strategy.SaqueStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * PADRAO FACADE: uma "fachada" que junta varias classes (repositorio e estrategias)
 * em poucos metodos simples. O controller so conversa com a fachada e nao precisa
 * saber como as contas sao guardadas nem qual estrategia usar.
 *
 * As dependencias chegam pelo construtor: isso e INJECAO DE DEPENDENCIA do Spring.
 */
@Service
public class BancoFacade {

    private final ContaRepository repositorio;
    private final DepositoStrategy deposito;
    private final SaqueStrategy saque;

    public BancoFacade(ContaRepository repositorio, DepositoStrategy deposito, SaqueStrategy saque) {
        this.repositorio = repositorio;
        this.deposito = deposito;
        this.saque = saque;
    }

    public Conta abrirConta(String titular, BigDecimal saldoInicial) {
        if (titular == null || titular.isBlank()) {
            throw new NegocioException("ERRO: TITULAR INVALIDO");
        }
        BigDecimal saldo = (saldoInicial == null) ? BigDecimal.ZERO : saldoInicial;
        if (saldo.signum() < 0) {
            throw new NegocioException("ERRO: VALOR INVALIDO");
        }
        return repositorio.criar(titular.trim(), saldo);
    }

    public Conta buscarConta(Long id) {
        return repositorio.buscar(id)
                .orElseThrow(() -> new NegocioException("ERRO: CONTA NAO ENCONTRADA"));
    }

    /**
     * Ordem das validacoes: tipo da operacao, depois valor, depois saldo (no saque).
     * Se qualquer uma falhar, a excecao e lancada e o saldo nao e alterado.
     */
    public Conta operar(Long id, String tipo, BigDecimal valor) {
        OperacaoStrategy estrategia = escolherEstrategia(tipo);
        validarValor(valor);

        Conta conta = buscarConta(id);
        estrategia.executar(conta, valor);
        return conta;
    }

    private OperacaoStrategy escolherEstrategia(String tipo) {
        if (tipo == null) {
            throw new NegocioException("ERRO: OPERACAO INVALIDA");
        }
        switch (tipo.trim().toUpperCase()) {
            case "DEPOSITAR":
                return deposito;
            case "SACAR":
                return saque;
            default:
                throw new NegocioException("ERRO: OPERACAO INVALIDA");
        }
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new NegocioException("ERRO: VALOR INVALIDO");
        }
    }
}
