package com.stumpf.als.i_vaga.classes;
public class Car {
    private String keyCar;
    private String placa;
    private String foreignKeyUser;
    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }
    public String getKeyCar() {
        return keyCar;
    }
    public void setKeyCar(String keyCar) {
        this.keyCar = keyCar;
    }
    public String getForeignKeyUser() {
        return foreignKeyUser;
    }
    public void setForeignKeyUser(String foreignKeyUser) {
        this.foreignKeyUser = foreignKeyUser;
    }
}