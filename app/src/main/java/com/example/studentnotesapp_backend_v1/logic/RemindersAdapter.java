package com.example.studentnotesapp_backend_v1.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentnotesapp_backend_v1.R;
import com.example.studentnotesapp_backend_v1.ReminderFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    private final ArrayList<ReminderFragment.Reminder> reminders;

    public RemindersAdapter(ArrayList<ReminderFragment.Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderFragment.Reminder reminder = reminders.get(position);

        holder.tvMessage.setText(reminder.getMessage());

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd • HH:mm", Locale.getDefault())
                .format(new Date(reminder.getDateTimeMillis()));
        holder.tvTime.setText(formattedDate);

        // Optional: handle button clicks
        holder.btnSnooze.setOnClickListener(v -> {
            // snooze logic
        });

        holder.btnReschedule.setOnClickListener(v -> {
            // reschedule logic
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        ImageButton btnSnooze, btnReschedule;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.reminderMessage);
            tvTime = itemView.findViewById(R.id.reminderTime);
            btnSnooze = itemView.findViewById(R.id.btnSnooze);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
        }
    }
}
