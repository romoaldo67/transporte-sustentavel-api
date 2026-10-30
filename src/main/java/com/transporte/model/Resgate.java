package com.transporte.model;

public class Resgate {

    private int id;
    private CartaoPontos cartaoPontos;
    private Passagem passagem;
    private String dataResgate;
    private int pontosUtilizados;

    public Resgate() {
    }

    public Resgate(int id, CartaoPontos cartaoPontos, Passagem passagem,
                   String dataResgate, int pontosUtilizados) {
        this.id = id;
        this.cartaoPontos = cartaoPontos;
        this.passagem = passagem;
        this.dataResgate = dataResgate;
        this.pontosUtilizados = pontosUtilizados;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public CartaoPontos getCartaoPontos() { return cartaoPontos; }
    public void setCartaoPontos(CartaoPontos cartaoPontos) { this.cartaoPontos = cartaoPontos; }

    public Passagem getPassagem() { return passagem; }
    public void setPassagem(Passagem passagem) { this.passagem = passagem; }

    public String getDataResgate() { return dataResgate; }
    public void setDataResgate(String dataResgate) { this.dataResgate = dataResgate; }

    public int getPontosUtilizados() { return pontosUtilizados; }
    public void setPontosUtilizados(int pontosUtilizados) { this.pontosUtilizados = pontosUtilizados; }

    @Override
    public String toString() {
        return "Resgate{id=%d, data='%s', pontos=%d}".formatted(id, dataResgate, pontosUtilizados);
    }
}
