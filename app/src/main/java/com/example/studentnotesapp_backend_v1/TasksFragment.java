package com.example.studentnotesapp_backend_v1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentnotesapp_backend_v1.logic.Task;
import com.example.studentnotesapp_backend_v1.logic.TasksAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TasksFragment extends Fragment {

    private RecyclerView rvTasks;
    private ProgressBar tasksLoading;
    private FloatingActionButton btnAddTask;

    private FirebaseFirestore db;
    private ArrayList<Task> taskList = new ArrayList<>();
    private TasksAdapter tasksAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Views
        rvTasks = view.findViewById(R.id.rvTasks);
        tasksLoading = view.findViewById(R.id.tasksLoading);
        btnAddTask = view.findViewById(R.id.btnAddTask);

        // Firestore
        db = FirebaseFirestore.getInstance();

        // RecyclerView setup
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksAdapter(taskList, this::onTaskAction);
        rvTasks.setAdapter(tasksAdapter);

        // Load tasks
        loadTasks();

        // Add new task button
        btnAddTask.setOnClickListener(v -> openAddTaskDialog(null));

        return view;
    }

    public void openAddTaskDialog(@Nullable Task existingTask) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);

        EditText etTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etDescription = dialogView.findViewById(R.id.etTaskDescription);
        EditText etDate = dialogView.findViewById(R.id.etTaskDate);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        Button btnSave = dialogView.findViewById(R.id.btnSaveTask);

        // Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Assignment", "Exam", "Personal"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Priority Spinner
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Low", "Medium", "High"});
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // If editing, pre-fill values
        if(existingTask != null){
            etTitle.setText(existingTask.getTitle());
            etDescription.setText(existingTask.getDescription());
            etDate.setText(existingTask.getDate());
            spinnerType.setSelection(typeAdapter.getPosition(existingTask.getType()));
            spinnerPriority.setSelection(priorityAdapter.getPosition(existingTask.getPriority()));
        }

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etDate.setText(dateStr);
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String priority = spinnerPriority.getSelectedItem().toString();

            if(title.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Title and date required", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar cal = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                cal.setTime(sdf.parse(date));
            } catch (Exception e) { e.printStackTrace(); }
            String week = new SimpleDateFormat("yyyy-ww", Locale.getDefault()).format(cal.getTime());

            if(existingTask == null){
                // New task
                Task task = new Task();
                task.setTitle(title);
                task.setDescription(desc);
                task.setDate(date);
                task.setWeek(week);
                task.setDone(false);
                task.setType(type);
                task.setPriority(priority);

                db.collection("tasks").add(task).addOnSuccessListener(docRef -> {
                    taskList.add(task);
                    tasksAdapter.notifyItemInserted(taskList.size() - 1);
                    dialog.dismiss();
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show());
            } else {
                // Update existing task
                existingTask.setTitle(title);
                existingTask.setDescription(desc);
                existingTask.setDate(date);
                existingTask.setWeek(week);
                existingTask.setType(type);
                existingTask.setPriority(priority);

                db.collection("tasks").document(existingTask.getId()).set(existingTask)
                        .addOnSuccessListener(aVoid -> {
                            tasksAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update task", Toast.LENGTH_SHORT).show());
            }
        });

        dialog.show();
    }

    private void loadTasks() {
        tasksLoading.setVisibility(View.VISIBLE);
        rvTasks.setVisibility(View.GONE);

        db.collection("tasks")
                .get()
                .addOnCompleteListener(task -> {
                    tasksLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        taskList.clear();
                        for (DocumentSnapshot doc : snapshot) {
                            Task t = doc.toObject(Task.class);
                            if (t != null) {
                                t.setId(doc.getId()); // <- crucial
                                taskList.add(t);
                            }
                        }
                        tasksAdapter.notifyDataSetChanged();
                        rvTasks.setVisibility(View.VISIBLE);
                    }
                });
    }

    // Callback from adapter for edit/delete/done actions
    private void onTaskAction(Task task, String action) {
        switch (action) {
            case "done":
                db.collection("tasks").document(task.getId())
                        .update("done", task.isDone())   // <- use actual new value
                        .addOnSuccessListener(aVoid -> {
                            // No full reload
                            tasksAdapter.notifyItemChanged(taskList.indexOf(task));
                        });
                break;

            case "edit":
                openAddTaskDialog(task);
                break;

            case "delete":
                db.collection("tasks").document(task.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> loadTasks());
                break;
        }
    }
}
