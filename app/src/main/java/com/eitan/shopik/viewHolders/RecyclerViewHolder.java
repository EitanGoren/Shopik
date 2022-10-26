package com.eitan.shopik.viewHolders;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.R;
import com.eitan.shopik.items.RecyclerItem;

public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder {

    protected ImageView imageView;
    protected TextView mBrand, category;
    protected Button full_screen;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        mBrand = itemView.findViewById(R.id.slider_brand);
    }

    public abstract void setItem(final RecyclerItem item);
    public Context getContext() {return itemView.getContext();}
}
