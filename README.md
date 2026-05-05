📚 Student Organiser App

An Android student productivity app built with Java + Firebase to manage tasks, deadlines, reminders, and track weekly progress — all in one streamlined dashboard.

⚙️ HOW TO RUN

Clone the repository

Create a Firebase project

Enable:

Authentication

Firestore Database

Add your google-services.json to /app

Build & run on Android 8+

✨ FEATURES

📝 Tasks

Create, edit, delete tasks

Set due dates and priorities

Mark tasks as completed

Real-time UI updates

📅 Calendar

View tasks by selected date

Daily breakdown

Priority sorting

⏰ Reminders

Schedule deadline reminders

AlarmManager powered notifications

Android 12+ compliant PendingIntent handling

Notification channel support

📊 Dashboard

Today’s agenda overview

Weekly completion progress bar

Instant progress updates

🔐 Authentication

Firebase Authentication

User-specific Firestore data

🛠 TECH STACK

Java (Android SDK)

Firebase Firestore

Firebase Authentication

RecyclerView

AlarmManager

NotificationManager

MVVM (Shared ViewModel)

🏗 Architecture
MainActivity
 ├── HomeFragment
 ├── TasksFragment
 ├── CalendarFragment
 ├── ReminderFragment
 └── ProfileFragment

Core components:

NotificationHelper → Handles notification logic

ReminderReceiver → Triggers scheduled alarms

TasksAdapter / RemindersAdapter → RecyclerView adapters

Shared ViewModel → Real-time UI updates

🔔 Notifications

Uses Notification Channels (Android 8+)

Requires POST_NOTIFICATIONS permission (Android 13+)

Uses FLAG_IMMUTABLE for Android 12+ compatibility

📈 Weekly Progress Calculation
(Completed Tasks / Total Weekly Tasks) × 100

Automatically updates when tasks are marked complete.

🚀 Future Improvements

Task categories

Snooze / reschedule reminder logic

Dark mode

Analytics & charts

Cloud sync optimization

<img width="1920" height="1080" alt="Screenshot (85)" src="https://github.com/user-attachments/assets/5bbadb4d-d783-4ec2-aed3-b5e09a693a72" />
<img width="519" height="888" alt="unibuddz" src="https://github.com/user-attachments/assets/86f35419-ba4e-453d-af02-bcb0ba52e5e1" />
<img width="1920" height="1080" alt="Screenshot (86)" src="https://github.com/user-attachments/assets/7da6c9d8-c752-4d2a-9afc-137774d9f03f" />
<img width="1920" height="1080" alt="Screenshot (82)" src="https://github.com/user-attachments/assets/526ac8fd-fcfb-4a0b-89f2-32b919682962" />
<img width="1920" height="1080" alt="Screenshot (83)" src="https://github.com/user-attachments/assets/5cf16873-6368-4ac2-9ea3-f35998ead8ea" />
<img width="1920" height="1080" alt="Screenshot (84)" src="https://github.com/user-attachments/assets/34a6251f-9321-49ac-9b0b-93499d9990d6" />


Firebase integration

Background scheduling

Real-time UI updates

Modern Android architecture patterns
