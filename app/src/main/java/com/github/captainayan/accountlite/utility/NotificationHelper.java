package com.github.captainayan.accountlite.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationHelper {

    private static final String CHANNEL_ID = "file_save_channel";
    private static final String CHANNEL_NAME = "File Save Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for file save operations";

    public static void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(CHANNEL_DESCRIPTION);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
