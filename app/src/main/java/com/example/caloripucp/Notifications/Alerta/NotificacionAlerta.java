package com.example.caloripucp.Notifications.Alerta;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.R;

public class NotificacionAlerta {

    private Context context;

    public NotificacionAlerta(Context context) {
        this.context = context;
    }

    public void mostrarNotificacionExcesoCalorias(String titulo, String mensaje) {
        String channelId = "alertas_channel";
        if(titulo.isEmpty()){
            titulo = "¡Cuidado! Exceso de Calorías";
            mensaje = "Has superado tu límite de calorías. Considera hacer ejercicio o reducir calorías en tu próxima comida.";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Creamos un Intent para abrir la ApppActivity al tocar la notificación
        Intent intent = new Intent(context, AppActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.alerta)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // mostramos la noti
        notificationManager.notify(1, builder.build());
    }
}

