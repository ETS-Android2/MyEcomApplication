package com.tuhin.android.myecomapplication.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;
import com.tuhin.android.myecomapplication.model.ProductModel;

import java.util.ArrayList;


public class CartFragment extends Fragment {


    private ArrayList<ProductModel> cartProducts;
    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private DatabaseReference productRef;
    private TextView productTotalSumTv, totalAmountTv, emptyCartTv;
    private ScrollView scrollView;
    private ChildEventListener childEventListener;
    private CartRecyclerAdapter adapter;
    private double sum ;


    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();

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
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        productTotalSumTv = view.findViewById(R.id.totalProductCostTv);
        totalAmountTv = view.findViewById(R.id.totalAmountTv);
        emptyCartTv = view.findViewById(R.id.emptyCartTv);
        scrollView = view.findViewById(R.id.scrollView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL);

        cartProducts = new ArrayList<>();
        adapter = new CartRecyclerAdapter(getActivity(), cartProducts);
        recyclerView.setAdapter(adapter);
        loadCartData();
        getCartPrice();


    }

    private void loadCartData() {

        productRef = mDatabase
                .getReference(NodeNames.PRODUCT);
        DatabaseReference rootRef = mDatabase.getReference(NodeNames.USERS).child(currentUser.getUid()).child(NodeNames.CART);
        DatabaseReference carRef = rootRef;

        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String key = snapshot.getKey();

                carRef.child(key).child(NodeNames.CART_PRODUCT_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String productId = snapshot.getValue().toString();
                        productRef.child(productId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    ProductModel productModel = snapshot.getValue(ProductModel.class);
                                    cartProducts.add(productModel);
                                    emptyCartTv.setVisibility(View.GONE);
                                    scrollView.setVisibility(View.VISIBLE);

                                    adapter.notifyItemInserted(cartProducts.indexOf(productModel));
                                    Log.d("key1", productModel.toString());
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getCartPrice() {
        //productTotalSumTv.setText(String.valueOf(0.00));

        DatabaseReference itemProductReference = mDatabase.getReference(NodeNames.PRODUCT);
        DatabaseReference rootRef = mDatabase.getReference(NodeNames.USERS).child(currentUser.getUid()).child(NodeNames.CART);
        ArrayList<String> prices = new ArrayList<>();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot sh : snapshot.getChildren()) {

                    if (sh.exists()) {
                        prices.clear();
                        String key = sh.getKey();

                        productRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String price = snapshot.child(NodeNames.PRODUCT_PRICE).getValue().toString();
                                prices.add(price);
                                sum = 0.00;
                                for (String p : prices) {

                                    sum = sum + Double.parseDouble(p);
                                    Log.d("sh123", String.valueOf(sum));
                                }
                                productTotalSumTv.setText(String.valueOf(sum));
                                calculateTotalCheckOut(Double.parseDouble(productTotalSumTv.getText().toString()));
                                Log.d("cart", price);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(NodeNames.CART)) {

                    Log.d("avillabe", snapshot.getKey());
                } else {
                    sum = 0.00;
                    productTotalSumTv.setText(String.valueOf(sum));
                    calculateTotalCheckOut(Double.parseDouble(productTotalSumTv.getText().toString()));
                    Log.d("not", "not123");
                    Log.d("no", String.valueOf(00.00));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void calculateTotalCheckOut(Double amount) {
        if(amount==0.00 || amount == 0.0 || amount == 0){
            totalAmountTv.setText(String.valueOf(0.00));
        }else{
            Double totalAmount = amount + 100.00;
            totalAmountTv.setText(String.valueOf(totalAmount));
        }

    }


}