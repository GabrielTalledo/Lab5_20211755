package com.example.caloripucp.Notifications.Recordatorio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ProgramadorNotificacion {

    public static void programarRecordatorio(Context context, int hora, int minuto, String titulo, String mensaje, String fragmento, int icono) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Configuramos el calendario para la hora especificada (por ejemplo, mediodía, 6 PM, 11 PM)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        Log.d("XDDDD", "tiempo sistema: "+System.currentTimeMillis());


        // Por si la hora ya pasó, reprogramamos la notificaccion para el día que sigue
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Log.d("XDDDD", "se aplaza: ");
        }

        // Creamos el intent para el BroadcastReceiver
        Intent intent = new Intent(context, ReceptorNotificacion.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("mensaje", mensaje);
        intent.putExtra("icono", icono);
        intent.putExtra("fragmento", fragmento);
        Log.d("XDDDDDDDDASD", "mensajito length: "+mensaje.length());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mensaje.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Log.d("XDDDD", "TIEMPITO: "+calendar.getTimeInMillis());

        alarmManager.cancel(pendingIntent);

        // por último, programamos la alarma para que se repita diariamente a la misma hora
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("XDDDD", "TIEMPITO: "+calendar.getTimeInMillis());
    }
}

