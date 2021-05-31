package com.example.text_recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQrCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    ZXingScannerView scannerView;
    String cameraPermission[];

    String got_qr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(ScanQrCode.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    50); }

        Log.i("ZXing Scanner View","INSIDE SCAN QR CODE ACTIVITY");
        scannerView = new ZXingScannerView(this);

        setContentView(scannerView);




    }

    @Override
    public void handleResult(Result result) {
        got_qr = result.getText().toLowerCase().replaceAll("\\p{Punct}","");
        Log.i("SCANQR - get_qrcode",got_qr);
        Intent intent = new Intent(ScanQrCode.this,ScanQR.class);
        intent.putExtra("GOT_QR",got_qr);
        ScanQR.scanned = 1;
        startActivity(intent);
        finish();
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