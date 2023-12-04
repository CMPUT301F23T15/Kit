package com.example.kit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.annotation.CameraExecutor;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.kit.databinding.ScannerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

    private ScannerBinding binding;
    private CameraSelector cameraSelector;
    private ListenableFuture<ProcessCameraProvider> futureProvider;
    private ProcessCameraProvider processProvider;
    private Preview preview;
    private Camera camera;
    private ImageAnalysis imageAnalysis;
    boolean foundFlag = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Scanner", "Initializing Scanner");

        binding = ScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Back button in case user does not want to scan
        binding.backButton.setOnClickListener(v -> {
            finish();
        });
        // Setup camera preview selector, choosing the rear camera
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        // Setups future provider, creates listener for it
        futureProvider = ProcessCameraProvider.getInstance(this);
        futureProvider.addListener(() -> {
            try{
                // Sets up binding for the preview
                processProvider = futureProvider.get();
                bindPreview(processProvider);
                bindImageAnalyzer();    // Binding image analyzer, will be used with AndroidML for barcode detection
            } catch (ExecutionException | InterruptedException e){

            }
        }, ContextCompat.getMainExecutor(this));


    }

    /**
     * Binds camera preview with cameraX
     * @param cameraProvider Camera provider object
     */
    private void bindPreview(ProcessCameraProvider cameraProvider){
        preview = new Preview.Builder()
                .setTargetRotation(binding.viewFinder.getDisplay().getRotation())
                .build();
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK) // May be redundant?
                .build();
        preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
        camera = processProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

    }

    /**
     * binds image analyzer, processes proxyImages for barcodes
     */
    private void bindImageAnalyzer() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .enableAllPotentialBarcodes()
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetRotation(binding.viewFinder.getDisplay().getRotation())
                .build();

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            processImageProxy(scanner, imageProxy);

        });
        processProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);

    }

    /**
     * Handles barcode analysis and data transfer back to the item view fragment
     * @param scanner Object of the barcode scanner
     * @param imageProxy ImageProxy containing barcode image information
     */
    private void processImageProxy(BarcodeScanner scanner, ImageProxy imageProxy){
        InputImage inImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        scanner.process(inImage)
                .addOnSuccessListener(barcodes -> {
                    for(Barcode barcode: barcodes){
                        if((barcode != null) && (barcode.getRawValue().trim().length() > 0) && !foundFlag){
                            foundFlag = true;
                            Log.i("Scanning", "Barcode had been found! " + barcode.getRawValue());
                            // used for debugging and finding items barcodes
//                            AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
//                            builder.setMessage(barcode.getRawValue()).create().show();
                            Intent data = new Intent(Intent.ACTION_SEND);
                            data.putExtra("Barcode", barcode.getRawValue());
                            setResult(Activity.RESULT_OK, data);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Scanner Processing", e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    imageProxy.close();
                });
    }
}
