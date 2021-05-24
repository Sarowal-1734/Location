package com.example.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    double latitude, longitude;
    TextView textView;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onGetMyLocationButtonClicked(View view) {
        getLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getLocation() {
        if (checkPermission()) {
//            if (locationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            textView.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                        } else {
                            requestLocation();
                        }
                    }
                });
//            } else {
//                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
//            }
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        }, 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2 && grantResults.length > 0) {
            // Permission Granted. Now get the location
            getLocation();
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    // Checking GPS on or not
//    @RequiresApi(api = Build.VERSION_CODES.P)
//    private boolean locationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (locationManager.isLocationEnabled()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    private void requestLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        checkPermission();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper());

    }
    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                latitude = location.getLatitude();
                longitude = location.getLatitude();
                textView.setText("Latitude: " + latitude + ", Longitude: " + longitude);
            }
        }
    };

    public void onOpenMapButtonClicked(View view) {
        String locationUriString = "geo:" + latitude + "," + longitude + "?q=()@" + latitude + "," + longitude;
        Uri locationUri = Uri.parse(locationUriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
        startActivity(mapIntent);
    }
}