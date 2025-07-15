# Green-Pantry
The Green Pantry App made for CS446 group project.
Group members: Sabina Gorbachev, Cindy Phan, Sreekanth Ghatti, Sid Kundu, Andrei Hirdea, Boyue Yang

## Technical Specification: Gemini Inference Layer

### Overview
The Green Pantry app will use Google's Gemini API as the core inference engine for food item recognition, replacing the need for a custom TensorFlow model and separate Python service. This approach significantly simplifies the architecture while providing powerful, state-of-the-art image recognition capabilities.

### Integration Architecture

#### Components
1. **Mobile Client (Android)**: Captures images and sends them to the backend service
2. **Backend Service**: Handles authentication, processes requests, and interfaces with the Gemini API
3. **Gemini API**: Performs image recognition and returns structured food item data
4. **Database**: Stores recognized items and user pantry information

#### Data Flow
1. User captures image of food item using the app's camera interface
2. Image is preprocessed on device (compressed, resized) to optimize transfer
3. Image is sent to our backend service along with user context
4. Backend forwards the image to Gemini API with a specialized prompt
5. Gemini identifies the food item and returns structured data
6. Backend processes the response and stores the item in the user's pantry
7. UI is updated to show the recognized item and its details

### Gemini API Implementation

#### API Configuration
- **Model**: Gemini Pro Vision (or latest available version)
- **Authentication**: API key stored securely in backend environment variables
- **Rate Limiting**: Implementation of request throttling to stay within API quotas
- **Error Handling**: Graceful fallbacks for API timeouts or recognition failures

#### Prompt Engineering
The system will use carefully crafted prompts to extract specific information from Gemini:

```
Prompt template:
"Identify the food item in this image. Please provide:
1. The specific name of the food item
2. Food category (fruit, vegetable, dairy, grain, protein, etc.)
3. Whether it's packaged or fresh
4. If packaged, any visible brand name
5. Approximate quantity or size if apparent
Return the information in JSON format."
```

#### Response Processing
The backend will parse the JSON response from Gemini and:
1. Extract the food item name, category, and other attributes
2. Match the item against our nutrition database
3. Handle edge cases (multiple items, uncertain identifications)
4. Format the data for storage and display

### Performance Considerations

#### Latency Management
- Client-side loading indicators during recognition process
- Image size optimization before upload (target: <500KB per image)
- Response caching for previously recognized common items

#### Bandwidth Usage
- Images will be compressed on-device before transmission
- Option for users to use lower quality image mode on limited data plans
- Batch processing capability for multiple items

#### Offline Capabilities
- Limited functionality when offline (manual entry still available)
- Queue system for pending recognitions when connectivity is restored

### Security and Privacy

- All images transmitted using TLS encryption
- Images not stored long-term on our servers (deleted after processing)
- Clear user consent flow for camera usage and image processing
- Compliance with relevant data protection regulations

### Error Handling

- Confidence threshold for recognition (items below threshold trigger manual verification)
- Alternative suggestions when recognition confidence is moderate
- User feedback mechanism to report and improve incorrect recognitions
- Graceful degradation to manual entry when service is unavailable

### Testing Strategy

- Unit tests for image preprocessing and API response handling
- Integration tests with Gemini API using a test image corpus
- A/B testing of different prompt formats to optimize recognition accuracy
- Performance testing under various network conditions

