package com.example.studentnotesapp_backend_v1.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        db.collection("tasks")
                .whereEqualTo("date", today)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        for (DocumentSnapshot doc : snapshot) {
                            Task t = doc.toObject(Task.class);
                            if (t != null) {
                                notifyUser(context, t.getTitle(), t.getDescription());
                            }
                        }
                    }
                });
    }

    private void notifyUser(Context context, String title, String message) {
        NotificationHelper helper = new NotificationHelper(context);
        helper.sendTaskReminder(title, message);
    }
}

