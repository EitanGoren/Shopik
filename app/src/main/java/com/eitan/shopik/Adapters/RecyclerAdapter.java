package com.eitan.shopik.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Customer.CustomerMainActivity;
import com.eitan.shopik.Customer.FullscreenImageActivity;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> implements Serializable {

    private ArrayList<RecyclerItem> items;
    private String type;

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

    public void setItems(ArrayList<RecyclerItem> items){
        this.items = items;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView sub_cat, brand;
        private Button full_screen;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            if(type.equals("SubCategory")){
                imageView = itemView.findViewById(R.id.sub_category_image);
                sub_cat = itemView.findViewById(R.id.sub_category_button);
            }
            else if(type.equals("Item") || type.equals("New-Item")) {
                imageView = itemView.findViewById(R.id.image_slider);
                brand = itemView.findViewById(R.id.slider_brand);
                full_screen = itemView.findViewById(R.id.fullscreen_button);
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
                    Macros.Functions.GlidePicture(getContext(),item.getImages().get(0),imageView);
                    full_screen.setOnClickListener(v -> {

                        Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                        intent.putExtra("isFav",false);
                        intent.putExtra("brand", item.getBrand());
                        intent.putExtra("id", item.getId());
                        intent.putExtra("img1", item.getImages().get(0));
                        intent.putExtra("img2", item.getImages().get(1));
                        intent.putExtra("img3", item.getImages().get(2));
                        intent.putExtra("img4", item.getImages().get(3));
                        intent.putExtra("seller_logo", item.getSellerLogoUrl());
                        intent.putExtra("description", item.toString());
                        intent.putExtra("type", item.getType());

                        ArrayList<Pair<View,String>> pairs = new ArrayList<>();
                        pairs.add(Pair.create(brand,"company_name"));

                        Macros.Functions.fullscreen(getContext(),intent,pairs);
                    });
                    break;
                case "Brand":
                    brand.setText(item.getText());
                    break;
                case "SubCategory":

                    GenderModel model = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(GenderModel.class);

                    Macros.Functions.GlidePicture(getContext(), item.getImage_resource(), imageView);
                    sub_cat.setText(item.getText());
                    imageView.setOnClickListener(v -> {

                        Intent intent = new Intent(getContext(), CustomerMainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("gender", item.getGender());
                        bundle.putString("type", item.getType());
                        bundle.putString("imageUrl", item.getUserImageUrl());
                        bundle.putString("sub_category", item.getItem_sub_category());
                        intent.putExtra("bundle", bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        if(model.getInterstitialAd().isAdLoaded())
                            model.getInterstitialAd().show();
                        else{
                            getContext().startActivity(intent);
                            ((Activity) getContext()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        }

                        model.getInterstitialAd().setAdListener(new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                getContext().startActivity(intent);
                                ((Activity) getContext()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                Log.d(Macros.TAG,adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {

                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        });

                    });
                    break;
            }
        }

        public Context getContext() {return itemView.getContext();}
    }
}
