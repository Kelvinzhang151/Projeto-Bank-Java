package br.com.bytebank.controller;

import br.com.bytebank.facade.BancoFacade;
import br.com.bytebank.model.Conta;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Porta de entrada da API. Recebe as requisicoes HTTP e repassa para a fachada.
 */
@RestController
@RequestMapping("/contas")
public class ContaController {

    // Dados que chegam no corpo (JSON) das requisicoes
    public record NovaContaRequest(String titular, BigDecimal saldoInicial) {
    }

    public record OperacaoRequest(String tipo, BigDecimal valor) {
    }

    // Dados que a API devolve
    public record ContaResponse(Long id, String titular, BigDecimal saldo) {
        static ContaResponse de(Conta conta) {
            return new ContaResponse(conta.getId(), conta.getTitular(), conta.getSaldo());
        }
    }

    private final BancoFacade banco;

    public ContaController(BancoFacade banco) {
        this.banco = banco;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse abrir(@RequestBody NovaContaRequest requisicao) {
        return ContaResponse.de(banco.abrirConta(requisicao.titular(), requisicao.saldoInicial()));
    }

    @GetMapping("/{id}")
    public ContaResponse consultar(@PathVariable Long id) {
        return ContaResponse.de(banco.buscarConta(id));
    }

    @PostMapping("/{id}/operacoes")
    public ContaResponse operar(@PathVariable Long id, @RequestBody OperacaoRequest requisicao) {
        return ContaResponse.de(banco.operar(id, requisicao.tipo(), requisicao.valor()));
    }
}
