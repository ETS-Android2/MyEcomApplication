package com.tuhin.android.myecomapplication.address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuhin.android.myecomapplication.MainActivity;
import com.tuhin.android.myecomapplication.MapsActivity;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;
import com.tuhin.android.myecomapplication.edit_profile.EditProfileActivity;
import com.tuhin.android.myecomapplication.utills.Utills;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressFieldPutActivity extends AppCompatActivity {

    private TextInputEditText addressEdtTxt, zipPinEdtTxt, cityEdtTxt, landMarkEdtTxt;
    private Button saveAddressBtn, getCurrentLocationBtn;
    private TextView userNAmeTv;

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private LatLng latLng;
    private double lati;
    private double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_field_put);

        addressEdtTxt = findViewById(R.id.areaEdtTxt);
        zipPinEdtTxt = findViewById(R.id.pinEdtTxt);
        cityEdtTxt = findViewById(R.id.cityEdtTxt);
        landMarkEdtTxt = findViewById(R.id.landmarkEdtTxt);

        userNAmeTv = findViewById(R.id.addrFldNameTv);

        saveAddressBtn = findViewById(R.id.saveAddressAdrFieldBtn);
        getCurrentLocationBtn = findViewById(R.id.getCurrentLocationBtn);
        firebaseDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("crrUs", currentUser.getEmail());
        if (getIntent() != null) {
            Intent data = getIntent();

            if(data.getIntExtra(MapsActivity.ID_EXTRA_INTENT_KEY,0)== EditProfileActivity.EDIT_ADRRESS_INTENT_VALUE){
                getCurrentLocationBtn.setVisibility(View.VISIBLE);
                addressEdtTxt.setText(data.getStringExtra(EditProfileActivity.EDIT_HOME_INTENT_KEY));
                cityEdtTxt.setText(data.getStringExtra(EditProfileActivity.EDIT_CITY_INTENT_KEY));
                zipPinEdtTxt.setText(data.getStringExtra(EditProfileActivity.EDIT_ZIP_INTENT_KEY));
                landMarkEdtTxt.setText(data.getStringExtra(EditProfileActivity.EDIT_LANDMARK_INTENT_KEY));
            }
            else{
                Log.d("yoo123", "i hab something");
                lati = data.getDoubleExtra(MapsActivity.LATITUDE_INTENT_KEY, 0.00);
                longi = data.getDoubleExtra(MapsActivity.LONGITUDE_INTENT_KEY, 0.00);
                if (lati != 0.00 && longi != 0.00) {
                    latLng = new LatLng(lati, longi);
                    getGeoCodeandSet(latLng);

                }
                //Log.d("extra", String.valueOf(data.getIntExtra(MapsActivity.ID_EXTRA_INTENT_KEY, 100021)));
            }
        }

        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLng != null) {
                    saveAndAddAddress();
                } else {
                    Toast.makeText(AddressFieldPutActivity.this, "Add Address to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

    }


    private void getGeoCodeandSet(LatLng latLng) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (Exception e) {
            addresses = new ArrayList<>();
            Log.d("erro", e.getMessage().toString());
        }

        if (!addresses.isEmpty()) {
            Address adres = addresses.get(0);
            String address = adres.getAddressLine(0);
            String postalCode = adres.getPostalCode();
            String city = adres.getLocality();
            String state = adres.getAdminArea();
            addressEdtTxt.setText(address);
            cityEdtTxt.setText(city);
            zipPinEdtTxt.setText(postalCode);

            Log.d("zip1", postalCode);
            Log.d("addr1", address);
            Log.d("city1", city);
        } else {
            Toast.makeText(this, getString(R.string.cannot_fetch_addrss), Toast.LENGTH_SHORT).show();
        }

    }

    private void saveAndAddAddress() {
        //if(isDeleiveryPossibleInThisPin(zipPinEdtTxt.getText().toString())){
            DatabaseReference rootREf = firebaseDatabase.getReference(NodeNames.USERS)
                    .child(currentUser.getUid())
                    .child(NodeNames.ADDRESS);

            String addressServ = addressEdtTxt.getText().toString() + "; " + cityEdtTxt.getText().toString() + "; "
                    + zipPinEdtTxt.getText().toString() + "; " + landMarkEdtTxt.getText().toString()+";";

            rootREf.setValue(addressServ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddressFieldPutActivity.this,
                                getString(R.string.address_added),
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddressFieldPutActivity.this,
                                MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AddressFieldPutActivity.this,
                                getString(R.string.failed_add_address,
                                        task.getException().toString()),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
//        }else{
//            Toast.makeText(this, "Service isn't available in this location", Toast.LENGTH_SHORT).show();
//        }

    }

    private void getCurrentLocation(){
        addressEdtTxt.setText("");
        cityEdtTxt.setText("");
        zipPinEdtTxt.setText("");

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(AddressFieldPutActivity.this);

        if(Utills.checkWheatherLocationIsEnebeledOrNot(this)){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(AddressFieldPutActivity.this, MapsActivity.class);
                startActivity(intent);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},205);
            }

        }else{
            Toast.makeText(this, "Please Turn on your location", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean isDeleiveryPossibleInThisPin(String pin) {
        if(Integer.parseInt(pin)>=743317 && Integer.parseInt(pin)<=743320){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==205){
            getCurrentLocation();
        }
    }
}