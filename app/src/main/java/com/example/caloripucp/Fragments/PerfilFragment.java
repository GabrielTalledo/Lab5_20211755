package com.example.caloripucp.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Activities.InicioActivity;
import com.example.caloripucp.Adapters.ElementoAdapter;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Notifications.Motivacion.NotificationWorker;
import com.example.caloripucp.Notifications.Recordatorio.NotificacionProgramadaUtils;
import com.example.caloripucp.Notifications.Recordatorio.ReceptorNotificacion;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.concurrent.TimeUnit;

public class PerfilFragment extends Fragment {

    private RecyclerView rvElementos;
    private ElementoAdapter elementoAdapter;
    private CircularProgressBar progressBar;
    Perfil perfil;
    Button btnIntervalo;
    Button btnBorrar;
    AlarmManager alarmManager;

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
                closeKeyboard();
                perfil.setIntervaloMotivacionNoti(Integer.parseInt(((TextInputEditText)view.findViewById(R.id.field_modificar_noti_perfil)).getText().toString()));
                Almacenamiento.guardarPerfilInicial(perfil,(AppCompatActivity) getActivity());
                Snackbar.make(((AppActivity) getActivity()).getBinding().getRoot(), "Se modificó el intervalo de motivación!", Snackbar.LENGTH_LONG).show();
                ((TextView)view.findViewById(R.id.text_intervalo_actual_perfil)).setText("Intervalo actual: "+perfil.getIntervaloMotivacionNoti()+" minuto"+(perfil.getIntervaloMotivacionNoti()==1?"":"s"));

                // Método con worker (solo funciona si la app esta en primer o segundo plano)
                // Eliminamos los workers anteriores:
                //WorkManager.getInstance(getContext()).cancelAllWork();
                // Creamos workers con el nuevo intervalo:
                //PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, perfil.getIntervaloMotivacionNoti(), TimeUnit.MINUTES).setInitialDelay(perfil.getIntervaloMotivacionNoti(),TimeUnit.MINUTES).build();
                //WorkManager.getInstance(getContext()).enqueueUniquePeriodicWork("NotificacionesPeriodicas", ExistingPeriodicWorkPolicy.REPLACE,notificationWorkRequest);

                alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);
                Intent intent = new Intent(getContext(), ReceptorNotificacion.class);
                intent.putExtra("MotivateWe","GG");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 11, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(pendingIntent);
                programarNotificacionMotivacion();
            }

        });

        btnBorrar = view.findViewById(R.id.btn_eliminar_perfil);
        btnBorrar.setOnClickListener(view1 -> {
            mostrarDialogBorrar(getContext());
        });

        return view;
    }

    // MÉTODOS:

    // Notificaciones:

    private void programarNotificacionMotivacion() {
        Intent intent = new Intent(getContext(), ReceptorNotificacion.class);
        intent.putExtra("MotivateWe", "GG");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 11, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Establecemos la alarma para que se repita cada 1 minuto
        long interval = perfil.getIntervaloMotivacionNoti()*60*1000; // en milisegundos
        long triggerTime = System.currentTimeMillis() + interval; // Primero se ejecuta despues del tiempo del intervalor
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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
        ((TextView)view.findViewById(R.id.text_peso_perfil)).setText(""+(int)perfil.getPeso() + " Kg");
        ((TextView)view.findViewById(R.id.text_altura_perfil)).setText(""+(int)perfil.getAltura()+" cm");
        ((TextView)view.findViewById(R.id.text_edad_perfil)).setText(""+(int)perfil.getEdad()+" años");
        ((TextView)view.findViewById(R.id.text_genero_perfil)).setText(""+perfil.getGenero());
        ((TextView)view.findViewById(R.id.text_objetivo_perfil)).setText(""+perfil.getObjetivo());
        ((TextView)view.findViewById(R.id.text_numero_dia_perfil)).setText(""+numeroDias);
        ((TextView)view.findViewById(R.id.text_caloria_diarias_perfil)).setText(""+perfil.getObjetivoCaloriasDiarias()+" kcal");
        ((TextView)view.findViewById(R.id.text_intervalo_actual_perfil)).setText("Intervalo actual: "+perfil.getIntervaloMotivacionNoti()+" minuto"+(perfil.getIntervaloMotivacionNoti()==1?"":"s"));


    }

    // Mostrar Dialog:
    public void mostrarDialogBorrar(Context context){

        new MaterialAlertDialogBuilder(context)
                .setTitle("Eliminar perfil")
                .setMessage("¿Estás seguro que deseas borrar tu información de perfil? Esta acción no se puede revertir!")
                .setPositiveButton("Aceptar", (dialog, which) -> {

                    // Eliminamos los workers:
                    WorkManager.getInstance(getContext()).cancelAllWorkByTag("motivacion");
                    // Cancelamos las demas notificaciones:
                    NotificacionProgramadaUtils utils = new NotificacionProgramadaUtils(getContext());
                    utils.cancelAllNotifications();
                    Almacenamiento.eliminarPerfilInicial(((AppCompatActivity)getActivity()));
                    startActivity(new Intent(getContext(), InicioActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().supportFinishAfterTransition();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }



}
