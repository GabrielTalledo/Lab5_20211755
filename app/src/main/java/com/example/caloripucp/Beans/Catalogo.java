package com.example.caloripucp.Beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Catalogo implements Serializable {
    private ArrayList<Elemento> catalogoAlimentos = new ArrayList<>();

    // Getters y setters:
    public ArrayList<Elemento> getCatalogoAlimentos() {
        return catalogoAlimentos;
    }
    public void setCatalogoAlimentos(ArrayList<Elemento> catalogo) {
        this.catalogoAlimentos = catalogo;
    }

    // Métodos:

    public String[] obtenerNombresCatalogo() {
        String[] nombres = null;
        if(!catalogoAlimentos.isEmpty()){
            nombres = new String[catalogoAlimentos.size()];
            for (int i = 0; i < catalogoAlimentos.size(); i++) {
                nombres[i] = catalogoAlimentos.get(i).getNombre() + " - " + catalogoAlimentos.get(i).getCalorias() + " Kcal";
            }
        }
        return nombres;
    }

}
