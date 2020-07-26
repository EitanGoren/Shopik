package com.eitan.shopik.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

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
            case "Market":
                selected = new RecyclerAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.market_item,parent,false));
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
        private TextView sub_cat, brand, price, text;
        private Button link, full_screen;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            if(type.equals("SubCategory")){
                imageView = itemView.findViewById(R.id.sub_category_image);
                sub_cat = itemView.findViewById(R.id.sub_category_button);
            }
            else if(type.equals("Market")){
                imageView = itemView.findViewById(R.id.image_market);
                text = itemView.findViewById(R.id.name_market);
                price = itemView.findViewById(R.id.price_market);
            }
            else if(type.equals("Item") || type.equals("New-Item")) {
                imageView = itemView.findViewById(R.id.image_slider);
                brand = itemView.findViewById(R.id.slider_brand);
                link = itemView.findViewById(R.id.store_link);
                full_screen = itemView.findViewById(R.id.fullscreen_button);
                price = itemView.findViewById(R.id.price_tag);
            }
            else {
                brand = itemView.findViewById(R.id.slider_brand);
                link = itemView.findViewById(R.id.store_link);
                full_screen = itemView.findViewById(R.id.fullscreen_button);
            }
        }

        public void setItem(final RecyclerItem item) {
            switch (type) {
                case "Item": {
                    brand.setText(item.getText());
                    brand.setCompoundDrawablesWithIntrinsicBounds(brand.getContext().getDrawable(R.drawable.ic_thumb_up_blue), null, null, null);
                    brand.setCompoundDrawablePadding(20);
                    link.setOnClickListener(v -> Macros.Functions.buy(link.getContext(),item.getLink()));
                    final ArrayList<String> imagesUrl = item.getImages();
                    Glide.with(imageView.getContext()).load(imagesUrl.get(0)).into(imageView);
                    full_screen.setOnClickListener(v -> Macros.Functions.fullscreen(full_screen.getContext(),item));
                    break;
                }
                case "Market": {
                    Glide.with(imageView.getContext()).load(item.getImage_resource()).into(imageView);
                    price.setText(item.getPrice());
                    text.setText(item.getText());
                    break;
                }
                case "New-Item": {
                    brand.setText(item.getText());
                    price.setText(item.getPrice());
                    link.setOnClickListener(v -> Macros.Functions.buy(link.getContext(),item.getLink()));
                    final ArrayList<String> imagesUrl = item.getImages();
                    Glide.with(imageView.getContext()).load(imagesUrl.get(0)).into(imageView);
                    full_screen.setOnClickListener(v -> {
                       Macros.Functions.fullscreen(full_screen.getContext(),item);
                    });
                    break;
                }
                case "Brand":
                    brand.setText(item.getText());
                    break;
                case "SubCategory":
                    Glide.with(imageView.getContext()).load(item.getImage_resource()).into(imageView);
                    sub_cat.setText(item.getText());
                    imageView.setOnClickListener(v -> Macros.Functions.goToCustomerMain(imageView.getContext(),item));
                    break;
            }
        }
    }
}
