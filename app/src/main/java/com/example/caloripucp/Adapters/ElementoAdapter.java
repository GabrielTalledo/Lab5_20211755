package com.example.caloripucp.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Beans.Elemento;
import com.example.caloripucp.R;

import java.util.ArrayList;
import java.util.List;

public class ElementoAdapter extends RecyclerView.Adapter<ElementoAdapter.ElementoViewHolder> {
    private List<Elemento> elementos = new ArrayList<>();
    private AppActivity appActivity;
    private boolean edicion = true;


    public ElementoAdapter(AppActivity appActivity) {
        this.appActivity = appActivity;
    }

    public ElementoAdapter(AppActivity appActivity,boolean edicion) {
        this.appActivity = appActivity;
        this.edicion = edicion;
    }

    @Override
    public ElementoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elemento, parent, false);
        return new ElementoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ElementoViewHolder holder, int position) {
        Elemento elemento = elementos.get(position);
        holder.bind(elemento,holder);
        if(edicion){
            holder.btnEliminar.setOnClickListener((view) -> {
                if (appActivity != null) {
                    appActivity.eliminarElementoRegistroDiario(position);
                }
            });
        }else{
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public void setElementos(List<Elemento> elementos) {
        this.elementos = elementos;
        notifyDataSetChanged();
    }

    class ElementoViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreTextView;
        private TextView caloriasTextView;
        private TextView horaTextView;
        private TextView tipoTextView;
        private LinearLayout adorno1;
        private LinearLayout adorno2;
        private View adornopos;
        private Button btnEliminar;

        ElementoViewHolder(View itemView) {
            super(itemView);
            tipoTextView = itemView.findViewById(R.id.text_tipo_elemento);
            nombreTextView = itemView.findViewById(R.id.text_nombre_elemento);
            caloriasTextView = itemView.findViewById(R.id.text_calorias_elemento);
            horaTextView = itemView.findViewById(R.id.text_hora_elemento);
            adorno1 = itemView.findViewById(R.id.adorno1);
            adorno2 = itemView.findViewById(R.id.adorno2);
            adornopos = itemView.findViewById(R.id.adorno_pos);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_elemento);
        }

        void bind(Elemento elemento, ElementoViewHolder holder) {
            nombreTextView.setText(elemento.getNombre());
            caloriasTextView.setText(String.valueOf(elemento.getCalorias() + " Kcal"));
            horaTextView.setText(elemento.getHora());
            if(elemento.getTipo().equals("Comida")){
                tipoTextView.setText("COMIDA");
                adornopos.setBackgroundResource(R.color.md_theme_inversePrimary_highContrast);
                adorno1.setBackgroundResource(R.color.md_theme_inverseOnSurface);
                adorno2.setBackgroundResource(R.color.md_theme_inverseOnSurface);
                btnEliminar.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.md_theme_inversePrimary_mediumContrast));
            }else{
                tipoTextView.setText("EJERCICIO");
                adornopos.setBackgroundResource(R.color.md_theme_tertiaryFixedDim);
                adorno1.setBackgroundResource(R.color.md_theme_tertiaryFixed);
                adorno2.setBackgroundResource(R.color.md_theme_tertiaryFixed);
                btnEliminar.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.md_theme_tertiaryContainer_mediumContrast));
            }

        }
    }
}

