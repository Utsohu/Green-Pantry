package com.example.greenpantry.data.repository

import android.graphics.Bitmap
import android.util.LruCache
import com.example.greenpantry.data.api.GeminiApiClient
import com.example.greenpantry.data.imageprocessing.ImagePreprocessor
import com.example.greenpantry.data.model.FoodCategory
import com.example.greenpantry.data.model.FoodRecognitionResponse
import com.example.greenpantry.data.model.RecognizedFoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRecognitionRepositoryImpl @Inject constructor(
    private val geminiApiClient: GeminiApiClient,
    private val imagePreprocessor: ImagePreprocessor
) : FoodRecognitionRepository {
    
    private val recognitionCache = LruCache<String, RecognizedFoodItem>(20)
    private val _recentRecognitions = MutableStateFlow<List<RecognizedFoodItem>>(emptyList())
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun recognizeFoodItem(bitmap: Bitmap): Result<RecognizedFoodItem> {
        try {
            // Preprocess the image
            val processedBitmap = imagePreprocessor.preprocessImage(bitmap)
            val imageHash = generateImageHash(processedBitmap)
            
            // Check cache first
            getCachedRecognition(imageHash)?.let { 
                return Result.success(it) 
            }
            
            // Call Gemini API
            val result = geminiApiClient.recognizeFoodItem(processedBitmap)
            
            return result.fold(
                onSuccess = { responseText ->
                    try {
                        // Parse JSON response
                        val cleanedResponse = extractJsonFromResponse(responseText)
                        val response = json.decodeFromString<FoodRecognitionResponse>(cleanedResponse)
                        
                        // Convert to domain model
                        val recognizedItem = RecognizedFoodItem(
                            name = response.itemName,
                            category = FoodCategory.fromString(response.category),
                            isPackaged = response.isPackaged,
                            brand = response.brand,
                            quantity = response.quantity,
                            confidence = response.confidence
                        )
                        
                        // Cache the result
                        cacheRecognition(imageHash, recognizedItem)
                        
                        // Add to recent recognitions
                        updateRecentRecognitions(recognizedItem)
                        
                        Result.success(recognizedItem)
                    } catch (e: Exception) {
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    override suspend fun getCachedRecognition(imageHash: String): RecognizedFoodItem? {
        return recognitionCache.get(imageHash)
    }
    
    override suspend fun cacheRecognition(imageHash: String, item: RecognizedFoodItem) {
        recognitionCache.put(imageHash, item)
    }
    
    override fun getRecentRecognitions(): Flow<List<RecognizedFoodItem>> {
        return _recentRecognitions.asStateFlow()
    }
    
    private fun generateImageHash(bitmap: Bitmap): String {
        val bytes = imagePreprocessor.compressBitmap(bitmap)
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    private fun extractJsonFromResponse(response: String): String {
        // Extract JSON from the response (Gemini might return extra text)
        val jsonStart = response.indexOf('{')
        val jsonEnd = response.lastIndexOf('}')
        
        return if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            response.substring(jsonStart, jsonEnd + 1)
        } else {
            response
        }
    }
    
    private fun updateRecentRecognitions(item: RecognizedFoodItem) {
        val currentList = _recentRecognitions.value.toMutableList()
        currentList.add(0, item)
        if (currentList.size > 50) {
            currentList.removeAt(currentList.lastIndex)
        }
        _recentRecognitions.value = currentList
    }
}