package com.example.greenpantry.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.BuildConfig
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.model.RecognizedFoodItem
import com.example.greenpantry.ui.home.HomeFragment
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    private val viewModel: CameraViewModel by viewModels()
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var progressBar: ProgressBar? = null
    private var cameraProvider: ProcessCameraProvider? = null
    
    
    // Test mode flag - set to true to use test image instead of camera
    private val USE_TEST_MODE = BuildConfig.DEBUG
    
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            // go to search if permission denied
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SearchFragment())
                .commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        progressBar = view.findViewById(R.id.progressBar)

        // notif button
        setupNotifBtn(view)

        // take photo button
        val captureButton = view.findViewById<ImageButton>(R.id.captureButton)
        captureButton.setOnClickListener {
            Log.d("CameraFragment", "Capture button clicked")
            Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show()
            
            if (USE_TEST_MODE) {
                // Test mode: use a sample image
                testWithSampleImage()
            } else {
                // Normal mode: capture from camera
                capturePhoto()
            }
        }
        
        
        // Observe recognition state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recognitionState.collect { state ->
                handleRecognitionState(state)
                
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                
                Log.d("CameraFragment", "Camera provider obtained")

                // build preview
                val preview = Preview.Builder().build()
                val previewView = view?.findViewById<PreviewView>(R.id.previewView)
                previewView?.let {
                    preview.setSurfaceProvider(it.surfaceProvider)
                }

                // allow image capture
                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(previewView?.display?.rotation ?: Surface.ROTATION_0)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind all use cases before binding new ones
                    cameraProvider?.unbindAll()
                    
                    // Bind use cases to camera
                    cameraProvider?.bindToLifecycle(
                        viewLifecycleOwner, cameraSelector, preview, imageCapture
                    )
                    
                    Log.d("CameraFragment", "Camera bound successfully")
                } catch (e: Exception) {
                    Log.e("CameraFragment", "Use case binding failed", e)
                    Toast.makeText(context, "Camera initialization failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CameraFragment", "Failed to get camera provider", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    
    private fun testWithSampleImage() {
        try {
            Log.d("CameraFragment", "Using test mode with sample image")
            
            // Create a simple test bitmap (red apple shape)
            val originalBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).apply {
                // Fill with white background
                eraseColor(android.graphics.Color.WHITE)
                
                // Draw a simple red circle to represent an apple
                val canvas = android.graphics.Canvas(this)
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                    style = android.graphics.Paint.Style.FILL
                }
                canvas.drawCircle(150f, 150f, 100f, paint)
                
                // Add a small green stem
                paint.color = android.graphics.Color.GREEN
                canvas.drawRect(145f, 40f, 155f, 60f, paint)
            }
            
            // Create a defensive copy to avoid recycling issues
            val bitmap = originalBitmap.copy(originalBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            
            Log.d("CameraFragment", "Test bitmap created: ${bitmap.width}x${bitmap.height}")
            viewModel.recognizeImage(bitmap)
            
        } catch (e: Exception) {
            Log.e("CameraFragment", "Test mode failed", e)
            Toast.makeText(context, "Test failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun capturePhoto() {
        val imageCapture = imageCapture ?: run {
            Log.e("CameraFragment", "ImageCapture is null")
            Toast.makeText(context, "Camera not ready", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("CameraFragment", "Starting photo capture")

        // Create time-stamped output file to hold the image
        val photoFile = File(
            requireContext().filesDir,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed", exc)
                    Toast.makeText(context, "Photo capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("CameraFragment", "Photo saved to: ${photoFile.absolutePath}")
                    processImage(photoFile)
                }
            }
        )
    }
    
    private fun processImage(photoFile: File) {
        try {
            Log.d("CameraFragment", "Processing image: ${photoFile.absolutePath}")
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            if (bitmap != null) {
                Log.d("CameraFragment", "Bitmap loaded: ${bitmap.width}x${bitmap.height}")
                viewModel.recognizeImage(bitmap)
                
                // Clean up the temporary photo file after processing
                try {
                    if (photoFile.exists()) {
                        photoFile.delete()
                        Log.d("CameraFragment", "Temporary photo file deleted")
                    }
                } catch (e: Exception) {
                    Log.w("CameraFragment", "Failed to delete temporary photo file", e)
                }
            } else {
                Log.e("CameraFragment", "Failed to decode bitmap from file")
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("CameraFragment", "Failed to process image", e)
            Toast.makeText(context, "Failed to process image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleRecognitionState(state: RecognitionState) {
        when (state) {
            is RecognitionState.Idle -> {
                progressBar?.visibility = View.GONE
            }
            is RecognitionState.Processing -> {
                progressBar?.visibility = View.VISIBLE
            }
            is RecognitionState.Success -> {
                progressBar?.visibility = View.GONE
                showRecognitionResult(state.item)
            }
            is RecognitionState.Error -> {
                progressBar?.visibility = View.GONE
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private fun showRecognitionResult(item: RecognizedFoodItem) {
        val fragmentManager = childFragmentManager

        // Force-remove any existing dialog with the same tag
        val prevDialog = fragmentManager.findFragmentByTag("RecognitionResultDialog")
        if (prevDialog != null) {
            fragmentManager.beginTransaction().remove(prevDialog).commitNow()
        }

        // Create and show new dialog
        val dialog = RecognitionResultDialog()
        dialog.setRecognizedItem(item)
        dialog.setOnSaveClickListener { recognizedItem, description ->
            viewModel.saveRecognizedItem(recognizedItem, description)
            //remove this line later
            storeRecognizedItem(recognizedItem)
            Toast.makeText(
                context,
                "${recognizedItem.name} added to pantry!",
                Toast.LENGTH_SHORT
            ).show()

            navigateToHome()
        }

        // Reset recognition state to Idle when dialog is cancelled/dismissed
        dialog.setOnCancelListener {
            viewModel.dismissRecognition()
        }

        dialog.show(fragmentManager, "RecognitionResultDialog")
    }

    private fun storeRecognizedItem(item : RecognizedFoodItem){
        val pantryDB = PantryItemDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            var searchPantryItem = pantryDB.pantryItemDao().getPantryItemByName(item.name)
            if (searchPantryItem != null) {
                searchPantryItem.quantity += item.quantity?.toInt()
                searchPantryItem.curNum += item.quantity?.toInt()?.times(100) ?: 0
                pantryDB.pantryItemDao().updatePantryItem(searchPantryItem)
            }
            else{
                searchPantryItem = item.toPantryItem()
                pantryDB.pantryItemDao().insertPantryItem(searchPantryItem)
            }
        }
    }
    
    private fun navigateToHome() {
        // Update bottom navigation selection
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_home
        
        // Navigate to home fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
    
    override fun onPause() {
        super.onPause()
        Log.d("CameraFragment", "onPause called")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d("CameraFragment", "onResume called")
    }
    
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("CameraFragment", "onDestroy called")
        
        // Properly release camera resources
        cameraProvider?.unbindAll()
        cameraProvider = null
        
        cameraExecutor.shutdown()
    }
}