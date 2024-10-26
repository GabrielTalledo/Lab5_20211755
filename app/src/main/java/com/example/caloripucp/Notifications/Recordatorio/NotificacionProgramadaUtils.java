package com.example.caloripucp.Notifications.Recordatorio;

import android.app.NotificationManager;
import android.content.Context;

public class NotificacionProgramadaUtils {

    private final Context context;

    public NotificacionProgramadaUtils(Context context) {
        this.context = context;
    }

    public void cancelNotification(int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public void cancelAllNotifications() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
