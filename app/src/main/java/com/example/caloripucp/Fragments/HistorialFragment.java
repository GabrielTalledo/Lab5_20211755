package com.example.caloripucp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Adapters.ElementoAdapter;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Beans.RegistroHistorial;
import com.example.caloripucp.Dialogs.CatalogoBottomSheetDialog;
import com.example.caloripucp.Dialogs.ComidaBottomSheetDialog;
import com.example.caloripucp.Dialogs.EjercicioBottomSheetDialog;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class HistorialFragment extends Fragment {

    private RecyclerView rvElementos;
    private ElementoAdapter elementoAdapter;
    private CircularProgressBar progressBar;
    private int numeroDia = 1;
    Registro registro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        // Configuracion del RecyclerView:
        rvElementos = view.findViewById(R.id.rv_registro_historial);
        rvElementos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        elementoAdapter = new ElementoAdapter((AppActivity) getActivity(),false);
        rvElementos.setAdapter(elementoAdapter);

        // Configuracion del ProgressBar:
        progressBar = view.findViewById(R.id.progressbar_calorias_historial);

        // Validacion de día:
        LinearLayout layoutValidacion = view.findViewById(R.id.layout_validacion);

        // Obtenemos el registro historial:

        RegistroHistorial historial = Almacenamiento.obtenerHistorial((AppCompatActivity) getActivity());

        if(historial.getHistorial() == null || historial.getHistorial().isEmpty()){
            layoutValidacion.setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.text_numero_dia_historial)).setText("Aún no hay registros en el historial !");
        }else{
            layoutValidacion.setVisibility(View.VISIBLE);

            // Por default, se obtiene el último registro:
            registro = historial.getHistorial().get(historial.getHistorial().size());
            elementoAdapter.setElementos(registro.getElementos());
            rvElementos.scrollToPosition(0);
            actualizarUI(registro,Almacenamiento.obtenerPerfilInicial((AppCompatActivity) getActivity()),view);

            // Ahora, con la barra de búsqueda:
            view.findViewById(R.id.btn_buscar_registros).setOnClickListener(v -> {

                if(validarCampos(view, historial.getHistorial().size())){
                    quitarErrores(view);
                    closeKeyboard();
                    numeroDia = Integer.parseInt(((TextInputEditText)view.findViewById(R.id.field_buscar_registro_historial)).getText().toString());
                    registro = historial.getHistorial().get(numeroDia);
                    elementoAdapter.setElementos(registro.getElementos());
                    rvElementos.scrollToPosition(0);
                    actualizarUI(registro,Almacenamiento.obtenerPerfilInicial((AppCompatActivity) getActivity()),view);
                }

            });

        }

        return view;
    }

    // MÉTODOS:

    public void quitarErrores(View view){
        ((TextInputLayout)view.findViewById(R.id.field_buscar_registro_historial_layout)).setError(null);
    }

    public boolean validarCampos(View view, int numRegistros){
        boolean validacion = true;
        quitarErrores(view);
        if(((TextInputEditText)view.findViewById(R.id.field_buscar_registro_historial)).getText().toString().isEmpty()){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_buscar_registro_historial_layout)).setError("El campo no puede estar vacío!");
        } else if(Double.parseDouble(((TextInputEditText)view.findViewById(R.id.field_buscar_registro_historial)).getText().toString()) <= 0){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_buscar_registro_historial_layout)).setError("El número de día debe ser un entero positivo!");
        } else if(Double.parseDouble(((TextInputEditText)view.findViewById(R.id.field_buscar_registro_historial)).getText().toString()) > numRegistros){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_buscar_registro_historial_layout)).setError("El número de día no existe en los registros!");
        }
        return validacion;
    }

    // Actualizar UI:
    public void actualizarUI(Registro registro, Perfil perfil, View view) {

        // Datos del día:
        ((TextView)view.findViewById(R.id.text_numero_dia_historial)).setText("Día N°" + registro.getNumeroDia());
        ((TextView)view.findViewById(R.id.text_nombre_dia_historial)).setText(registro.getDia());
        ((TextView)view.findViewById(R.id.text_fecha_historial)).setText(registro.getFecha());

        // Datos de calorias:
        if(registro.getElementos().isEmpty()){
            ((TextView)view.findViewById(R.id.text_elementoshow_historial)).setText("No hay elementos!");
        }else{
            ((TextView)view.findViewById(R.id.text_elementoshow_historial)).setText("ELEMENTOS AÑADIDOS");

        }
        ((TextView)view.findViewById(R.id.text_meta_historial)).setText(""+perfil.getObjetivoCaloriasDiarias()+" kcal");
        ((TextView)view.findViewById(R.id.text_caloria_comida_historial)).setText(""+registro.obtenerCaloriasComidas()+" kcal");
        ((TextView)view.findViewById(R.id.text_caloria_ejercicio_historial)).setText(""+registro.obtenerCaloriasEjercicio()+" kcal");
        ((TextView)view.findViewById(R.id.text_caloria_total_historial)).setText(""+registro.obtenerTotalConsumido()+" kcal");
        if(perfil.getObjetivoCaloriasDiarias()-registro.obtenerTotalConsumido() <= 0){
            ((TextView)view.findViewById(R.id.calorias_restantes_historial)).setText(""+(-1*(perfil.getObjetivoCaloriasDiarias()-registro.obtenerTotalConsumido())));
        }else{
            ((TextView)view.findViewById(R.id.calorias_restantes_historial)).setText(""+(perfil.getObjetivoCaloriasDiarias()-registro.obtenerTotalConsumido()));
        }
        float progreso = 100-(((float)registro.obtenerTotalConsumido()/(float)perfil.getObjetivoCaloriasDiarias())*100);
        if(progreso<=0){
            progressBar.setProgress(0);
            ((TextView)view.findViewById(R.id.text_calos_restante_historial)).setText("Kcal restantes!");
            if(progreso<0){
                ((TextView)view.findViewById(R.id.text_calos_restante_historial)).setText("Kcal excedidas!");
                progressBar.setBackgroundProgressBarColor(ContextCompat.getColor(requireContext(), R.color.md_theme_error));
            }
        }else{
            if(progreso<15){
                progressBar.setProgressBarColor(ContextCompat.getColor(requireContext(),R.color.md_theme_tertiaryContainer_mediumContrast));
            }else{
                progressBar.setProgressBarColor(ContextCompat.getColor(requireContext(),R.color.md_theme_primaryContainer_mediumContrast));
            }
            progressBar.setBackgroundProgressBarColor(ContextCompat.getColor(requireContext(),R.color.md_theme_outlineVariant));
            progressBar.setProgress((int) progreso);
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
