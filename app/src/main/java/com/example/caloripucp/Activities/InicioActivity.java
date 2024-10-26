package com.example.caloripucp.Activities;
import static android.Manifest.permission.POST_NOTIFICATIONS;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.caloripucp.Beans.Catalogo;
import com.example.caloripucp.Beans.Elemento;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Beans.RegistroHistorial;
import com.example.caloripucp.Fragments.PerfilFragment;
import com.example.caloripucp.Notifications.Motivacion.NotificationWorker;
import com.example.caloripucp.Notifications.Recordatorio.ProgramadorNotificacion;
import com.example.caloripucp.Notifications.Recordatorio.ReceptorNotificacion;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.example.caloripucp.databinding.ActivityInicioBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class InicioActivity extends AppCompatActivity {

    ActivityInicioBinding binding;
    Perfil perfil;
    Catalogo catalogo;
    RegistroHistorial historial;
    Button btn_ingresar;
    TextInputEditText field_nombre;
    TextInputEditText field_peso;
    TextInputEditText field_altura;
    TextInputEditText field_edad;
    AutoCompleteTextView field_genero;
    AutoCompleteTextView field_actividad;
    AutoCompleteTextView field_objetivo;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding:
        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Creamos los canales de notificaciones:
        createNotificationChannel("recordatorio_channel", "Recordatorios", "Notificaciones de recordatorios para las comidas del día.");
        createNotificationChannel("alertas_channel", "Alertas", "Notificaciones de alertas sobre el consumo de calorías durante el día.");
        createNotificationChannel("motivacion_channel", "Motivación", "Notificaciones con mensajes de motivación durante el día.");

        // Ya cuenta con un perfil?

        if(Almacenamiento.obtenerPerfilInicial(this) != null){
            startActivity(new Intent(this,AppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            supportFinishAfterTransition();
        }

        // Lógica:
        perfil = new Perfil();
        catalogo = new Catalogo();
        catalogo.setCatalogoAlimentos(crearCatalogoAlimentos());
        historial = new RegistroHistorial();

        // Inputs
        btn_ingresar = binding.btnIngresar;
        field_nombre = binding.fieldNombre;
        field_peso = binding.fieldPeso;
        field_altura = binding.fieldAltura;
        field_edad = binding.fieldEdad;
        field_genero = binding.fieldGenero;
        field_actividad = binding.fieldActividad;
        field_objetivo = binding.fieldObjetivo;

        String[] generos = new String[]{"Hombre", "Mujer"};
        String[] actividades = new String[]{"Sedentario", "Poco Activo", "Activo", "Muy Activo", "Super Activo"};
        String[] objetivos = new String[]{"Perder Peso", "Mantener Peso", "Ganar Peso"};

        ArrayAdapter<String> adapter_genero = new ArrayAdapter<>(this, R.layout.item_dropdown, generos);
        ArrayAdapter<String> adapter_actividad = new ArrayAdapter<>(this, R.layout.item_dropdown, actividades);
        ArrayAdapter<String> adapter_objetivos = new ArrayAdapter<>(this, R.layout.item_dropdown, objetivos);

        field_genero.setAdapter(adapter_genero);
        field_actividad.setAdapter(adapter_actividad);
        field_objetivo.setAdapter(adapter_objetivos);

        field_genero.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                perfil.setGenero("Hombre");
            }else{
                perfil.setGenero("Mujer");
            }
        });

        field_actividad.setOnItemClickListener((parent, view, position, id) -> {
            Double[] factores = new Double[]{1.2, 1.375, 1.55, 1.725, 1.9};
            perfil.setActividad(actividades[position]);
            perfil.setActividadFactor(factores[position]);
        });

        field_objetivo.setOnItemClickListener((parent, view, position, id) -> {
            Double[] caloriasFactor = new Double[]{-300.0, 0.0, 500.0};
            perfil.setObjetivo(objetivos[position]);
            perfil.setObjetivoCalorias(caloriasFactor[position]);
        });

        // Botón Ingresar:

        btn_ingresar.setOnClickListener(view -> {
            if(validarCampos()){

                // Obtenemos permisos de notificaciones:

                if(!askPermission()){
                    Snackbar.make(binding.getRoot(), "Debe habilitar los permisos de notificaciones!", Snackbar.LENGTH_SHORT).show();
                }else{
                    // Seteamos el perfil inicial:
                    perfil.setNombre(field_nombre.getText().toString());
                    perfil.setPeso(Double.parseDouble(field_peso.getText().toString()));
                    perfil.setAltura(Double.parseDouble(field_altura.getText().toString()));
                    perfil.setEdad(Double.parseDouble(field_edad.getText().toString()));
                    perfil.setObjetivoCaloriasDiarias(calcularCaloriasDiarias());
                    perfil.setIntervaloMotivacionNoti(1);
                    // Creamos el archivo del perfil y guardamos el objeto:
                    Almacenamiento.guardarPerfilInicial(perfil, this);
                    // Creamos el primer registro (el registro actual):
                    Registro registroActual = new Registro();
                    registroActual.setearInformacionDia(1);
                    // Guardamos este primer registro:
                    Almacenamiento.guardarRegistroDiario(registroActual, this);
                    // Guardamos el catalogo:
                    Almacenamiento.guardarCatalogo(catalogo,this);
                    // Guardamos el historial:
                    Almacenamiento.guardarHistorial(historial,this);
                    // Notificaciones de recordatorios personalizados:
                    // Debido a la optimizacion de batería de android, algunas no se muestran correctamente y tampoco en el
                    // tiempo exacto
                    ProgramadorNotificacion.programarRecordatorio(this, 9, 0, "Recordatorio de desayuno", "Recuerda registrar tu delicioso desayuno en la app!","Diario",R.drawable.desayuno);
                    ProgramadorNotificacion.programarRecordatorio(this, 12, 0, "Recordatorio de almuerzo", "No olvides de registrar tu almuerzo nutritivo en la app!","Diario",R.drawable.almuerzo);
                    ProgramadorNotificacion.programarRecordatorio(this, 19, 0, "Recordatorio de cena", "Registra tu cena nocturna en la app, no lo olvides!","Diario",R.drawable.comida2);
                    ProgramadorNotificacion.programarRecordatorio(this, 23, 0, "Recordatorio de fin de día", "No has registrado comidas hoy. Recuerda llevar tu registro para lograr tus metas!","Diario",R.drawable.alerta);
                    // Notificaciones de motivación:

                    // Método con worker (solo funciona si la app esta en primer o segundo plano)
                    //WorkManager.getInstance(this).cancelAllWork();
                    //PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, perfil.getIntervaloMotivacionNoti(), TimeUnit.MINUTES).setInitialDelay(perfil.getIntervaloMotivacionNoti(),TimeUnit.MINUTES).build();
                    //WorkManager.getInstance(this).enqueueUniquePeriodicWork("NotificacionesPeriodicas", ExistingPeriodicWorkPolicy.REPLACE,notificationWorkRequest);

                    alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
                    programarNotificacionMotivacion();

                    // Pasamos a la app:
                    startActivity(new Intent(this,AppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    supportFinishAfterTransition();
                }
            }

        });

    }


    // ---------
    //  MÉTODOS:
    // ---------

    // Notificaciones:

    private void programarNotificacionMotivacion() {
        Intent intent = new Intent(this, ReceptorNotificacion.class);
        intent.putExtra("MotivateWe", "GG");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Establecemos la alarma para que se repita cada 1 minuto
        long interval = perfil.getIntervaloMotivacionNoti()*60*1000; // en milisegundos
        long triggerTime = System.currentTimeMillis() + interval; // Primero se ejecuta despues del tiempo del intervalor
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
    }

    public void createNotificationChannel(String channelId,String channelName,String channelDescription) {
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(channelDescription);
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        askPermission();
    }

    public boolean askPermission() {
        boolean permiso = true;
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(InicioActivity.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
        if(ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){
            permiso = false;
        }
        return permiso;
    }

    // Otros:

    public ArrayList<Elemento> crearCatalogoAlimentos() {
        ArrayList<Elemento> alimentos = new ArrayList<>();

        // Agregamos 5 alimentos de ejemplo
        alimentos.add(new Elemento("Manzana", 115, "Comida"));
        alimentos.add(new Elemento("Avena", 389, "Comida"));
        alimentos.add(new Elemento("Salmón", 206, "Comida"));
        alimentos.add(new Elemento("Ensalada", 190, "Comida"));
        alimentos.add(new Elemento("Palta", 320, "Comida"));

        return alimentos;
    }

    public int calcularCaloriasDiarias(){
        double caloriasDiarias;

        if(perfil.getGenero().equals("Hombre")){
            caloriasDiarias = (66.47 + (13.75 * perfil.getPeso()) + (5.0 * perfil.getAltura()) - (6.74 * perfil.getEdad()))*perfil.getActividadFactor() + perfil.getObjetivoCalorias();
        }else {
            caloriasDiarias = (655.1 + (9.56 * perfil.getPeso()) + (1.85 * perfil.getAltura()) - (4.68 * perfil.getEdad()))*perfil.getActividadFactor() + perfil.getObjetivoCalorias();
        }

        return (int) caloriasDiarias;
    }

    public void quitarErrores(){
        binding.fieldNombreLayout.setError(null);
        binding.fieldPesoLayout.setError(null);
        binding.fieldAlturaLayout.setError(null);
        binding.fieldEdadLayout.setError(null);
        binding.fieldGeneroLayout.setError(null);
        binding.fieldActividadLayout.setError(null);
        binding.fieldObjetivoLayout.setError(null);
    }

    public boolean validarCampos(){
        boolean validacion = true;
        quitarErrores();
        if(field_nombre.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldNombreLayout.setError("El campo no puede estar vacío!");
        }
        if(field_peso.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldPesoLayout.setError("El campo no puede estar vacío!");
        } else if(Double.parseDouble(field_peso.getText().toString()) == 0){
            validacion = false;
            binding.fieldPesoLayout.setError("El peso no puede ser 0 Kg!");
        }
        if(field_altura.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldAlturaLayout.setError("El campo no puede estar vacío!");
        }else if(Double.parseDouble(field_altura.getText().toString()) == 0){
            validacion = false;
            binding.fieldAlturaLayout.setError("La altura no puede ser 0 cm!");
        }
        if(field_edad.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldEdadLayout.setError("El campo no puede estar vacío!");
        }else if(Double.parseDouble(field_edad.getText().toString()) == 0){
            validacion = false;
            binding.fieldEdadLayout.setError("La edad no puede ser 0 años!");
        }
        if(field_genero.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldGeneroLayout.setError("Debe elegir un género!");
        }
        if(field_actividad.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldActividadLayout.setError("Debe elegir su nivel de actividad!");
        }
        if(field_objetivo.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldObjetivoLayout.setError("Debe elegir un objetivo!");
        }

        return validacion;
    }

}