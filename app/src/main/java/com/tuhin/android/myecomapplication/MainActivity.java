package com.tuhin.android.myecomapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuhin.android.myecomapplication.cart.CartFragment;
import com.tuhin.android.myecomapplication.category_list.CategoryFragment;
import com.tuhin.android.myecomapplication.user.UserFragment;


public class MainActivity extends AppCompatActivity {
   private FrameLayout container;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        container = findViewById(R.id.mainFrameContainer);
        bottomNavigationView = findViewById(R.id.bottomNavView);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameContainer, new CategoryFragment())
                .commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()){

                case R.id.catgory:

                    fragment = new CategoryFragment();
                    break;
                case R.id.cart:
                    fragment = new CartFragment();
                    break;
                case R.id.user:
                    fragment = new UserFragment();
                    break;

            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameContainer, fragment)
                    .commit();
            return false;
        }
    };


}

