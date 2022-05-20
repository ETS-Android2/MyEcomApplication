package com.tuhin.android.myecomapplication.verfication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.sign_up.SignUpActivity;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private Button verifyBtn,sendOtpBtn,resendOtpBtn;
    private TextView phoneNoTv,phNumberTv,sendOtpTv,verifyOtpTv;
    private EditText phoneNumberEdt;
    private LinearLayout phoneNumberLL;
    private com.chaos.view.PinView pinView;

    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationCode;
    public static final String VEIRFICATION_INTENT_KEY = "verification_code";
    private static final String PHONE_NUMBER_MATCHING_REGEX = "^[1-9][0-9]{9}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        verifyBtn = findViewById(R.id.otpVerifyBtn);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        resendOtpBtn = findViewById(R.id.reSendOtpBtn);
        phNumberTv = findViewById(R.id.phNumberTv);
        sendOtpTv = findViewById(R.id.sentOtpMsgTv);
        verifyOtpTv = findViewById(R.id.verifyOtpTv);
        phoneNumberLL = findViewById(R.id.phoneNumberLL);
        phoneNumberEdt = findViewById(R.id.phoneNoEdtPhone);


        pinView = findViewById(R.id.pin_view);

        auth = FirebaseAuth.getInstance();

        phNumberTv.setVisibility(View.VISIBLE);
        phoneNumberLL.setVisibility(View.VISIBLE);


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(pinView.getText().toString().equals(code)){
                    verifyCode(code);
                }else{

                    pinView.setError(getString(R.string.wrong_otp));

                }

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode=s;
            }
        };

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!phoneNumberEdt.equals(" ")){
                    if(phoneNumberEdt.getText().toString().matches(PHONE_NUMBER_MATCHING_REGEX)){
                        String number = "+91"+phoneNumberEdt.getText().toString();
                        sendVerificationCode(number);
                        phNumberTv.setVisibility(View.GONE);
                        phoneNumberLL.setVisibility(View.GONE);
                        sendOtpBtn.setVisibility(View.GONE);
                        verifyOtpTv.setVisibility(View.VISIBLE);
                        sendOtpTv.setVisibility(View.VISIBLE);
                        pinView.setVisibility(View.VISIBLE);
                        verifyBtn.setVisibility(View.VISIBLE);
                        resendOtpBtn.setVisibility(View.VISIBLE);

                    }else{
                        phoneNumberEdt.setError(getString(R.string.enter_valid_number));
                    }
                }else{
                    phoneNumberEdt.setError(getString(R.string.enter_valid_number));
                }

            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pinView.getText().toString().equals(" ")){
                   pinView.setError(getString(R.string.enter_valid_number));
                }
                verifyCode(pinView.getText().toString());
            }
        });

    }

    private void sendVerificationCode(String number) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number)
                .setTimeout(2L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,code);
        startActivity(new Intent(OtpActivity.this, SignUpActivity.class));
    }
}