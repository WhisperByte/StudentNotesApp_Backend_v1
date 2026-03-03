📚 Student Organiser App

An Android student productivity app built with Java + Firebase to manage tasks, deadlines, reminders, and track weekly progress — all in one streamlined dashboard.

✨ Features
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

🛠 Tech Stack

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

⚙️ Setup

Clone the repository

Create a Firebase project

Enable:

Authentication

Firestore Database

Add your google-services.json to /app

Build & run on Android 8+

🚀 Future Improvements

Task categories

Snooze / reschedule reminder logic

Dark mode

Analytics & charts

Cloud sync optimization

🎯 Project Goal

Built to improve student productivity while demonstrating:

Firebase integration

Background scheduling

Real-time UI updates

Modern Android architecture patterns
