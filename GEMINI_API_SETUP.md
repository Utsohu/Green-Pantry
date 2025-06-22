# Gemini API Setup Instructions

## Overview
The app uses Google's Gemini API for food recognition. You need to add your API key to make it work.

## Steps to Set Up

### 1. Get a Gemini API Key
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated API key

### 2. Add API Key to Your Project
Open the `local.properties` file in your project root and add this line:
```
GEMINI_API_KEY=your_actual_api_key_here
```

Replace `your_actual_api_key_here` with the API key you copied from Google AI Studio.

### 3. Rebuild the Project
After adding the API key, rebuild your project:
- In Android Studio: Build → Rebuild Project
- Or run: `./gradlew clean build`

## Testing the Camera Feature
1. Run the app
2. Navigate to the Camera tab
3. Point the camera at a food item
4. Tap the capture button
5. The app will process the image and show the recognized food item

## Troubleshooting

### Check Logs
Look for these log messages in Logcat:
- "GeminiConfig" - Should show "API Key configured: Yes"
- "CameraFragment" - Shows image processing steps
- "GeminiApiClient" - Shows API calls and responses
- "CameraViewModel" - Shows recognition progress

### Common Issues
1. **Empty API Key**: Make sure you've added the key to local.properties
2. **Invalid API Key**: Verify the key is correct and active in Google AI Studio
3. **Network Issues**: Ensure your device/emulator has internet access
4. **API Quota**: Free tier has limits, check your usage in Google AI Studio

## Note
The `local.properties` file is gitignored and should not be committed to version control. Each developer needs to add their own API key.