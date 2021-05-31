package com.example.text_recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    String cameraPermission[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("ZXingScannerView","INISDE SCAN CODE ACTIVITY");
        scannerView = new ZXingScannerView(this);

        setContentView(scannerView);

        cameraPermission = new String[]{Manifest.permission.CAMERA};

        if (!checkCameraPermission()) {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, 0);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    @Override
    public void handleResult(Result result) {
        MainActivity.qr_code.setText(result.getText().toLowerCase().replaceAll("\\p{Punct}",""));
        Log.i("QR CODE ",MainActivity.qr_code.getText().toString());
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scannerView.stopCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        scannerView.startCamera();

    }
}