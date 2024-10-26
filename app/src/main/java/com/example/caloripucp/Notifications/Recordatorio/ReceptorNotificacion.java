package com.example.caloripucp.Notifications.Recordatorio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Notifications.Motivacion.NotificationWorker;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;

public class ReceptorNotificacion extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent.getStringExtra("MotivateWe") != null){
            mostrarNotificacionMotivacional(context);
        }else{
            String titulo = intent.getStringExtra("titulo");
            String mensaje = intent.getStringExtra("mensaje");
            int icono = intent.getIntExtra("icono", R.drawable.motivacion);

            // Intent que nos sirve abrir la AppActivity al tocar la notificación
            Intent appIntent = new Intent(context, AppActivity.class);
            appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appIntent.putExtra("fragmento", intent.getStringExtra("fragmento"));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, appIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Creamos la notificación

            Registro registroDiario = Almacenamiento.obtenerRegistroDiario(context);
            boolean validacion = true;

            Log.d("XDDDD", "CONTROL0 ");


            if(titulo == null || titulo.equals("Recordatorio diario")){
                if(!(registroDiario.getElementos() == null || registroDiario.getElementos().isEmpty())){
                    validacion = false;
                }
            }

            if(validacion){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "recordatorio_channel")
                        .setSmallIcon(icono)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                Notification notificacion = builder.build();

                Log.d("XDDDD", "CONTROL1 ");
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("XDDDD", "CONTROL2 ");
                    notificationManager.notify(mensaje.length(), notificacion);
                }
            }
        }
    }

    private void mostrarNotificacionMotivacional(Context context) {
        String channelId = "motivacion_channel";
        String titulo = "Hora de motivarse";
        String mensaje = "";
        String[] mensajitos = {
                "¡Otro día más cerca de tus metas! ",
                "Cada paso cuenta, cada bocado también. ¡Sigue así!",
                "La alimentación saludable es tu mejor aliado.",
                "¡Hoy es un buen día para moverte!",
                "Recuerda, la constancia es la clave del éxito.",
                "¡Celebra tus pequeños logros! ",
                "La comida sana no tiene por qué ser aburrida ehhh.",
                "¡Combina una dieta equilibrada con ejercicio y verás resultados!",
                "¡Tú puedes con esto y más!",
                "¡Cuida tu cuerpo, es el único que tienes!",
                "La alimentación saludable es una inversión en tu futuro.",
                "¡Disfruta del proceso! La salud es lo más importante.",
                "¡Un cuerpo sano en una mente sana!",
                "La comida es tu combustible, ¡aliméntate bien!",
        };

        mensaje = mensajitos[(int) (Math.random() * mensajitos.length)];

        Log.d("AEA MANO", "mostrarNotificacionMotivacional: xddd asd");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, AppActivity.class);
        intent.putExtra("motivacionFragment","uwu");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.motivacion)
                .setContentTitle(titulo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(67, builder.build());
    }
}

