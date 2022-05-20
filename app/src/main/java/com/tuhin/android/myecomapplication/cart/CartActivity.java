package com.tuhin.android.myecomapplication.cart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tuhin.android.myecomapplication.R;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cartFrameContainer,CartFragment.newInstance())
                .commit();
    }
}