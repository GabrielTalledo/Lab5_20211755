package com.example.caloripucp.Beans;

import java.io.Serializable;
import java.util.HashMap;

public class RegistroHistorial implements Serializable {
    private HashMap<Integer,Registro> historial = new HashMap<>();

    // Métodos:

    public int obtenerCantDias(){
        if(historial == null || historial.isEmpty()){
            return 0;
        }else{
            return historial.size();
        }
    }

    // Getters y Setters:
    public HashMap<Integer, Registro> getHistorial() {
        return historial;
    }

    public void setHistorial(HashMap<Integer, Registro> historial) {
        this.historial = historial;
    }
}
