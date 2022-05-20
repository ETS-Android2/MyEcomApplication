package com.tuhin.android.myecomapplication.category_list;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;


public class CategoryFragment extends Fragment {


    int[] drawablePic = {R.drawable.grocery,
            R.drawable.fresh,
            R.drawable.alpla_home_care_2,
            R.drawable.baby_products,
            R.drawable.electronics_items,
            R.drawable.beauty_images};

    int[] advImg = {R.drawable.wow_coupon_code,R.drawable.big_diwali_sale

    };

    String[] tittles = {
            "Grocery",
            "Fresh",
            "Home Care",
            "Baby Products",
            "Electronics",
            "Beauty Products"
    };

    private FirebaseDatabase database;
    private RecyclerView recyclerView;
    private SliderView imageSliderView;
    private TextView deliverdAddrTv;
    private FirebaseUser currentUser;

    private String[] arr;


    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL);
        DatabaseReference rootRef = database.getReference(NodeNames.USERS).child(currentUser.getUid()).child(NodeNames.ADDRESS);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().toString().trim().equals("")){
                    arr = snapshot.getValue().toString().split(";");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.categoryRv);
        imageSliderView = view.findViewById(R.id.imageSlider);
        deliverdAddrTv = view.findViewById(R.id.deleiveredTittleTv);
        if(arr!=null){
            deliverdAddrTv.setText("Delivered To "+currentUser.getDisplayName()+" "+arr[1]+" -"+arr[2]);
        }else{
            deliverdAddrTv.setText("Delivered To "+currentUser.getDisplayName());
        }


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setAdapter(new CategoryListEvAdapter(drawablePic,tittles,getActivity()));

        ImageSliderSetAaapter adapter = new ImageSliderSetAaapter(getActivity(),advImg);
        imageSliderView.setSliderAdapter(adapter);

        imageSliderView.setIndicatorAnimation(IndicatorAnimationType.SWAP); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSliderView.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
        imageSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        imageSliderView.setIndicatorSelectedColor(Color.WHITE);
        imageSliderView.setIndicatorUnselectedColor(Color.GRAY);
        imageSliderView.setScrollTimeInSec(3); //set scroll delay in seconds :
        imageSliderView.startAutoCycle();


    }



}