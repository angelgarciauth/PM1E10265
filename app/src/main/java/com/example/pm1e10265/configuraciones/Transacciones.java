package com.example.pm1e10265.configuraciones;

public class Transacciones {
    //Nombre de la base de datos
    public static final String NameDatabase = "PM1E10265";

    //Creacion de las tablas de la base de datos

    public static final String tablacontactos = "contactos";
    public static final String tablapais = "pais";

    /*
    *
    * Campos especificos de la tabla contactos
    * */

    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String pathImage = "imagepath";
    public static final String imagen = "imagen";

    //Transacciones DDL (data definition languaje)

    public static final String idpais = "idpais";
    public static final String nombrepais = "nombrepais";
    public static final String codigopais = "codigopais";

    public static final String CreateTableContactos = "CREATE TABLE "+tablacontactos + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                                                        "pais TEXT,nombre TEXT, telefono INTEGER, nota TEXT, imagepath TEXT, imagen BLOB)";

    public static final String DropTablecontactos = "DROP TABLE IF EXISTS "+ tablacontactos;

    public static final String CreateTablePais = "CREATE TABLE "+tablapais + "(idpais INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nombrepais TEXT, codigopais INTEGER)";

    public static final String DropTablePais = "DROP TABLE IF EXISTS "+ tablapais;


}
