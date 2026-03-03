package com.example.studentnotesapp_backend_v1.logic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.studentnotesapp_backend_v1.MainActivity;
import com.example.studentnotesapp_backend_v1.R;

import java.util.Random;

public class NotificationHelper {

    private static final String CHANNEL_ID = "task_reminders_channel";
    private static final String CHANNEL_NAME = "Task Reminders";
    private final Context context;
    private final SharedPreferences prefs;

    public NotificationHelper(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    public void sendTaskReminder(String title, String message) {
        boolean enabled = prefs.getBoolean("taskDeadline", true);
        if (!enabled) return;

        // Create a PendingIntent to open MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE  // ⚠️ Important for Android 12+
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent); // attach intent

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            manager.notify(new Random().nextInt(), builder.build());
        }
    }
}
