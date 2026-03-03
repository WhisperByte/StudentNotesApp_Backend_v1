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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentnotesapp_backend_v1.logic.AgendaAdapter;
import com.example.studentnotesapp_backend_v1.logic.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView rvTodaysAgenda;
    private ProgressBar agendaLoading, progressLoading;
    private TextView txtProgressPercent;

    private FirebaseFirestore db;
    private ArrayList<Task> todaysTasks = new ArrayList<>();
    private AgendaAdapter agendaAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Views
        rvTodaysAgenda = view.findViewById(R.id.rvTodaysAgenda);
        agendaLoading = view.findViewById(R.id.agendaLoading);
        progressLoading = view.findViewById(R.id.progressLoading);
        txtProgressPercent = view.findViewById(R.id.txtProgressPercent);

        db = FirebaseFirestore.getInstance();

        // RecyclerView setup
        rvTodaysAgenda.setLayoutManager(new LinearLayoutManager(getContext()));
        agendaAdapter = new AgendaAdapter(todaysTasks, (task, action) -> {
            switch (action) {
                case "done":
                    // Update Firestore with the new done state
                    db.collection("tasks").document(task.getId())
                            .update("done", task.isDone());
                    break;

                case "edit":
                    openAddTaskDialog(task);
                    break;

                case "delete":
                    db.collection("tasks").document(task.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                int pos = todaysTasks.indexOf(task);
                                if (pos >= 0) {
                                    todaysTasks.remove(pos);
                                    agendaAdapter.notifyItemRemoved(pos);
                                }
                            });
                    break;
            }
        });
        rvTodaysAgenda.setAdapter(agendaAdapter);

        loadTodaysAgenda();
        loadWeeklyProgress();

        return view;
    }

    private void loadTodaysAgenda() {
        agendaLoading.setVisibility(View.VISIBLE);
        rvTodaysAgenda.setVisibility(View.GONE);

        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("tasks")
                .whereEqualTo("date", todayStr)
                .get()
                .addOnCompleteListener(task -> {
                    agendaLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        todaysTasks.clear();
                        for (DocumentSnapshot doc : snapshot) {
                            Task t = doc.toObject(Task.class);
                            if (t != null) {
                                t.setId(doc.getId());
                                todaysTasks.add(t);
                            }
                        }
                        agendaAdapter.notifyDataSetChanged();
                        rvTodaysAgenda.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadWeeklyProgress() {
        progressLoading.setVisibility(View.VISIBLE);

        Date now = new Date();
        String currentWeek = new SimpleDateFormat("yyyy-ww", Locale.getDefault()).format(now);

        db.collection("tasks")
                .whereEqualTo("week", currentWeek)
                .get()
                .addOnCompleteListener(task -> {
                    progressLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        int total = snapshot.size();
                        int completed = 0;
                        for (DocumentSnapshot doc : snapshot) {
                            Boolean done = doc.getBoolean("done");
                            if (done != null && done) completed++;
                        }
                        int percent = total == 0 ? 0 : (completed * 100 / total);
                        txtProgressPercent.setText(percent + "%");
                        ((android.widget.ProgressBar)getView().findViewById(R.id.progressWeeklyTasks))
                                .setProgress(percent);
                    } else {
                        txtProgressPercent.setText("0%");
                    }
                });
    }

    // Add/Edit Task dialog
    private void openAddTaskDialog(@Nullable Task existingTask) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);

        EditText etTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etDescription = dialogView.findViewById(R.id.etTaskDescription);
        EditText etDate = dialogView.findViewById(R.id.etTaskDate);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        Button btnSave = dialogView.findViewById(R.id.btnSaveTask);

        // Type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Assignment", "Exam", "Personal"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Priority spinner
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Low", "Medium", "High"});
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // If editing, prefill
        if (existingTask != null) {
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
                    (view, year, month, dayOfMonth) -> etDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
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

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Title and date required", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar cal = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                cal.setTime(sdf.parse(date));
            } catch (Exception e) { e.printStackTrace(); }
            String week = new SimpleDateFormat("yyyy-ww", Locale.getDefault()).format(cal.getTime());

            if (existingTask == null) {
                Task task = new Task();
                task.setTitle(title);
                task.setDescription(desc);
                task.setDate(date);
                task.setWeek(week);
                task.setDone(false);
                task.setType(type);
                task.setPriority(priority);

                db.collection("tasks").add(task).addOnSuccessListener(docRef -> {
                    task.setId(docRef.getId());
                    todaysTasks.add(task);
                    agendaAdapter.notifyItemInserted(todaysTasks.size() - 1);
                    dialog.dismiss();
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show());
            } else {
                existingTask.setTitle(title);
                existingTask.setDescription(desc);
                existingTask.setDate(date);
                existingTask.setWeek(week);
                existingTask.setType(type);
                existingTask.setPriority(priority);

                db.collection("tasks").document(existingTask.getId()).set(existingTask)
                        .addOnSuccessListener(aVoid -> {
                            agendaAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update task", Toast.LENGTH_SHORT).show());
            }
        });

        dialog.show();
    }
}
