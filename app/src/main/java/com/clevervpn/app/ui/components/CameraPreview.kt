package com.clevervpn.app.ui.components

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.clevervpn.app.utils.QRCodeAnalyzer

@Composable
fun CameraPreview(codeCallback: (String?) -> Unit) {

    val localContext = LocalContext.current
    val lifeCycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.Start
            ) {
                Button(
                    onClick = {
                        codeCallback(null)
                    },
                ) {
                    Text(text = "Close")
                }

                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val cameraSelector = CameraSelector.Builder().build()

                        preview.surfaceProvider = previewView.surfaceProvider

                        val imageAnalysis = ImageAnalysis.Builder().build()

                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QRCodeAnalyzer { url ->
                                codeCallback(url)
                            }
                        )
                        runCatching {
                            cameraProviderFuture.get().unbindAll()
                            cameraProviderFuture.get().bindToLifecycle(
                                lifeCycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        }.onFailure {
                            Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
                        }
                        previewView
                    }
                )
            }
        }
    }
}

