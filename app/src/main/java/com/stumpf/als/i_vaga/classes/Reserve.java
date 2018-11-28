package com.stumpf.als.i_vaga.classes;

public class Reserve {
    private String keyReserve;
    private String dataInic;
    private String dataFim;
    private String placaReserva;
    private String valorReserva;
    private Boolean reservas;
    private String foreignKeyGarage;
    private String foreignKeyCar;
    public String getKeyReserve() {
        return keyReserve;
    }

    public void setKeyReserve(String keyReserve) {
        this.keyReserve = keyReserve;
    }

    public String getDataInic() {
        return dataInic;
    }

    public void setDataInic(String dataInic) {
        this.dataInic = dataInic;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getPlacaReserva() {
        return placaReserva;
    }

    public void setPlacaReserva(String placaReserva) {
        this.placaReserva = placaReserva;
    }

    public String getValorReserva() {
        return valorReserva;
    }

    public void setValorReserva(String valorReserva) {
        this.valorReserva = valorReserva;
    }

    public Boolean getReservas() {
        return reservas;
    }

    public void setReservas(Boolean reservas) {
        this.reservas = reservas;
    }

    public String getForeignKeyGarage() {
        return foreignKeyGarage;
    }

    public void setForeignKeyGarage(String foreignKeyGarage) {
        this.foreignKeyGarage = foreignKeyGarage;
    }

    public String getForeignKeyCar() {
        return foreignKeyCar;
    }

    public void setForeignKeyCar(String foreignKeyCar) {
        this.foreignKeyCar = foreignKeyCar;
    }
}
