package br.com.bytebank.repository;

import br.com.bytebank.model.Conta;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Guarda as contas em memoria (sem banco de dados).
 *
 * PADRAO SINGLETON: o Spring cria UMA unica instancia desta classe
 * e entrega a mesma para quem pedir. Por isso o Map abaixo e compartilhado
 * por toda a aplicacao e as contas nao se perdem entre as requisicoes.
 */
@Repository
public class ContaRepository {

    private final Map<Long, Conta> contas = new HashMap<>();
    private long proximoId = 1;

    public Conta criar(String titular, BigDecimal saldoInicial) {
        Conta conta = new Conta(proximoId++, titular, saldoInicial);
        contas.put(conta.getId(), conta);
        return conta;
    }

    public Optional<Conta> buscar(Long id) {
        return Optional.ofNullable(contas.get(id));
    }
}
