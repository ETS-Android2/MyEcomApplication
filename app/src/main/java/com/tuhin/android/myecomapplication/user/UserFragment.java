package com.tuhin.android.myecomapplication.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuhin.android.myecomapplication.MainActivity;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.admin.ProductEntryActivity;
import com.tuhin.android.myecomapplication.contact_us.ContactActivity;
import com.tuhin.android.myecomapplication.edit_profile.EditProfileActivity;
import com.tuhin.android.myecomapplication.orders.OrdersActivity;
import com.tuhin.android.myecomapplication.sign_in.SignInActivity;

public class UserFragment extends Fragment {
    private TextView editProfileTv,previousOrdersTv,orderStatusTv;
    private Button contactUs,logoutBtn;
    private FirebaseAuth auth;

    public UserFragment() {

    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editProfileTv = view.findViewById(R.id.userTv);
        previousOrdersTv = view.findViewById(R.id.previous_ordersTv);
        orderStatusTv = view.findViewById(R.id.orderStatusTv);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        contactUs = view.findViewById(R.id.contactUsBtn);

        editProfileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        previousOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrdersActivity.class));
            }
        });

        orderStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrdersActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(getActivity(),SignInActivity.class));
                getActivity().finish();
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProductEntryActivity.class));
                //getActivity().finish();
            }
        });


    }
}