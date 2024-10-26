package com.example.caloripucp.Beans;

import java.io.Serializable;

public class Perfil implements Serializable {

    private String nombre;
    private String genero;
    private double edad;
    private double peso;
    private double altura;
    private String actividad;
    private double actividadFactor;
    private String objetivo;
    private double objetivoCalorias;
    private int objetivoCaloriasDiarias;
    private int intervaloMotivacionNoti;

    // Getter y Setters:

    public String getActividad() {
        return actividad;
    }

    public int getIntervaloMotivacionNoti() {
        return intervaloMotivacionNoti;
    }

    public void setIntervaloMotivacionNoti(int intervaloMotivacionNoti) {
        this.intervaloMotivacionNoti = intervaloMotivacionNoti;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public double getActividadFactor() {
        return actividadFactor;
    }

    public void setActividadFactor(double actividadFactor) {
        this.actividadFactor = actividadFactor;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getEdad() {
        return edad;
    }

    public void setEdad(double edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public double getObjetivoCalorias() {
        return objetivoCalorias;
    }

    public void setObjetivoCalorias(double objetivoCalorias) {
        this.objetivoCalorias = objetivoCalorias;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getObjetivoCaloriasDiarias() {
        return objetivoCaloriasDiarias;
    }

    public void setObjetivoCaloriasDiarias(int objetivoCaloriasDiarias) {
        this.objetivoCaloriasDiarias = objetivoCaloriasDiarias;
    }
}
