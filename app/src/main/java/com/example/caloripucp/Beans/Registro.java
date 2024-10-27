package com.example.caloripucp.Beans;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Registro implements Serializable {
    private String fecha; // Formato: dd/mm/aaaa
    private String dia; // Lunes, Martes, etc
    private int numeroDia; // 1, 2, 3, etc
    private ArrayList<Elemento> elementos = new ArrayList<>();

    // Métodos:

    public void setearInformacionDia(int numeroDia){
        fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        dia = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(new Date());
        dia = Character.toUpperCase(dia.charAt(0)) + dia.substring(1);
        Log.d("xd", "setearInformacionDia: " + dia);
        this.numeroDia = numeroDia;
    }

    public int obtenerCaloriasComidas(){
        int calorias = 0;
        for(Elemento elemento : elementos){
            if(elemento.getTipo().equals("Comida")){
                calorias += elemento.getCalorias();
            }
        }
        return calorias;
    }

    public int obtenerCaloriasEjercicio(){
        int calorias = 0;
        for(Elemento elemento : elementos){
            if(elemento.getTipo().equals("Ejercicio")){
                calorias += elemento.getCalorias();
            }
        }
        return calorias;
    }

    public int obtenerTotalConsumido(){
        int calorias = 0;
        for(Elemento elemento : elementos){
            if(elemento.getTipo().equals("Comida")){
                calorias += elemento.getCalorias();
            }else{
                calorias -= elemento.getCalorias();
            }
        }
        return calorias;
    }

    public void agregarElemento(Elemento elemento){
        elementos.add(elemento);
    }

    public void eliminarElemento(Elemento elemento){ // Descartado pues no piden esta función!
        elementos.remove(elemento);
    }

    public int obtenerCaloriasConsumidas(){
        int calorias = 0;
        for(Elemento elemento : elementos){
            calorias += elemento.getCalorias();
        }
        return calorias;
    }

    // Getters y Setters:

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public ArrayList<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(ArrayList<Elemento> elementos) {
        this.elementos = elementos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getNumeroDia() {
        return numeroDia;
    }

    public void setNumeroDia(int numeroDia) {
        this.numeroDia = numeroDia;
    }
}
