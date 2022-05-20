package com.tuhin.android.myecomapplication.cart;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.tuhin.android.myecomapplication.model.ProductModel;

import java.util.ArrayList;
import java.util.Objects;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {


    private ArrayList<ProductModel> cartProducts;
    private Context context;
    //private double sum = 0;
    private int count = 1;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Constants.FIREBASE_STORAGE_REFERENCE_URL);



    public CartRecyclerAdapter(Context context, ArrayList<ProductModel> cartProducts) {
        this.cartProducts = cartProducts;

        this.context = context;
    }



    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_container, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        ProductModel product = cartProducts.get(holder.getAdapterPosition());
        firebaseStorage.getReference(NodeNames.PRODUCT_IMAGES+"/"+product.getProduct_id()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.trash_empty)
                        .into(holder.getProductImageView());

            }
        });

        holder.getProductNameTv().setText(product.getName());
        holder.getProductPriceTv().setText(product.getPrice());
        holder.getRemoveItemBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(product);
            }
        });
        holder.getIncItemBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count<5){
                    count++;
                }

                holder.getItemQntTv().setText(String.valueOf(count));
            }
        });

        holder.getDecItemBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count<=5 && count>1){
                    count--;
                }
                holder.getItemQntTv().setText(String.valueOf(count));
            }
        });



    }

    public ArrayList<ProductModel> getCartProducts() {
        return cartProducts;
    }

    private void removeItem(ProductModel product1) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE_URL).getReference(NodeNames.USERS).child(userId).child(NodeNames.CART);
        rootRef.child(product1.getProduct_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, context.getString(R.string.item_removed_succesfully), Toast.LENGTH_SHORT).show();
                    int pos = cartProducts.indexOf(product1);
                    cartProducts.remove(product1);

                    notifyItemRemoved(pos);
                }else{
                    Toast.makeText(context, context.getString(R.string.item_removed_failed,task.getException().toString()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }






    class CartViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImageView;
        private TextView productNameTv,productPriceTv,itemQntTv;
        private Button removeItemBtn,incItemBtn,decItemBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            this.productImageView = itemView.findViewById(R.id.productImageCartRvTv);
            this.productNameTv = itemView.findViewById(R.id.productNameCartRvTv);
            this.productPriceTv = itemView.findViewById(R.id.productPriceCartRvTv);
            this.itemQntTv = itemView.findViewById(R.id.itemQntTv);
            this.removeItemBtn = itemView.findViewById(R.id.removeItemTvCartRvBtn);
            this.incItemBtn = itemView.findViewById(R.id.incrementItemNoBtn);
            this.decItemBtn= itemView.findViewById(R.id.decrementItemNoBtn);
        }

        public ImageView getProductImageView() {
            return productImageView;
        }

        public TextView getProductNameTv() {
            return productNameTv;
        }

        public TextView getProductPriceTv() {
            return productPriceTv;
        }

        public TextView getItemQntTv() {
            return itemQntTv;
        }

        public Button getRemoveItemBtn() {
            return removeItemBtn;
        }

        public Button getIncItemBtn() {
            return incItemBtn;
        }

        public Button getDecItemBtn() {
            return decItemBtn;
        }
    }


}
