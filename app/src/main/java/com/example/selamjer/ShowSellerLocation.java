package com.example.selamjer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ShowSellerLocation extends AppCompatActivity {

    SupportMapFragment smf_googleMap;
    FusedLocationProviderClient client;

    private static final int REQUEST_LOCATION = 100;

    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_location);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        smf_googleMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maps);
        client = LocationServices.getFusedLocationProviderClient(this);
        getMyLocation();

        // Set multiple latitude and longitude values for service centres
        double[] serviceLatitudes = {2.9929, 2.9765, 2.9291, 2.9326, 2.9599, 2.9259, 2.9710, 2.9647};
        double[] serviceLongitudes = {101.7928, 101.7200, 101.6742, 101.7644, 101.7542, 101.7780, 101.7743, 101.7793};

        // Choose which set of coordinates you want to use
        int selectedCoordinatesIndex = 1;  // Change this index to choose different coordinates

        latitude = serviceLatitudes[selectedCoordinatesIndex];
        longitude = serviceLongitudes[selectedCoordinatesIndex];


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(ShowSellerLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
            }
        }
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                smf_googleMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {

                        // Create a list to store MarkerOptions
                        ArrayList<Object> markerOptionsList = new ArrayList<>();

                        // Iterate through the service centres and add markers
                        double[] serviceLatitudes = {2.9929, 2.9765, 2.9291, 2.9326, 2.9599, 2.9259, 2.9710, 2.9647};
                        double[] serviceLongitudes = {101.7928, 101.7200, 101.6742, 101.7644, 101.7542, 101.7780, 101.7743, 101.7793};
                        for (int i = 0; i < serviceLatitudes.length; i++) {
                            LatLng latLng = new LatLng(serviceLatitudes[i], serviceLongitudes[i]);
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title("Thrift Shop " + (i + 1))
                                    .snippet("Open from 10am - 10pm");

                            // Add each marker option to the list
                            markerOptionsList.add(markerOptions);

                            // Add marker to the map
                            googleMap.addMarker(markerOptions);
                        }

                        // If you want to focus the camera on a specific marker, use the selectedCoordinatesIndex
                        LatLng selectedLatLng = new LatLng(latitude, longitude);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 12));

                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, Homepage.class);

        // Add any additional flags if needed
        // For example, to clear the back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Start ServiceDashboard
        startActivity(intent);

        // Finish the current activity to remove it from the back stack
        finish();
    }
}