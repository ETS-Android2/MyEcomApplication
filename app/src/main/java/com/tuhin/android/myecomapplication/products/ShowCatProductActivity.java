package com.tuhin.android.myecomapplication.products;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.cart.CartActivity;
import com.tuhin.android.myecomapplication.common.Constants;

import com.tuhin.android.myecomapplication.common.NodeNames;
import com.tuhin.android.myecomapplication.model.ProductModel;

import java.util.ArrayList;

public class ShowCatProductActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private DatabaseReference rootREf,productRef;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private StorageReference imageStorageReference;
    private ArrayList<ProductModel> products;
    private String product_cat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cat_product);

        recyclerView = findViewById(R.id.productRv);
        products = new ArrayList<>();

        rootREf = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL).getReference(NodeNames.PRODUCT);
        rootREf.keepSynced(true);
        Toast.makeText(getApplicationContext(), rootREf.get().toString(), Toast.LENGTH_SHORT).show();

        firebaseStorage = FirebaseStorage.getInstance(Constants.FIREBASE_STORAGE_REFERENCE_URL);
        imageStorageReference = firebaseStorage.getReference(NodeNames.PRODUCT_IMAGES);

        auth = FirebaseAuth.getInstance();

        if(getIntent()!= null){
            Intent data = getIntent();
            product_cat = data.getStringExtra(Constants.CATEGORY_INTENT_KEY);

        }
        ProductRvAdapter adapter = new ProductRvAdapter(this,products);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);

        rootREf.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ProductModel productModel = snapshot.getValue(ProductModel.class);
                if(productModel.getCategory().equals(product_cat)){
                    products.add(productModel);
                    adapter.notifyItemInserted(products.indexOf(productModel));
                    //adapter.notifyDataSetChanged();
                    //Toast.makeText(getApplicationContext(), productModel.toString(), Toast.LENGTH_SHORT).show();
                }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.cartMenu:
                startActivity(new Intent(ShowCatProductActivity.this, CartActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_menu,menu);
        return true;
    }
}