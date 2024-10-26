package com.example.caloripucp.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Adapters.ElementoAdapter;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Beans.RegistroHistorial;
import com.example.caloripucp.Dialogs.CatalogoBottomSheetDialog;
import com.example.caloripucp.Dialogs.ComidaBottomSheetDialog;
import com.example.caloripucp.Dialogs.EjercicioBottomSheetDialog;
import com.example.caloripucp.Notifications.Alerta.NotificacionAlerta;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.w3c.dom.Text;

public class DiarioFragment extends Fragment {

    private RecyclerView rvElementos;
    private ElementoAdapter elementoAdapter;
    private CircularProgressBar progressBar;
    private final int umbralProgreso = 15;
    private boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diario, container, false);

        // Configuracion del RecyclerView:
        rvElementos = view.findViewById(R.id.rv_registro_diario);
        rvElementos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        elementoAdapter = new ElementoAdapter((AppActivity) getActivity());
        rvElementos.setAdapter(elementoAdapter);

        // Configuracion del ProgressBar:
        progressBar = view.findViewById(R.id.progressbar_calorias_diario);

        // LiveData del registro diario en la actividad principal
        AppActivity mainActivity = (AppActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getRegistroDiarioLiveData().observe(getViewLifecycleOwner(), registro -> {
                // Actualizamos el adaptador cuando cambie el registro
                if(registro != null){
                    elementoAdapter.setElementos(registro.getElementos());
                    rvElementos.scrollToPosition(rvElementos.getAdapter().getItemCount() - 1);
                    actualizarUI(registro,mainActivity.getPerfilLiveData().getValue(),view);
                    // Analizamos si debemos lanzar una alerta
                    if (flag) {
                        float progreso = 100-(((float)registro.obtenerTotalConsumido()/(float)Almacenamiento.obtenerPerfilInicial((AppCompatActivity) getActivity()).getObjetivoCaloriasDiarias())*100);
                        if((int)progreso <= umbralProgreso && (int)progreso > 0){
                            NotificacionAlerta alerta = new NotificacionAlerta(getContext());
                            alerta.mostrarNotificacionExcesoCalorias("¡Atención! Se está acercando a su meta diaria de calorías!","Recuerde registrar su consumo de calorías restante.");
                        }else{
                            if(progreso==0.0){
                                NotificacionAlerta alerta = new NotificacionAlerta(getContext());
                                alerta.mostrarNotificacionExcesoCalorias("Felicidades!","Ha llegado al límite de consumo de calorías.");
                            }else{
                                if((int)progreso<0){
                                    NotificacionAlerta alerta = new NotificacionAlerta(getContext());
                                    alerta.mostrarNotificacionExcesoCalorias("","");
                                }
                            }
                        }
                    }else{
                        flag = true;
                    }

                }else{
                    ((TextView)view.findViewById(R.id.text_elementoshow_diario)).setText("Aún no hay elementos!");
                }
            });
        }

        // Configuramos los botones para abrir los BottomSheetDialog

        // Añadir comida
        view.findViewById(R.id.btn_agregar_comida_diario).setOnClickListener(v -> mostrarComidaDialog());

        // Añadir ejercicio
        view.findViewById(R.id.btn_agregar_ejercicio_diario).setOnClickListener(v -> mostrarEjercicioDialog());

        // Añadir del catálogo
        view.findViewById(R.id.btn_agregar_catalogo_diario).setOnClickListener(v -> mostrarCatalogoDialog());

        // Pasar día pero forzado xd
        view.findViewById(R.id.btn_pasardia_diario).setOnClickListener(v -> pasarDiaForzado(view));


        return view;
    }

    // Pasar al siguiente día de manera forzada:

    public void pasarDiaForzado(View view){

        RegistroHistorial historial = Almacenamiento.obtenerHistorial((AppCompatActivity) getActivity());
        Registro registro = Almacenamiento.obtenerRegistroDiario((AppCompatActivity) getActivity());
        historial.getHistorial().put(registro.getNumeroDia(),registro);

        // Guardamos el historial:
        Almacenamiento.guardarHistorial(historial,(AppCompatActivity) getActivity());

        // Modificamos el registro diario con LiveData:
        AppActivity mainActivity = (AppActivity) getActivity();
        if (mainActivity != null) {
            Registro registroNuevo = new Registro();
            registroNuevo.setearInformacionDia(registro.getNumeroDia()+1);
            mainActivity.agregarRegistroDiario(registroNuevo);
            actualizarUI(registroNuevo, ((AppActivity) getActivity()).getPerfilLiveData().getValue(),view);
        }

    }

    // Actualizar UI:

    public void actualizarUI(Registro registro, Perfil perfil, View view) {

        // Datos del día:
        ((TextView)view.findViewById(R.id.text_numero_dia_diario)).setText("Día N°" + registro.getNumeroDia());
        ((TextView)view.findViewById(R.id.text_nombre_dia_diario)).setText(registro.getDia());
        ((TextView)view.findViewById(R.id.text_fecha_diario)).setText(registro.getFecha());

        // Datos de calorias:
        if(registro.getElementos().isEmpty()){
            ((TextView)view.findViewById(R.id.text_elementoshow_diario)).setText("Aún no hay elementos!");
        }else{
            ((TextView)view.findViewById(R.id.text_elementoshow_diario)).setText("ELEMENTOS AÑADIDOS");

        }
        ((TextView)view.findViewById(R.id.text_meta_diario)).setText(""+perfil.getObjetivoCaloriasDiarias()+" kcal");
        ((TextView)view.findViewById(R.id.text_caloria_comida_diario)).setText(""+registro.obtenerCaloriasComidas()+" kcal");
        ((TextView)view.findViewById(R.id.text_caloria_ejercicio_diario)).setText(""+registro.obtenerCaloriasEjercicio()+" kcal");
        ((TextView)view.findViewById(R.id.text_caloria_total_diario)).setText(""+registro.obtenerTotalConsumido()+" kcal");
        if(perfil.getObjetivoCaloriasDiarias()-registro.obtenerTotalConsumido() <= 0){
            ((TextView)view.findViewById(R.id.calorias_restantes_diario)).setText(""+(-1*(perfil.getObjetivoCaloriasDiarias()-registro.obtenerTotalConsumido())));
        }else{
            ((TextView)view.findViewById(R.id.calorias_restantes_diario)).setText(""+(perfil.getObjetivoCaloriasDiarias()-registro.obtenerTotalConsumido()));
        }
        float progreso = 100-(((float)registro.obtenerTotalConsumido()/(float)perfil.getObjetivoCaloriasDiarias())*100);
        if(progreso<=0){
            progressBar.setProgress(0);
            ((TextView)view.findViewById(R.id.text_calos_restante)).setText("Kcal restantes!");
            if(progreso<0){
                ((TextView)view.findViewById(R.id.text_calos_restante)).setText("Kcal excedidas!");
                progressBar.setBackgroundProgressBarColor(ContextCompat.getColor(requireContext(), R.color.md_theme_error));
            }
        }else{
            if(progreso<15){
                progressBar.setProgressBarColor(ContextCompat.getColor(requireContext(),R.color.md_theme_tertiaryFixedDim));
            }else{
                ((TextView)view.findViewById(R.id.text_calos_restante)).setText("Kcal restantes");
                progressBar.setProgressBarColor(ContextCompat.getColor(requireContext(),R.color.md_theme_inversePrimary_highContrast));
            }
            progressBar.setBackgroundProgressBarColor(ContextCompat.getColor(requireContext(),R.color.md_theme_outlineVariant_mediumContrast));
            progressBar.setProgress((int) progreso);
        }

    }

    // Bottoms sheets dialogs:
    private void mostrarComidaDialog() {
        ComidaBottomSheetDialog comidaDialog = new ComidaBottomSheetDialog();
        comidaDialog.show(getParentFragmentManager(), "comidaDialog");
    }

    private void mostrarEjercicioDialog() {
        EjercicioBottomSheetDialog ejercicioDialog = new EjercicioBottomSheetDialog();
        ejercicioDialog.show(getParentFragmentManager(), "ejercicioDialog");
    }

    private void mostrarCatalogoDialog() {
        CatalogoBottomSheetDialog catalogoDialog = new CatalogoBottomSheetDialog();
        catalogoDialog.show(getParentFragmentManager(), "catalogoDialog");
    }
}
