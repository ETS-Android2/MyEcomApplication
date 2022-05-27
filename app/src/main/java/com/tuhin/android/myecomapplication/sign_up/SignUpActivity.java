package com.tuhin.android.myecomapplication.sign_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuhin.android.myecomapplication.MapsActivity;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;
import com.tuhin.android.myecomapplication.verfication.OtpActivity;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {



    private TextInputEditText userEmailEdt,paswdEdtTex,userNAmeEdt,confPswdEdt,phoneSignUpEdtTxt;
    private Button signUpBtn,exitBtn;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference  rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userEmailEdt = findViewById(R.id.emailSignUpEdttxt);
        userNAmeEdt = findViewById(R.id.nameSignUpEdttxt);
        paswdEdtTex = findViewById(R.id.pswdSignUpEdttxt);
        confPswdEdt= findViewById(R.id.confPswdSignUpEdttxt);
        phoneSignUpEdtTxt = findViewById(R.id.phoneSignUpEdttxt);

        signUpBtn= findViewById(R.id.signUpBtn);
        exitBtn = findViewById(R.id.exitBtn);
        rootRef = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL).getReference().child(NodeNames.USERS);
        rootRef.keepSynced(true);

        if(getIntent()!=null){
            phoneSignUpEdtTxt.setText(getIntent().getStringExtra(OtpActivity.PHONE_NUMBER_INTENT_KEY));
        }


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userNAmeEdt.getText().toString()==null){
                    userNAmeEdt.setError(getString(R.string.valid_username));
                }else if(!Patterns.EMAIL_ADDRESS.matcher(userEmailEdt.getText().toString()).matches() && userEmailEdt.getText().toString()==null){
                    userEmailEdt.setError(getString(R.string.valid_email));
                }else if(paswdEdtTex.length()<8){
                    paswdEdtTex.setError(getString(R.string.wrong_psswd));
                }else if(!paswdEdtTex.getText().toString().equals(confPswdEdt.getText().toString())){
                    confPswdEdt.setError(getString(R.string.passwords__shd_same_error));
                }else{
                    auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(userEmailEdt.getText().toString(), confPswdEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                firebaseUser = auth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "Created Succesfully", Toast.LENGTH_SHORT).show();

                                String userId = firebaseUser.getUid();
                                Toast.makeText(getApplicationContext(), auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userNAmeEdt.getText().toString())
                                        .build();
                                firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
//                                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(getApplicationContext(), firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();

                                            Map<String,String> signUpDataMap = new HashMap<>();
                                            signUpDataMap.put(NodeNames.NAME,userNAmeEdt.getText().toString());
                                            signUpDataMap.put(NodeNames.EMAIL,userEmailEdt.getText().toString());
                                            signUpDataMap.put(NodeNames.PINCODE,"");
                                            signUpDataMap.put(NodeNames.PHONE,phoneSignUpEdtTxt.getText().toString());
                                            signUpDataMap.put(NodeNames.ADDRESS,"");
                                            signUpDataMap.put(NodeNames.PHOTO,"");
                                            rootRef.child(userId).setValue(signUpDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        Toast.makeText(getApplicationContext(), getString(R.string.sign_cmplt), Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_register,task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                        }else {
                                            Toast.makeText(getApplicationContext(), getString(R.string.signfailed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else{
                                Toast.makeText(getApplicationContext(), getString(R.string.fail_to_signup), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.signfailed,
                                    e.getMessage().toString()), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

                }


            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });


    }

}