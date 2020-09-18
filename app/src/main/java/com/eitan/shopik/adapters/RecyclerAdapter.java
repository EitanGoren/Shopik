package com.eitan.shopik.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.customer.CustomerMainActivity;
import com.eitan.shopik.customer.FullscreenImageActivity;
import com.eitan.shopik.items.RecyclerItem;

import java.io.Serializable;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> implements Serializable {

    private final ArrayList<RecyclerItem> items;
    private final String type;

    public RecyclerAdapter(ArrayList<RecyclerItem> items, String type){
        this.items = items;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerAdapter.RecyclerViewHolder selected = null;
        switch (type){
            case "New-Item":
            case "Item" :
                selected = new RecyclerAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.slide_item_container,parent,false));
                break;
            case "Brand":
                selected = new RecyclerAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.brand_recycler_item,parent,false));
                break;
            case "SubCategory":
                selected = new RecyclerAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.recycler_sub_category_item,parent,false));
                break;
        }
        assert selected != null;
        return selected;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private ImageButton imageButton;
        private TextView sub_cat, brand,category;
        private Button full_screen;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            if(type.equals("SubCategory")){
                imageButton = itemView.findViewById(R.id.sub_category_image);
                sub_cat = itemView.findViewById(R.id.sub_category_button);
            }
            else if(type.equals("Item") || type.equals("New-Item")) {
                imageView = itemView.findViewById(R.id.image_slider);
                brand = itemView.findViewById(R.id.slider_brand);
                full_screen = itemView.findViewById(R.id.fullscreen_button);
                category = itemView.findViewById(R.id.category);
            }
            else {
                brand = itemView.findViewById(R.id.slider_brand);
                full_screen = itemView.findViewById(R.id.fullscreen_button);
            }
        }

        public void setItem(final RecyclerItem item) {

            switch (type) {
                case "Item":
                case "New-Item":
                    brand.setText(item.getText());

                    Glide.with(getContext()).load(item.getImages().get(0)).
                            transition(withCrossFade(900)).
                            into(imageView);

                    full_screen.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                        intent.putExtra("isFav",false);
                        intent.putExtra("brand", item.getSeller());
                        intent.putExtra("id", item.getId());
                        for(int i=0;i<item.getImages().size(); i++) {
                            intent.putExtra("img"+(i+1), item.getImages().get(i));
                        }
                        if(item.getImages().size() < 4){
                            for(int i=item.getImages().size(); i<4; i++) {
                                intent.putExtra("img"+(i), item.getImages().get(0));
                            }
                        }
                        intent.putExtra("seller_logo", item.getSellerLogoUrl());
                        intent.putExtra("description", item.toString());
                        intent.putExtra("seller", item.getSeller());

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(),
                                        Pair.create(brand,"company_name"));
                        getContext().startActivity(intent, options.toBundle());
                    });

                    category.setText(item.getType());

                    break;
                case "Brand":
                    brand.setText(item.getText());
                    break;
                case "SubCategory":
                    Macros.Functions.GlidePicture(getContext(), item.getImage_resource(), imageButton);
                    sub_cat.setText(item.getText());
                    imageButton.setOnClickListener(v -> {

                        Intent intent = new Intent(getContext(), CustomerMainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("gender", item.getGender());
                        bundle.putString("type", item.getType());
                        bundle.putString("imageUrl", item.getUserImageUrl());
                        bundle.putString("sub_category", item.getItem_sub_category());
                        intent.putExtra("bundle", bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        getContext().startActivity(intent);
                        ((Activity) getContext()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                    });
                    break;
            }
        }

        public Context getContext() {return itemView.getContext();}
    }
}
