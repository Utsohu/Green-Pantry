package com.example.greenpantry.ui.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.data.model.RecognizedFoodItem
import com.example.greenpantry.domain.usecase.RecognizeFoodItemUseCase
import com.example.greenpantry.domain.usecase.SaveRecognizedItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val recognizeFoodItemUseCase: RecognizeFoodItemUseCase,
    private val saveRecognizedItemUseCase: SaveRecognizedItemUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "CameraViewModel"
    }
    
    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()
    
    private val _savedItems = MutableStateFlow<List<RecognizedFoodItem>>(emptyList())
    val savedItems: StateFlow<List<RecognizedFoodItem>> = _savedItems.asStateFlow()
    
    fun recognizeImage(bitmap: Bitmap) {
        // Prevent multiple simultaneous recognition requests
        if (_recognitionState.value is RecognitionState.Processing) {
            Log.w(TAG, "Recognition already in progress, ignoring new request")
            return
        }
        
        viewModelScope.launch {
            Log.d(TAG, "Starting image recognition")
            _recognitionState.value = RecognitionState.Processing
            
            try {
                recognizeFoodItemUseCase(bitmap).fold(
                    onSuccess = { recognizedItem ->
                        Log.d(TAG, "Recognition successful: ${recognizedItem.name}")
                        _recognitionState.value = RecognitionState.Success(recognizedItem)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Recognition failed", exception)
                        _recognitionState.value = RecognitionState.Error(
                            exception.message ?: "Failed to recognize food item"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during recognition", e)
                _recognitionState.value = RecognitionState.Error(
                    "Unexpected error: ${e.message}"
                )
            }
        }
    }
    
    fun saveRecognizedItem(item: RecognizedFoodItem, description: String = "") {
        viewModelScope.launch {
            Log.d(TAG, "Saving recognized item: ${item.name}")
            saveRecognizedItemUseCase(item, description).fold(
                onSuccess = {
                    Log.d(TAG, "Item saved successfully: ${item.name}")
                    // Add to saved items list
                    _savedItems.value = _savedItems.value + item
                    // Reset state to idle
                    _recognitionState.value = RecognitionState.Idle
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to save item", exception)
                    _recognitionState.value = RecognitionState.Error(
                        "Failed to save item: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun dismissRecognition() {
        _recognitionState.value = RecognitionState.Idle
    }
}