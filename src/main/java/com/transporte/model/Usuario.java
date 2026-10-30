package com.transporte.model;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String cpf;
    private String dataCadastro;

    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String cpf, String dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.dataCadastro = dataCadastro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public boolean validarCPF() {
        return cpf != null && cpf.replaceAll("\\D", "").length() == 11;
    }

    @Override
    public String toString() {
        return "Usuario{id=%d, nome='%s', email='%s', cpf='%s', dataCadastro='%s'}"
                .formatted(id, nome, email, cpf, dataCadastro);
    }
}
