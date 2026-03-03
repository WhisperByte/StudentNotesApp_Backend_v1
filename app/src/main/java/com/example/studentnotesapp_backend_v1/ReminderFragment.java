package com.example.studentnotesapp_backend_v1;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentnotesapp_backend_v1.logic.ReminderReceiver;
import com.example.studentnotesapp_backend_v1.logic.RemindersAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderFragment extends Fragment {

    private RecyclerView rvReminders;
    private ProgressBar remindersLoading;

    private FirebaseFirestore db;
    private ArrayList<Reminder> remindersList = new ArrayList<>();
    private RemindersAdapter remindersAdapter;

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        rvReminders = view.findViewById(R.id.rvReminders);
        remindersLoading = view.findViewById(R.id.remindersLoading);

        rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        remindersAdapter = new RemindersAdapter(remindersList);
        rvReminders.setAdapter(remindersAdapter);

        db = FirebaseFirestore.getInstance();
        prefs = getContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);

        loadReminders();

        createNotificationChannel();

        return view;
    }

    private void loadReminders() {
        remindersLoading.setVisibility(View.VISIBLE);
        rvReminders.setVisibility(View.GONE);

        db.collection("reminders")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    remindersLoading.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        remindersList.clear();

                        for (DocumentSnapshot doc : task.getResult()) {
                            Reminder r = doc.toObject(Reminder.class);
                            if (r != null) {
                                r.setId(doc.getId());
                                remindersList.add(r);

                                // Schedule notifications if enabled
                                if (prefs.getBoolean("taskDeadline", true)) {
                                    scheduleNotification(r);
                                }
                            }
                        }

                        remindersAdapter.notifyDataSetChanged();
                        rvReminders.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void scheduleNotification(Reminder reminder) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(reminder.getDateTimeMillis()); // make sure Reminder stores datetime in millis

        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("message", reminder.getMessage());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                reminder.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "REMINDER_CHANNEL",
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Task and Reminder Notifications");

            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    public static class Reminder {
        private String id;
        private String title;
        private String message;
        private long dateTimeMillis; // store reminder datetime in milliseconds

        public Reminder() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getDateTimeMillis() { return dateTimeMillis; }
        public void setDateTimeMillis(long dateTimeMillis) { this.dateTimeMillis = dateTimeMillis; }
    }
}
