package com.tuhin.android.myecomapplication.category_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tuhin.android.myecomapplication.R;

public class ImageSliderSetAaapter extends SliderViewAdapter<ImageSliderSetAaapter.ImagelSliderViewHolder> {


    private Context context;
    private int[] images;

    public ImageSliderSetAaapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public ImagelSliderViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.image_slider_container,parent,false);
        return new ImagelSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImagelSliderViewHolder viewHolder, int position) {

        viewHolder.getImageView().setImageResource(images[position]);
        viewHolder.getTextView().setText("Discount");
    }

    @Override
    public int getCount() {
        return images.length;
    }

    class ImagelSliderViewHolder extends SliderViewAdapter.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public ImagelSliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_auto_image_slider);
            textView = itemView.findViewById(R.id.tv_auto_image_slider);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
