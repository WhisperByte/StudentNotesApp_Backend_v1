package com.example.studentnotesapp_backend_v1.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentnotesapp_backend_v1.R;

public class NotificationSettingsActivity extends AppCompatActivity {

    private Switch switchTaskDeadline, switchDailySummary, switchAppUpdates;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);

        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());

        switchTaskDeadline = findViewById(R.id.switchTaskDeadline);
        switchDailySummary = findViewById(R.id.switchDailySummary);
        switchAppUpdates = findViewById(R.id.switchAppUpdates);

        loadSettings();

        switchTaskDeadline.setOnCheckedChangeListener((button, isChecked) -> saveSetting("taskDeadline", isChecked));
        switchDailySummary.setOnCheckedChangeListener((button, isChecked) -> saveSetting("dailySummary", isChecked));
        switchAppUpdates.setOnCheckedChangeListener((button, isChecked) -> saveSetting("appUpdates", isChecked));
    }

    private void loadSettings() {
        switchTaskDeadline.setChecked(prefs.getBoolean("taskDeadline", true));
        switchDailySummary.setChecked(prefs.getBoolean("dailySummary", false));
        switchAppUpdates.setChecked(prefs.getBoolean("appUpdates", true));
    }

    private void saveSetting(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }
}
