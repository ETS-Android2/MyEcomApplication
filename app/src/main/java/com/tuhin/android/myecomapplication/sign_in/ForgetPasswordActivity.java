package com.tuhin.android.myecomapplication.sign_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.tuhin.android.myecomapplication.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextView emailSentTv;
    private Button exitBtn, sentPswdResetBtn;
    private EditText emailEdt;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        auth = FirebaseAuth.getInstance();

        emailEdt = findViewById(R.id.forgetUserEmailEdt);

        emailSentTv = findViewById(R.id.passwrdEmailSEntTv);


        sentPswdResetBtn = findViewById(R.id.emailFrgtPswdSentBtn);
        getSupportActionBar().setTitle(getString(R.string.forget_password));

        CoordinatorLayout coordinatorLayout = findViewById(R.id.cdL);


        sentPswdResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailEdt.getText().toString().equals("")){
                    emailEdt.setError(getString(R.string.field_error));
                }else{
                    auth.sendPasswordResetEmail(emailEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                emailSentTv.setVisibility(View.VISIBLE);
                                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                        getString(R.string.profile_changed_successfully),
                                        Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snackbar.dismiss();
                                    }
                                }).show();
                            }
                        }
                    });
                }
            }
        });



    }
}