package com.example.trackme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "asf";

    Button btn;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    //Database Firebase
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference myRef=database.getReference("Devices");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn=(Button) findViewById(R.id.btn);
        //myRef.child(Build.MANUFACTURER+Build.MODEL);

        callPermisions();

    }


    public void requestLocationUpdate(){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PermissionChecker.PERMISSION_GRANTED
        ) {

            fusedLocationClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(2000);
            locationRequest.setInterval(4000);
            Toast.makeText(getApplicationContext(),"Your Device is being tracked",Toast.LENGTH_LONG).show();

            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    Log.e(TAG, "lat" + locationResult.getLastLocation().getLatitude() + "long" + locationResult.getLastLocation().getLongitude());
                    myRef.child(Build.MANUFACTURER + Build.MODEL).child("Latitude").setValue(locationResult.getLastLocation().getLatitude());
                    myRef.child(Build.MANUFACTURER + Build.MODEL).child("Longitude").setValue(locationResult.getLastLocation().getLongitude());

                    Toast.makeText(getApplicationContext(), "Latitude" + locationResult.getLastLocation().getLatitude()+"Longitude" + locationResult.getLastLocation().getLongitude() ,Toast.LENGTH_LONG).show();

                }
            } ,getMainLooper());

        }else callPermisions();

    }

    public void callPermisions(){

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, "Location Permissions are required to get user location"/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        requestLocationUpdate();

                    }
                });

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermisions();
            }
        });

    }
}
