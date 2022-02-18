package com.example.pm1e10265.tablas;

import java.io.Serializable;

public class Pais implements Serializable {
    private Integer idP;
    private String nombrePais;
    private Integer codigoPais;

    public Pais(){

    }

    public Pais(Integer idP, String nombrePais, Integer codigoPais) {
        this.idP = idP;
        this.nombrePais = nombrePais;
        this.codigoPais = codigoPais;
    }

    public Integer getIdP() {
        return idP;
    }

    public void setIdP(Integer idP) {
        this.idP = idP;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public Integer getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(Integer codigoPais) {
        this.codigoPais = codigoPais;
    }
}
