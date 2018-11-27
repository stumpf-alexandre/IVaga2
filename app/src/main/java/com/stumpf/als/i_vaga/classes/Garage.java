package com.stumpf.als.i_vaga.classes;
public class Garage {
    private String keyGaragem;
    private String rua;
    private long numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private Double valor;
    private String foreingnKeyUser;
    private boolean garagem;
    public String getKeyGaragem() {
        return keyGaragem;
    }
    public void setKeyGaragem(String keyGaragem) {
        this.keyGaragem = keyGaragem;
    }
    public String getRua() {
        return rua;
    }
    public void setRua(String rua) {
        this.rua = rua;
    }
    public long getNumero() {
        return numero;
    }
    public void setNumero(long numero) {
        this.numero = numero;
    }
    public String getComplemento() {
        return complemento;
    }
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public Double getValor() {
        return valor;
    }
    public void setValor(Double valor) {
        this.valor = valor;
    }
    public boolean getGaragem() {
        return garagem;
    }
    public void setGaragem(boolean garagem) {
        this.garagem = garagem;
    }
    public String getForeingnKeyUser() {
        return foreingnKeyUser;
    }
    public void setForeingnKeyUser(String foreingnKeyUser) {
        this.foreingnKeyUser = foreingnKeyUser;
    }
}