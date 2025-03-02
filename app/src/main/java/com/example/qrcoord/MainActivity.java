package com.example.qrcoord;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    // UI
    private Button submitButton;
    private EditText nameField;

    // Permission
    private final int REQUEST_PERMISSION_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButton = findViewById(R.id.submit);
        nameField = findViewById(R.id.form_name);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Retrieve String from the form
                final String name = nameField.getText().toString();

                // Check GPS acces permissions
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check if reason of request should be shown
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show explanation of permission request
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.title_location_permission)
                                .setMessage(R.string.text_location_permission)
                                .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_PERMISSION_LOCATION);
                                    }
                                })
                                .create()
                                .show();
                    }

                    // No explanation needed, we can request the permission.
                    else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSION_LOCATION);
                    }
                }
                // Permission granted
                else {
                    FusedLocationProviderClient fusedLocationClient;
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

                    Task<Location> locationTask = fusedLocationClient.getLastLocation();
                    locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Valid location retrieved
                            if( location != null ) {
                                transitionToDisplayQR(location.getLatitude(), location.getLongitude(), name);
                            }
                            else {
                                Log.v("GPS", "Could not retrieve coordinates");
                            }
                        }
                    });
                }
            }
        });
    }

    // Call the activity which handles encoding and displaying the QR
    private void transitionToDisplayQR(double latitude, double longitude, String name) {
        Intent intent = new Intent(this, DisplayQR.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("name", name);

        startActivity(intent);
    }
}