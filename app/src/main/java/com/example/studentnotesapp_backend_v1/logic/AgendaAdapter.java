package com.example.studentnotesapp_backend_v1.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentnotesapp_backend_v1.R;

import java.util.ArrayList;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    public interface TaskActionListener {
        void onAction(Task task, String action);
    }

    private final ArrayList<Task> tasks;
    private final TaskActionListener listener;

    public AgendaAdapter(ArrayList<Task> tasks, TaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());

        // Bind checkbox without triggering previous listener
        holder.taskCheck.setOnCheckedChangeListener(null);
        holder.taskCheck.setChecked(task.isDone());

        // Checkbox click listener
        holder.taskCheck.setOnClickListener(v -> {
            boolean newDone = !task.isDone();
            task.setDone(newDone);
            holder.taskCheck.setChecked(newDone);
            if (listener != null) listener.onAction(task, "done");
        });

        // Edit
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onAction(task, "edit");
        });

        // Delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onAction(task, "delete");
        });

        // Task type color indicator
        switch (task.getType().toLowerCase()) {
            case "assignment": holder.colorIndicator.setBackgroundColor(0xFF1976D2); break;
            case "exam": holder.colorIndicator.setBackgroundColor(0xFFD32F2F); break;
            case "personal": holder.colorIndicator.setBackgroundColor(0xFF388E3C); break;
            default: holder.colorIndicator.setBackgroundColor(0xFFBDBDBD);
        }

        // Priority color
        switch (task.getPriority().toLowerCase()) {
            case "high": holder.priority.setBackgroundColor(0xFFD32F2F); break;
            case "medium": holder.priority.setBackgroundColor(0xFFFFA000); break;
            case "low": holder.priority.setBackgroundColor(0xFF388E3C); break;
            default: holder.priority.setBackgroundColor(0xFF1976D2);
        }
        holder.priority.setText(task.getPriority());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class AgendaViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheck;
        TextView title, description, priority;
        ImageView btnEdit, btnDelete;
        View colorIndicator;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheck = itemView.findViewById(R.id.taskCheck);
            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDescription);
            priority = itemView.findViewById(R.id.taskPriority);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
            colorIndicator = itemView.findViewById(R.id.taskColorIndicator);
        }
    }
}
