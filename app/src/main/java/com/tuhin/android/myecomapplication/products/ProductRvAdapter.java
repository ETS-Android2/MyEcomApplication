package com.tuhin.android.myecomapplication.products;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.common.NodeNames;
import com.tuhin.android.myecomapplication.current_prd.CurrentProductActivity;
import com.tuhin.android.myecomapplication.model.ProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductRvAdapter extends RecyclerView.Adapter<ProductRvAdapter.ProductViewHolder> {

    public static final String PRODCUT_ID_INTENT_KEY = "com.tuhin.android.myecomapplication.products.product_id";

    private Context context;
    private ArrayList<ProductModel> products;
    private FirebaseStorage storage = FirebaseStorage.getInstance(Constants.FIREBASE_STORAGE_REFERENCE_URL);

    public ProductRvAdapter(Context context, ArrayList<ProductModel> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_container_view, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {


        ProductModel product = products.get(holder.getAdapterPosition());
        storage.getReference(NodeNames.PRODUCT_IMAGES + "/" + product.getProduct_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.trash_empty)
                        .into(holder.getProductImage());
            }
        });

        holder.getPrdNameTv().setText(product.getName());
        holder.getPrdPriceTv().setText(product.getPrice());
        holder.getProductGradLL().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CurrentProductActivity.class);
                intent.putExtra(ProductRvAdapter.PRODCUT_ID_INTENT_KEY, product.getProduct_id());

                context.startActivity(intent);
            }
        });

        holder.getAddCartPrdBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productAddToCart(product.getProduct_id());
            }
        });

    }

    private void productAddToCart(String product_id) {


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference currntUserRef = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL).getReference(NodeNames.USERS).child(userId).child(NodeNames.CART);

        Map<String, String> productCartMap = new HashMap<>();
        productCartMap.put(NodeNames.CART_PRODUCT_ID, product_id);
        productCartMap.put(NodeNames.CART_PRODUCT_QUNATITY, "1");

        currntUserRef.child(product_id).setValue(productCartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, context.getString(R.string.product_added_to_cart), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.failed_add_product, task.getException().toString()), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView prdNameTv, prdPriceTv;
        private LinearLayout productGradLL;
        private Button addCartPrdBtn;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.prdGradImageIv);
            prdNameTv = itemView.findViewById(R.id.gradProductNameTv);
            prdPriceTv = itemView.findViewById(R.id.gradProductPriceTv);
            productGradLL = itemView.findViewById(R.id.productGradLL);
            addCartPrdBtn = itemView.findViewById(R.id.addCartPrdBtn);

        }

        public ImageView getProductImage() {
            return productImage;
        }

        public TextView getPrdNameTv() {
            return prdNameTv;
        }

        public TextView getPrdPriceTv() {
            return prdPriceTv;
        }

        public LinearLayout getProductGradLL() {
            return productGradLL;
        }

        public Button getAddCartPrdBtn() {
            return addCartPrdBtn;
        }
    }
}
