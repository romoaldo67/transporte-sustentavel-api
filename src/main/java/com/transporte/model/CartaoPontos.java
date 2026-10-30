package com.transporte.model;

public class CartaoPontos {

    private String numeroCartao;
    private int saldoPontos;
    private String dataValidade;
    private Usuario usuario;

    public CartaoPontos() {
    }

    public CartaoPontos(String numeroCartao, int saldoPontos, String dataValidade, Usuario usuario) {
        this.numeroCartao = numeroCartao;
        this.saldoPontos = saldoPontos;
        this.dataValidade = dataValidade;
        this.usuario = usuario;
    }

    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }

    public int getSaldoPontos() { return saldoPontos; }
    public void setSaldoPontos(int saldoPontos) { this.saldoPontos = saldoPontos; }

    public String getDataValidade() { return dataValidade; }
    public void setDataValidade(String dataValidade) { this.dataValidade = dataValidade; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public void adicionarPontos(int pontos) {
        this.saldoPontos += pontos;
    }

    public void debitarPontos(int pontos) {
        this.saldoPontos -= pontos;
    }

    public boolean possuiSaldo(int pontos) {
        return saldoPontos >= pontos;
    }

    @Override
    public String toString() {
        return "CartaoPontos{numero='%s', saldo=%d, validade='%s'}"
                .formatted(numeroCartao, saldoPontos, dataValidade);
    }
}
