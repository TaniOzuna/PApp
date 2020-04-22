package com.example.papp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private static final String TAG = "MapsActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    DatabaseReference mDatabase;
    ImageButton mMarker;
    //Button btn = (Button) findViewById(R.id.boton);

    private View infoWindow;
    //ImageButton btn_home;
    //private Button click;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "empezó a correr");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase = db.getReference("ubicacion");

        Button click = (Button) findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subirLatLongFirebase();
                Intent intent = new Intent(MapsActivity.this, sugerencia.class);
                startActivity(intent);
            }
        });

    }


    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        mMap = googleMap;

        Button btn = (Button) findViewById(R.id.boton);


        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }


        final HashMap<Marker, Integer> markerMap = new HashMap<Marker, Integer>();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        final LatLng Catedral = new LatLng(20.677039, -103.3491727);
        final LatLng Metropolitano = new LatLng(20.6712501, -103.4405329);
        final LatLng Iteso = new LatLng(20.611072, -103.4181287);


        Marker marker1 = googleMap.addMarker(new MarkerOptions().position(Catedral).title("FADU")
                .snippet("Facultad de Arquitectura, Diseño y Urbanismo")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        markerMap.put(marker1, R.drawable.imagen);

        Marker marker2 = googleMap.addMarker(new MarkerOptions().position(Metropolitano).title("TEOREMA")
                .snippet("san matin 1245 blablaklalala"));
        markerMap.put(marker2, R.drawable.imagen);

        Marker marker3 = googleMap.addMarker(new MarkerOptions().position(Iteso).title("El Mundo del Acrilico").snippet("san benito 2144/"));
        markerMap.put(marker3, R.drawable.imagen);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom, null);

                TextView titulo = (TextView) v.findViewById(R.id.titulo);
                TextView direccion = (TextView) v.findViewById(R.id.direccion);
                ImageView imagen = ((ImageView) v.findViewById(R.id.imagen));
                //RatingBar ratingBar = ((RatingBar) v.findViewById(R.id.rating_bar));

                titulo.setText(marker.getTitle());
                direccion.setText(marker.getSnippet());

                if (markerMap.get(marker) != null)
                    imagen.setImageDrawable(getResources().getDrawable(markerMap.get(marker)));

                return v;
            }
        });

        // este metodo abre el activity de rating stars, cuando se da click en el infowindow
        /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(MapsActivity.this, calificacion.class);
                startActivity(intent);
            }

        });*/


        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, calificacion.class);
                startActivity(intent);
            }

        });*/
    }

    //permisos a partir de aquí-----------------------------------------------------------------------------


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                                                   @Override
                                                   public void onComplete(@NonNull Task task) {
                                                       if(task.isSuccessful()){
                                                           Log.d(TAG, "onComplete: found location! ");
                                                           Location currentLocation = (Location)task.getResult();

                                                           //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ubicacion"); //recien lo agreggue

                                                           moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                                                   DEFAULT_ZOOM,
                                                                   "My Location");

                                                       }else{
                                                           Log.d(TAG, "onComplete: current location is null");
                                                           Toast.makeText(MapsActivity.this, "unable to get current" +
                                                                   "location", Toast.LENGTH_SHORT).show();
                                                       }

                                                   }
                                               }
                );

            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        /*if(!title.equals("My location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }*/


    }


    //}

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions ={Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0) {
                    for( int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }


}


