package com.transporte.model;

public class Passagem {

    public static final String STATUS_DISPONIVEL = "Disponível";
    public static final String STATUS_RESGATADA = "Resgatada";

    private int id;
    private String origem;
    private String destino;
    private String dataViagem;
    private Transporte transporte;
    private int valorEmPontos;
    private String status;

    public Passagem() {
    }

    public Passagem(int id, String origem, String destino, String dataViagem,
                    Transporte transporte, int valorEmPontos, String status) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.dataViagem = dataViagem;
        this.transporte = transporte;
        this.valorEmPontos = valorEmPontos;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getDataViagem() { return dataViagem; }
    public void setDataViagem(String dataViagem) { this.dataViagem = dataViagem; }

    public Transporte getTransporte() { return transporte; }
    public void setTransporte(Transporte transporte) { this.transporte = transporte; }

    public int getValorEmPontos() { return valorEmPontos; }
    public void setValorEmPontos(int valorEmPontos) { this.valorEmPontos = valorEmPontos; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean estaDisponivel() {
        return STATUS_DISPONIVEL.equals(status);
    }

    @Override
    public String toString() {
        return "Passagem{id=%d, %s->%s, data='%s', pontos=%d, status='%s'}"
                .formatted(id, origem, destino, dataViagem, valorEmPontos, status);
    }
}
