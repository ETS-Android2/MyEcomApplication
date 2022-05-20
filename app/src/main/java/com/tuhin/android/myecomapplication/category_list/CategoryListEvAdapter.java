package com.tuhin.android.myecomapplication.category_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuhin.android.myecomapplication.R;
import com.tuhin.android.myecomapplication.common.Constants;
import com.tuhin.android.myecomapplication.products.ShowCatProductActivity;

public class CategoryListEvAdapter extends RecyclerView.Adapter<CategoryListEvAdapter.CatyegoryViewHolder> {


    private int[] images;
    private String[] tittles;
    private Context context;

    public CategoryListEvAdapter(int[] images, String[] tittles,Context context) {
        this.images = images;
        this.tittles = tittles;
        this.context = context;
    }

    @NonNull
    @Override
    public CatyegoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_rv_container,parent,false);
        return new CatyegoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatyegoryViewHolder holder, int position) {

        holder.getCategoryImageTv().setImageResource(images[holder.getAdapterPosition()]);
        holder.getCategoryTittleTv().setText(tittles[holder.getAdapterPosition()]);
        holder.getCategoryFL().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowCatProductActivity.class);
                intent.putExtra(Constants.CATEGORY_INTENT_KEY,tittles[holder.getAdapterPosition()]);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return images.length;
    }


    class CatyegoryViewHolder extends RecyclerView.ViewHolder{

        private TextView categoryTittleTv;
        private ImageView categoryImageTv;
        private FrameLayout categoryFL;


        public CatyegoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageTv = itemView.findViewById(R.id.categoryIv);
            categoryFL = itemView.findViewById(R.id.CategoryViewLL);
            categoryTittleTv = itemView.findViewById(R.id.CategorytittleTv);

        }

        public TextView getCategoryTittleTv() {
            return categoryTittleTv;
        }

        public ImageView getCategoryImageTv() {
            return categoryImageTv;
        }

        public FrameLayout getCategoryFL() {
            return categoryFL;
        }

    }
}
