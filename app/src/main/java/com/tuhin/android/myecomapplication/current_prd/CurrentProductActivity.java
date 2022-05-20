package com.tuhin.android.myecomapplication.current_prd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.cart.CartActivity;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;
import com.tuhin.android.myecomapplication.products.ProductRvAdapter;

import java.util.HashMap;
import java.util.Map;

public class CurrentProductActivity extends AppCompatActivity {

    private TextView currntPrdNameTv, currntPrdPriceTv, currntproductDesTv;
    private Button addToCartBtn;
    private ImageView currntPrdImgIv;

    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_product);

        currntPrdNameTv = findViewById(R.id.currntPrdNameTv);
        currntPrdPriceTv = findViewById(R.id.currntPrdPriceTv);
        currntproductDesTv = findViewById(R.id.currntproductDesTv);


        addToCartBtn = findViewById(R.id.addToCartBtn);

        currntPrdImgIv = findViewById(R.id.currntPrdImgIv);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL);
        mFirebaseStorage = FirebaseStorage.getInstance(Constants.FIREBASE_STORAGE_REFERENCE_URL);

        if (getIntent() != null) {
            Intent data = getIntent();
            productId = data.getStringExtra(ProductRvAdapter.PRODCUT_ID_INTENT_KEY);
        }

        rootRef = mDatabase.getReference(NodeNames.PRODUCT);
        StorageReference storageReference = mFirebaseStorage.getReference(NodeNames.PRODUCT_IMAGES + "/" + productId + ".jpg");
        loadPhoto(storageReference);
        updateProductNameAndPrice(rootRef, productId);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCartDbs(productId);
            }
        });

    }

    private void updateProductNameAndPrice(DatabaseReference reference, String productId) {

        reference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String prdName = snapshot.child(NodeNames.PRODUCT_NAME).getValue().toString();
                String prdPrice = snapshot.child(NodeNames.PRODUCT_PRICE).getValue().toString();
                currntPrdNameTv.setText(prdName);
                currntPrdPriceTv.setText(prdPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch, error.getMessage().toString()), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loadPhoto(StorageReference storageReference) {

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getApplicationContext())
                        .load(uri)
                        .placeholder(R.drawable.trash_empty)
                        .into(currntPrdImgIv);
            }
        });

    }

    private void addToCartDbs(String productId) {
        String userId= currentUser.getUid();
        DatabaseReference currntUserRef = mDatabase.getReference(NodeNames.USERS).child(userId).child(NodeNames.CART);

        Map<String, String> productCartMap = new HashMap<>();
        productCartMap.put(NodeNames.CART_PRODUCT_ID, productId);
        productCartMap.put(NodeNames.CART_PRODUCT_QUNATITY, "1");

        currntUserRef.child(productId).setValue(productCartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CurrentProductActivity.this, getString(R.string.product_added_to_cart), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CurrentProductActivity.this, getString(R.string.failed_add_product,task.getException().toString()), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cartMenu:
                startActivity(new Intent(CurrentProductActivity.this, CartActivity.class));
                break;
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }


}