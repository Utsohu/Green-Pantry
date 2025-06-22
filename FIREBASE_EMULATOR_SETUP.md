# Firebase Emulator Setup for Green Pantry

## Overview
The app is configured to automatically use Firebase emulators when running on an Android emulator in debug mode. This helps bypass network connectivity issues during development.

## Setup Instructions

### 1. Install Firebase CLI
If you haven't already, install the Firebase CLI:
```bash
npm install -g firebase-tools
```

### 2. Start Firebase Emulators
From the project root directory, run:
```bash
firebase emulators:start
```

This will start:
- Authentication emulator on port 9099
- Firestore emulator on port 8080
- Emulator UI on port 4000

### 3. Run the App
1. Start your Android emulator in Android Studio
2. Run the app normally using the Run button or `./gradlew installDebug`

The app will automatically detect it's running on an emulator and connect to the local Firebase emulators instead of production Firebase.

## Troubleshooting

### Network Errors
If you still see network errors:
1. Make sure the Firebase emulators are running (check http://localhost:4000)
2. Check the logs for "Configuring Firebase emulators" message
3. The app includes a fallback mechanism that allows login to proceed in debug mode even if Firebase is unreachable

### Authentication Flow
- In debug mode with network issues, the app will automatically bypass authentication after a timeout
- This allows you to test the food recognition features without being blocked by auth issues
- The bypass is only enabled in debug builds and won't affect production

### Checking Emulator Connection
Look for these log messages:
- "Firebase initialized"
- "Configuring Firebase emulators for Android emulator"
- "Auth emulator configured at 10.0.2.2:9099"
- "Firestore emulator configured at 10.0.2.2:8080"

## Testing Food Recognition
Once logged in (either through Firebase or the debug bypass):
1. Navigate to the Camera tab
2. Take a photo of a food item
3. The app will process the image using the Gemini API
4. View the recognized food details and save to your pantry

## Note
The authentication bypass is a development feature only. In production builds, proper Firebase authentication is required.