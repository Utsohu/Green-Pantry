package com.example.greenpantry.data.imageprocessing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagePreprocessor @Inject constructor() {
    
    companion object {
        private const val MAX_WIDTH = 1024
        private const val MAX_HEIGHT = 1024
        private const val COMPRESSION_QUALITY = 85
    }
    
    fun preprocessImage(imageFile: File): Bitmap {
        val bitmap = loadBitmapWithCorrectOrientation(imageFile)
        return resizeBitmap(bitmap)
    }
    
    fun preprocessImage(bitmap: Bitmap): Bitmap {
        return resizeBitmap(bitmap)
    }
    
    private fun loadBitmapWithCorrectOrientation(imageFile: File): Bitmap {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }
    
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return bitmap
        }
        
        val aspectRatio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        
        if (width > height) {
            targetWidth = MAX_WIDTH
            targetHeight = (MAX_WIDTH / aspectRatio).toInt()
        } else {
            targetHeight = MAX_HEIGHT
            targetWidth = (MAX_HEIGHT * aspectRatio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
    
    fun compressBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
        return outputStream.toByteArray()
    }
}