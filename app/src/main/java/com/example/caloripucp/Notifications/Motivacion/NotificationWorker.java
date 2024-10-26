package com.example.caloripucp.Notifications.Motivacion;

import android.app.NotificationChannel;
import android.content.Context;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.R;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.d("XD123123", "doWork: xddd");
        mostrarNotificacionMotivacional();
        return Result.retry();
    }

    private void mostrarNotificacionMotivacional() {
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

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AppActivity.class);
        intent.putExtra("motivacionFragment","uwu");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.motivacion)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(66, builder.build());
    }
}
