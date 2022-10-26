package com.eitan.shopik.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.customer.FullscreenImageActivity;
import com.eitan.shopik.items.RecyclerItem;
import com.eitan.shopik.viewHolders.RecyclerViewHolder;

import java.io.Serializable;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
        implements Serializable {

    private final ArrayList<RecyclerItem> items;
    private final ViewHolderType type;

    public enum ViewHolderType{
        ITEM,
        BRAND
    }

    private static class BrandRecyclerViewHolder extends RecyclerViewHolder{
        public BrandRecyclerViewHolder(@NonNull View itemView) { super(itemView);}

        @Override
        public void setItem(RecyclerItem item) {
            mBrand.setText(item.getText());
        }
    }
    private static class ItemRecyclerViewHolder extends RecyclerViewHolder{

        public ItemRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_slider);
            full_screen = itemView.findViewById(R.id.fullscreen_button);
            category = itemView.findViewById(R.id.category);
        }

        @Override
        public void setItem(RecyclerItem item) {
            mBrand.setText(item.getText());
            Macros.Functions.GlidePicture(getContext(), item.getImages().get(0), imageView,900);
            full_screen.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("isFav",false);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images", item.getImages());
                intent.putExtra("package", bundle);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(),
                        Pair.create(mBrand,"company_name"));
                getContext().startActivity(intent, options.toBundle());
            });
            category.setText(item.getType());
        }
    }

    public RecyclerAdapter(ArrayList<RecyclerItem> items, ViewHolderType type){
        this.items = items;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerViewHolder selected = null;

        switch (type){
            case ITEM:
                if(parent.getTag() == null || parent.getTag() instanceof BrandRecyclerViewHolder) {
                    selected = new ItemRecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.slide_item_container, parent, false));
                    parent.setTag(selected);
                }
                else if(parent.getTag() != null && parent.getTag() instanceof ItemRecyclerViewHolder) {
                    selected = (RecyclerViewHolder) parent.getTag();
                }
                break;
            case BRAND:
                if(parent.getTag() == null || parent.getTag() instanceof ItemRecyclerViewHolder) {
                    selected = new BrandRecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.brand_recycler_item, parent, false));
                    parent.setTag(selected);
                }
                else if(parent.getTag() != null && parent.getTag() instanceof BrandRecyclerViewHolder) {
                    selected = (RecyclerViewHolder) parent.getTag();
                }
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
}
