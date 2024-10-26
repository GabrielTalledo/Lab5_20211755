package com.example.caloripucp.Beans;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class Elemento implements Serializable {
    private String nombre;
    private int calorias;
    private String hora;
    private String tipo;

    public Elemento() {
    }

    public Elemento(String nombre, int calorias, String tipo) {
        this.nombre = nombre;
        this.calorias = calorias;
        this.tipo = tipo;
    }

    // Métodos:

    public void setearHora(){
        hora = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    // Getters y Setters:

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
