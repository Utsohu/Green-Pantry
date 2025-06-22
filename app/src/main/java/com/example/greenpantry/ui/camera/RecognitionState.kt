package com.example.greenpantry.ui.camera

import com.example.greenpantry.data.model.RecognizedFoodItem

sealed class RecognitionState {
    object Idle : RecognitionState()
    object Capturing : RecognitionState()
    object Processing : RecognitionState()
    data class Success(val item: RecognizedFoodItem) : RecognitionState()
    data class Error(val message: String) : RecognitionState()
}