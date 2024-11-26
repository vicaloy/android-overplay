# 📱 Gyroscope Sensor Flow - Android Project

This project demonstrates how to handle the gyroscope sensor in an Android application using Kotlin Coroutines and Flows. It converts the gyroscope sensor callbacks into a Flow, allowing reactive and asynchronous data handling.

## 🚀 Key Features

- Gyroscope Sensor Handling:
Listens to gyroscope sensor changes using the SensorManager API.
Converts sensor data into a Flow for reactive programming.

- Flow-Based Implementation:
Provides a Flow that emits gyroscope data, allowing easy subscription and unsubscription.
Offers a clean and modular approach to handling sensor events.

- Two Subscription Options:
-   Manual Subscription/Unsubscription:
Use explicit methods to start and stop listening to the gyroscope sensor.
-   Automatic Flow Cancellation:
Collect the Flow in a ViewModel or Coroutine scope, and cancel the Job when you no longer need updates. See logcat with "MainViewModel".

## 🛠️ Technologies Used

- Kotlin: Primary language for Android development.
- Kotlin Coroutines: For asynchronous programming and managing flows.
- Flow: Converts sensor callbacks into a reactive data stream.
- SensorManager: Handles gyroscope sensor registration and data retrieval.
- Hilt: For dependency injection (optional in this example).
