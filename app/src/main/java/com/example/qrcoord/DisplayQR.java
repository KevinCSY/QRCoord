package com.example.qrcoord;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DisplayQR extends AppCompatActivity {

    // UI
    private ImageView qrImage;
    private Button backButton;

    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Data
    private double latitude;
    private double longitude;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr);

        backButton = findViewById(R.id.back);
        qrImage = findViewById(R.id.imview_qrImage);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        Bundle bundle = getIntent().getExtras();
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        name = bundle.getString("name");

        // Button to return to screen with the form
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayQR.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        int smallerDimension = getSmallerDimension();

        final DatabaseReference databaseWrite;
        // push() creates a unique key
        databaseWrite = databaseReference.push();
        databaseWrite.child("Name").setValue(name);
        databaseWrite.child("Latitude").setValue(latitude);
        databaseWrite.child("Longitude").setValue(longitude);

        String qrText = "Name : " + name + "\n" +
                        "Latitude : " + latitude + "\n" +
                        "Longitude : " + longitude;

        // Encode the data and show the QR code on the screen
        QRGEncoder qrEncoder = new QRGEncoder(qrText, null, QRGContents.Type.TEXT, smallerDimension);
        Bitmap qrBitmap = qrEncoder.getBitmap();
        qrImage.setImageBitmap(qrBitmap);
    }

    // This part is just to get the size of the QRCode based on the device's dimensions
    private int getSmallerDimension() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = Math.min(width, height);
        return (smallerDimension * 3) / 4;
    }
}

