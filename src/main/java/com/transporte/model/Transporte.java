package com.transporte.model;

public class Transporte {

    public static final double LIMIAR_CARBONO_BAIXO = 0.05;
    public static final double DESCONTO_SUSTENTAVEL = 0.15;

    private int id;
    private String tipo;
    private String nome;
    private int valorPontos;
    private double taxaCarbono;

    public Transporte() {
    }

    public Transporte(int id, String tipo, String nome, int valorPontos, double taxaCarbono) {
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
        this.valorPontos = valorPontos;
        this.taxaCarbono = taxaCarbono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getValorPontos() { return valorPontos; }
    public void setValorPontos(int valorPontos) { this.valorPontos = valorPontos; }

    public double getTaxaCarbono() { return taxaCarbono; }
    public void setTaxaCarbono(double taxaCarbono) { this.taxaCarbono = taxaCarbono; }

    public boolean sustentavel() {
        return taxaCarbono < LIMIAR_CARBONO_BAIXO;
    }

    public int calcularPontosComDesconto() {
        if (!sustentavel()) {
            return valorPontos;
        }
        return valorPontos - (int) (valorPontos * DESCONTO_SUSTENTAVEL);
    }

    @Override
    public String toString() {
        return "Transporte{id=%d, tipo='%s', nome='%s', valorPontos=%d, taxaCarbono=%.3f}"
                .formatted(id, tipo, nome, valorPontos, taxaCarbono);
    }
}
