package com.tuhin.android.myecomapplication.sign_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuhin.android.myecomapplication.MapsActivity;
import com.tuhin.android.myecomapplication.R;

public class EmailVerificationActivity extends AppCompatActivity {

    private TextView verifiedEmialTv;
    private Button sendLinkBtn, setLocationActivityBtn, resendEmailBtn;
    private LinearLayout btnLL, userLL;
    private EditText usernameEdt, userPswdEdt;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_verification);
        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser();

        verifiedEmialTv = findViewById(R.id.verifiedTv);

        usernameEdt = findViewById(R.id.usernameEmVfEdt);
        userPswdEdt = findViewById(R.id.userpswdEmVfEdt);

        sendLinkBtn = findViewById(R.id.sendLinkBtn);
        resendEmailBtn = findViewById(R.id.reSendLinkBtn);
        setLocationActivityBtn = findViewById(R.id.setLocationNextBtn);

        btnLL = findViewById(R.id.btnLL);
        userLL = findViewById(R.id.userLL);


        if (currentUser.isEmailVerified()) {

            emailVerified();
        } else {
            btnLL.setVisibility(View.VISIBLE);
            userLL.setVisibility(View.VISIBLE);
            setLocationActivityBtn.setVisibility(View.GONE);
            sendLinkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (usernameEdt.getText().toString().equals("")) {
                        usernameEdt.setError(getString(R.string.field_error));
                    } else if (userPswdEdt.getText().toString().equals("")) {
                        usernameEdt.setError(getString(R.string.field_error));
                    } else {
                        auth.signInWithEmailAndPassword(usernameEdt.getText().toString(), userPswdEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EmailVerificationActivity.this, getString(R.string.sign_in), Toast.LENGTH_SHORT).show();
                                    if (!task.getResult().getUser().isEmailVerified()) {
                                        sendEmainVerification(task.getResult().getUser());
                                        sendLinkBtn.setText("Refresh");
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            auth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    if (auth.getCurrentUser().isEmailVerified()) {
                                                        emailVerified();
                                                        Toast.makeText(EmailVerificationActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(EmailVerificationActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                                        //resendLink(auth.getCurrentUser());
                                                    }
                                                }
                                            });
                                        }
                                    }, 9000);
                                } else {
                                    Toast.makeText(EmailVerificationActivity.this, getString(R.string.fail_to_sign_in, task.getException().toString()), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

        }

        setLocationActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailVerificationActivity.this, MapsActivity.class));
                finish();
            }
        });

        resendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmainVerification(currentUser);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (currentUser.isEmailVerified()) {
                    emailVerified();

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (currentUser.isEmailVerified()) {
                    emailVerified();

                }
            }
        });
    }


    private void resendLink(FirebaseUser user) {
        sendLinkBtn.setEnabled(false);
       // Toast.makeText(this, "Try again by re-sending the link ", Toast.LENGTH_SHORT).show();
        resendEmailBtn.setEnabled(true);
        resendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmainVerification(user);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (user.isEmailVerified()) {
                                    emailVerified();
                                    return;
                                } else {
                                    resendLink(user);
                                }
                            }
                        });
                    }
                }, 6000);
            }
        });

    }


    private void sendEmainVerification(FirebaseUser curntUS) {
        if (!curntUS.isEmailVerified()) {
            curntUS.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EmailVerificationActivity.this, getString(R.string.email_sent), Toast.LENGTH_SHORT).show();


                    } else {
                        resendEmailBtn.setEnabled(true);
                        Toast.makeText(EmailVerificationActivity.this, getString(R.string.email_sent_faild, task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    resendEmailBtn.setEnabled(true);

                }
            });
        }

    }

    private void emailVerified() {
        userLL.setVisibility(View.GONE);
        btnLL.setVisibility(View.GONE);
        setLocationActivityBtn.setVisibility(View.VISIBLE);

        verifiedEmialTv.setText(R.string.email_veified);
        verifiedEmialTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic__verified), null, null);
    }

    //needed a recusrsion fumction


}