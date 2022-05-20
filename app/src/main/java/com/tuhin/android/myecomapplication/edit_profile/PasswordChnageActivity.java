package com.tuhin.android.myecomapplication.edit_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuhin.android.myecomapplication.R;

public class PasswordChnageActivity extends AppCompatActivity {

    private TextInputEditText oldPswdEdt, newPswdEdt, reEnPswdEdt;
    private Button changePswdBtn;
    private FirebaseUser currentUser;
    private LinearLayout ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_chnage);

        oldPswdEdt = findViewById(R.id.oldPswdEdttxt);
        newPswdEdt = findViewById(R.id.newPswdEdttxt);
        reEnPswdEdt = findViewById(R.id.newPswdCnfEdttxt);

        changePswdBtn = findViewById(R.id.changeChnPasswordBtn);
        ll = findViewById(R.id.ll);

        changePswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePassword();
            }
        });

    }


    private void changePassword() {
        if (oldPswdEdt.getText().toString().equals(" ")) {
            oldPswdEdt.setError("Field Mustbe filled");
        } else if (newPswdEdt.getText().toString().equals(" ")) {
            oldPswdEdt.setError("Field Mustbe filled");
        } else if (newPswdEdt.length() < 8) {
            newPswdEdt.setError(getString(R.string.wrong_psswd));
        } else if (!reEnPswdEdt.getText().toString().equals(reEnPswdEdt.getText().toString())) {
            reEnPswdEdt.setError(getString(R.string.passwords__shd_same_error));
        }else{
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String email = currentUser.getEmail();
            AuthCredential authCredential = EmailAuthProvider.getCredential(email,oldPswdEdt.getText().toString());
            currentUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Snackbar.make(ll,
                                R.string.pswd_change_scsfly,
                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.exit, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        }).show();
                    }else{
                        Snackbar.make(ll,
                                getString(R.string.pswd_change_failed,task.getException().toString()),
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }



    }
}