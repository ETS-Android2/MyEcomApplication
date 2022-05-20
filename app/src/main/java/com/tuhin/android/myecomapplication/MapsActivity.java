package com.tuhin.android.myecomapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tuhin.android.myecomapplication.address.AddressFieldPutActivity;
import com.tuhin.android.myecomapplication.databinding.ActivityMapsBinding;
import com.tuhin.android.myecomapplication.utills.Utills;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final int LOCATION_PERMISSION_ACCESS_KEY = 100;
    private FusedLocationProviderClient locationProviderClient;
    private Location lastKnownLocation;
    private LatLng latLng;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    private CameraPosition cameraPosition;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 15;
    public static final String LATITUDE_INTENT_KEY = "MapsActivity.latitude";
    public static final String LONGITUDE_INTENT_KEY = "MapsActivity.longitude";
    public static final String ID_EXTRA_INTENT_KEY = "MapsActivity.longitude";
    public static final int ACTIVITY_TO_ADDRESS_REQUEST_KEY = 1100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ACCESS_KEY);

        } else {
            getCurrentLocationOfTheUser();
        }
    }

    private void getCurrentLocationOfTheUser() {
        if (Utills.checkWheatherLocationIsEnebeledOrNot(MapsActivity.this)) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Task<Location> locationResult = locationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        lastKnownLocation = task.getResult();
                        if(lastKnownLocation!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                            latLng = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                            if(latLng!=null){

                                mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                                getGeoCode(latLng);
                            }

                        }else{
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }

                    }
                });
            }

        }else{
            Toast.makeText(this, "Please Turn on your location", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_ACCESS_KEY && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationOfTheUser();
            }

        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getGeoCode(LatLng latLng){
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        }catch (Exception e){
            addresses = new ArrayList<>();

        }
        if(!addresses.isEmpty()){
            Address adres = addresses.get(0);
            String address = adres.getAddressLine(0);
            String postalCode = adres.getPostalCode();
            String city = adres.getLocality();
            String state = adres.getAdminArea();
            Log.d("zip",postalCode);
            Log.d("addr",address);
            Log.d("city",city);

        }else{
            Toast.makeText(this, getString(R.string.cannot_fetch_addrss), Toast.LENGTH_SHORT).show();
        }

    }

    public void saveMapAddr(View view){
        if(view.getId()==R.id.saveMapAddrBtn){
            double lati = latLng.latitude;
            double longi = latLng.longitude;

            Intent intent = new Intent(this, AddressFieldPutActivity.class);
            if(latLng!=null){
                intent.putExtra(LATITUDE_INTENT_KEY,lati);
                intent.putExtra(LONGITUDE_INTENT_KEY,longi);
                Log.d("lati", String.valueOf(lati));
                Log.d("longi", String.valueOf(longi));
                //int id = getIntent().getIntExtra(ID_EXTRA_INTENT_KEY,-1);


                startActivity(intent);
                //finish();
            }


            //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        }

    }


}