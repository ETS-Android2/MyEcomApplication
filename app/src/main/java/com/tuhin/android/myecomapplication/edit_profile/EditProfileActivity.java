package com.tuhin.android.myecomapplication.edit_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuhin.android.myecomapplication.MapsActivity;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.address.AddressFieldPutActivity;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;

public class EditProfileActivity extends AppCompatActivity {
    public static final int EDIT_ADRRESS_INTENT_VALUE = 20;
    public static final String EDIT_HOME_INTENT_KEY = "home_edit";
    public static final String EDIT_CITY_INTENT_KEY = "city_edit";
    public static final String EDIT_ZIP_INTENT_KEY = "pin_edit";
    public static final String EDIT_LANDMARK_INTENT_KEY = "landmark_edit";


    private Button chnagePswdActBtn, savePrfBtn, changeAddressBtn;
    private TextView userEmailTv;
    private TextInputEditText nameEdtTv, phoneNumEdt;

    private FirebaseUser currentUser;
    private FirebaseDatabase mDatabase;
    private LinearLayout ll2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        changeAddressBtn = findViewById(R.id.edtPrfChangeAddrssBtn);
        chnagePswdActBtn = findViewById(R.id.chngPasswdEdtBtn);
        savePrfBtn = findViewById(R.id.saveEdtProfileBtn);

        userEmailTv = findViewById(R.id.edtProfileMailTv);

        nameEdtTv = findViewById(R.id.edtNameEdttxt);
        phoneNumEdt = findViewById(R.id.edtPhoneEdttxt);

        ll2 = findViewById(R.id.ll2);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL);

        userEmailTv.setText(currentUser.getEmail());
        nameEdtTv.setText(currentUser.getDisplayName());


        savePrfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileToDatabase();
            }
        });

        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddressDataFromServer();




            }
        });

        chnagePswdActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, PasswordChnageActivity.class));
            }
        });
    }

    private void saveProfileToDatabase() {

        String userId = currentUser.getUid();

        DatabaseReference rootRef = mDatabase.getReference(NodeNames.USERS).child(userId);

        if (nameEdtTv.getText().toString().equals("")) {
            nameEdtTv.setError("Field Mustbe filled");

        } else if (phoneNumEdt.getText().toString().trim().equals("")) {
            phoneNumEdt.setError("Field Mustbe filled");
        }
        else {

            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameEdtTv.getText().toString())
                    .build();
            currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        rootRef.child(NodeNames.NAME).setValue(nameEdtTv.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    rootRef.child(NodeNames.PHONE).setValue(phoneNumEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                userEmailTv.setText(currentUser.getDisplayName());
                                                Snackbar snackbar = Snackbar.make(ll2,
                                                        getString(R.string.profile_changed_successfully),
                                                        Snackbar.LENGTH_LONG);
                                                snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snackbar.dismiss();
                                                    }
                                                }).show();

                                            } else {
                                                Snackbar.make(ll2,
                                                        getString(R.string.something_went_wrong, task.getException().toString()),
                                                        Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar.make(ll2,
                                            getString(R.string.something_went_wrong, task.getException().toString()),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Snackbar.make(ll2,
                                getString(R.string.something_went_wrong, task.getException().toString()),
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });


            Log.d("void",nameEdtTv.getText().toString());

        }

    }


    private void getAddressDataFromServer(){

        String userId = currentUser.getUid();

        DatabaseReference rootRef = mDatabase.getReference(NodeNames.USERS).child(userId);
        rootRef.child(NodeNames.ADDRESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().toString().trim().equals("")){
                    String address = snapshot.getValue().toString();
                    Log.d("addr",address);
                    String[] adr = address.split(";");
                    String home = adr[0];
                    String city = adr[1];
                    String zip = adr[2];
                    String landmark = adr[3];
                    Intent data = new Intent(EditProfileActivity.this,AddressFieldPutActivity.class);
                    data.putExtra(MapsActivity.ID_EXTRA_INTENT_KEY,EDIT_ADRRESS_INTENT_VALUE);
                    data.putExtra(EDIT_HOME_INTENT_KEY,home);
                    data.putExtra(EDIT_CITY_INTENT_KEY,city);
                    data.putExtra(EDIT_ZIP_INTENT_KEY,zip);
                    data.putExtra(EDIT_LANDMARK_INTENT_KEY,landmark);

                    startActivity(data);
                }
                Intent data1 = new Intent(EditProfileActivity.this,AddressFieldPutActivity.class);
                data1.putExtra(MapsActivity.ID_EXTRA_INTENT_KEY,EDIT_ADRRESS_INTENT_VALUE);
                startActivity(data1);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(EditProfileActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });


    }
}