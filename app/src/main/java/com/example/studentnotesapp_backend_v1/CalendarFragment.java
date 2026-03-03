package com.example.studentnotesapp_backend_v1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentnotesapp_backend_v1.logic.Task;
import com.example.studentnotesapp_backend_v1.logic.TasksAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView rvDailyTasks;
    private ProgressBar tasksLoading;
    private TextView selectedDateTitle;

    private FirebaseFirestore db;
    private ArrayList<Task> dailyTasks = new ArrayList<>();
    private TasksAdapter tasksAdapter;

    private String selectedDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Views
        calendarView = view.findViewById(R.id.calendarView);
        rvDailyTasks = view.findViewById(R.id.rvDailyTasks);
        tasksLoading = view.findViewById(R.id.tasksLoading);
        selectedDateTitle = view.findViewById(R.id.selectedDateTitle);

        // Firestore
        db = FirebaseFirestore.getInstance();

        // RecyclerView
        rvDailyTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksAdapter(dailyTasks, this::onTaskAction);
        rvDailyTasks.setAdapter(tasksAdapter);

        // Default = today
        selectedDate = formatDate(calendarView.getDate());
        selectedDateTitle.setText("Tasks for " + selectedDate);
        loadTasksForDate(selectedDate);

        // Date change listener
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year, month + 1, dayOfMonth);
            selectedDateTitle.setText("Tasks for " + selectedDate);
            loadTasksForDate(selectedDate);
        });

        return view;
    }

    private int priorityValue(String p) {
        if (p == null) return 0;
        switch (p.toLowerCase()) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default:
                try { return Integer.parseInt(p); }
                catch (Exception e) { return 0; }
        }
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis);
    }

    private void loadTasksForDate(String date) {
        tasksLoading.setVisibility(View.VISIBLE);
        rvDailyTasks.setVisibility(View.GONE);

        db.collection("tasks")
                .whereEqualTo("date", date)
                .get() // remove orderBy to avoid Firestore index issue
                .addOnCompleteListener(task -> {
                    tasksLoading.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        dailyTasks.clear();

                        for (DocumentSnapshot doc : task.getResult()) {
                            Task t = doc.toObject(Task.class);
                            if (t != null) {
                                t.setId(doc.getId());
                                dailyTasks.add(t);
                            }
                        }

                        // Optionally, sort by priority in-memory
                        dailyTasks.sort((a, b) ->
                                priorityValue(b.getPriority()) - priorityValue(a.getPriority())
                        );

                        tasksAdapter.notifyDataSetChanged();
                        rvDailyTasks.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void onTaskAction(Task task, String action) {
        switch (action) {

            case "done":
                db.collection("tasks").document(task.getId())
                        .update("done", task.isDone())
                        .addOnSuccessListener(aVoid -> {
                            int pos = dailyTasks.indexOf(task);
                            if (pos >= 0) tasksAdapter.notifyItemChanged(pos);
                        });
                break;

            case "edit":
                openEditDialog(task);
                break;

            case "delete":
                db.collection("tasks").document(task.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            int pos = dailyTasks.indexOf(task);
                            if (pos >= 0) {
                                dailyTasks.remove(pos);
                                tasksAdapter.notifyItemRemoved(pos);
                            }
                        });
                break;
        }
    }

    private void openEditDialog(Task task) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openTaskDialogFromFragment(task);
        }
    }
}
