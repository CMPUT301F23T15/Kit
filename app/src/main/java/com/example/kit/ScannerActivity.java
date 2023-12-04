package com.example.kit;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Scanner", "Initializing Scanner");
        binding = ScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        futureProvider = ProcessCameraProvider.getInstance(this);
        futureProvider.addListener(() -> {
            try{
                processProvider = futureProvider.get();
                bindPreview(processProvider);
                bindImageAnalyzer();
            } catch (ExecutionException | InterruptedException e){

            }
        }, ContextCompat.getMainExecutor(this));


    }
    private void bindPreview(ProcessCameraProvider cameraProvider){
        preview = new Preview.Builder()
                .setTargetRotation(binding.viewFinder.getDisplay().getRotation())
                .build();
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
        camera = processProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

    }
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

    private void processImageProxy(BarcodeScanner scanner, ImageProxy imageProxy){
        InputImage inImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        scanner.process(inImage)
                .addOnSuccessListener(barcodes -> {
                    for(Barcode barcode: barcodes){
                        if(barcode != null){
                            Intent data = new Intent();
                            data.putExtra("Barcode", barcode.getRawValue());
                            finish();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
                        builder.setMessage(barcode.getRawValue()).create().show();
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
