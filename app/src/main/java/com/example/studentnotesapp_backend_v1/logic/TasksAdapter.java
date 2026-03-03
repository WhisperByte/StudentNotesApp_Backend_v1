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

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onAction(Task task, String action);
    }

    private ArrayList<Task> tasks;
    private TaskActionListener listener;

    public TasksAdapter(ArrayList<Task> tasks, TaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.taskCheck.setChecked(task.isDone());

        // Set priority color
        switch (task.getPriority().toLowerCase()) {
            case "high":
                holder.priority.setBackgroundColor(0xFFD32F2F); // Red
                break;
            case "medium":
                holder.priority.setBackgroundColor(0xFFFFA000); // Amber
                break;
            case "low":
                holder.priority.setBackgroundColor(0xFF388E3C); // Green
                break;
            default:
                holder.priority.setBackgroundColor(0xFF1976D2); // Default Blue
        }
        holder.priority.setText(task.getPriority());

        // Checkbox toggle
        holder.taskCheck.setOnClickListener(v -> {
            boolean newDone = !task.isDone();
            task.setDone(newDone);                // update local state
            holder.taskCheck.setChecked(newDone); // immediately reflect UI
            if (listener != null) listener.onAction(task, "done");
        });

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onAction(task, "edit");
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onAction(task, "delete");
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        CheckBox taskCheck;
        TextView title, description, priority;
        ImageView btnEdit, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheck = itemView.findViewById(R.id.taskCheck);
            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDescription);
            priority = itemView.findViewById(R.id.taskPriority);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
