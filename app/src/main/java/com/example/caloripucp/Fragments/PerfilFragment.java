package com.example.caloripucp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Adapters.ElementoAdapter;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class PerfilFragment extends Fragment {

    private RecyclerView rvElementos;
    private ElementoAdapter elementoAdapter;
    private CircularProgressBar progressBar;
    Perfil perfil;
    Button btnIntervalo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Actualizar datos en la UI:
        perfil = Almacenamiento.obtenerPerfilInicial((AppCompatActivity) getActivity());
        actualizarUI(perfil,Almacenamiento.obtenerHistorial((AppCompatActivity) getActivity()).obtenerCantDias(),view);

        // Botón:
        btnIntervalo = view.findViewById(R.id.btn_modificar_noti_perfil);
        btnIntervalo.setOnClickListener(view1 -> {

            if(validarCampos(view)){
                perfil.setIntervaloMotivacionNoti(Integer.parseInt(((TextInputEditText)view.findViewById(R.id.field_modificar_noti_perfil)).getText().toString()));
                Almacenamiento.guardarPerfilInicial(perfil,(AppCompatActivity) getActivity());
                Snackbar.make(((AppActivity) getActivity()).getBinding().getRoot(), "Se modificó el intervalo de motivación!", Snackbar.LENGTH_LONG).show();
                ((TextView)view.findViewById(R.id.text_intervalo_actual_perfil)).setText("Intervalo actual: "+perfil.getIntervaloMotivacionNoti()+" minutos");

            }

        });

        return view;
    }

    // MÉTODOS:

    public void quitarErrores(View view){
        ((TextInputLayout)view.findViewById(R.id.field_modificar_noti_perfil_layout)).setError(null);
    }

    public boolean validarCampos(View view){
        boolean validacion = true;
        quitarErrores(view);
        if(((TextInputEditText)view.findViewById(R.id.field_modificar_noti_perfil)).getText().toString().isEmpty()){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_modificar_noti_perfil_layout)).setError("El campo no puede estar vacío!");
        } else if(Double.parseDouble(((TextInputEditText)view.findViewById(R.id.field_modificar_noti_perfil)).getText().toString()) <= 0){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_modificar_noti_perfil_layout)).setError("El intervalo debe ser un entero positivo!");
        }
        return validacion;
    }

    // Actualizar UI:
    public void actualizarUI(Perfil perfil, int numeroDias,View view) {

        // Perfil
        ((TextView)view.findViewById(R.id.text_nombre_perfil)).setText(""+perfil.getNombre());
        ((TextView)view.findViewById(R.id.text_peso_perfil)).setText(""+perfil.getPeso() + " Kg");
        ((TextView)view.findViewById(R.id.text_altura_perfil)).setText(""+perfil.getAltura()+" cm");
        ((TextView)view.findViewById(R.id.text_edad_perfil)).setText(""+perfil.getEdad()+" años");
        ((TextView)view.findViewById(R.id.text_genero_perfil)).setText(""+perfil.getGenero());
        ((TextView)view.findViewById(R.id.text_objetivo_perfil)).setText(""+perfil.getObjetivo());
        ((TextView)view.findViewById(R.id.text_numero_dia_perfil)).setText(""+numeroDias);
        ((TextView)view.findViewById(R.id.text_caloria_diarias_perfil)).setText(""+perfil.getObjetivoCaloriasDiarias()+" kcal");
        ((TextView)view.findViewById(R.id.text_intervalo_actual_perfil)).setText("Intervalo actual: "+perfil.getIntervaloMotivacionNoti()+" minutos");


    }

}
