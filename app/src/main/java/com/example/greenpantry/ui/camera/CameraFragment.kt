package com.example.greenpantry.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.search.SearchFragment

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private var imageCapture: ImageCapture? = null
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

        // notif button
        val notifBtn = view.findViewById<ImageButton>(R.id.notificationButton)
        notifBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationsFragment())
                .commit()
        }

        // take photo button
        val captureButton = view.findViewById<ImageButton>(R.id.captureButton)
        captureButton.setOnClickListener {
            Toast.makeText(context, "cameraBtn clicked", Toast.LENGTH_SHORT).show()
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
            val cameraProvider = cameraProviderFuture.get()

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
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}
