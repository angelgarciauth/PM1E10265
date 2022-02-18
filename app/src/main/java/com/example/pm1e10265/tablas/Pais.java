package com.example.pm1e10265.tablas;

import java.io.Serializable;

public class Pais implements Serializable {
    private Integer id;
    private String nombre;
    private Integer codigo;

    public Pais(){

    }

    public Pais(Integer id, String nombre, Integer codigo) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }
}
