package com.tuhin.android.myecomapplication.sign_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuhin.android.myecomapplication.MainActivity;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.verfication.OtpActivity;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText userEmailEdt,paswdEdtText;
    private Button signInBtn,newREgBtn;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userEmailEdt = findViewById(R.id.emailSignInEdttxt);
        paswdEdtText = findViewById(R.id.pswdSignInEdttxt);

        signInBtn = findViewById(R.id.signInBtn);
        newREgBtn = findViewById(R.id.backTosignUpBtn);
        auth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                auth.signInWithEmailAndPassword(userEmailEdt.getText().toString(),paswdEdtText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();

                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.fail_to_sign_in,task.getException()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        newREgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, OtpActivity.class));
            }
        });
    }


}