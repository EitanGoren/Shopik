package com.eitan.shopik.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.R;

import java.util.ArrayList;
import java.util.List;

public class itemPicsAdapter extends ArrayAdapter<String> {
    public itemPicsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

/*
    public itemPicsAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, objects);
    }

    @Override
    public int getPosition(@Nullable String item) {
        return super.getPosition(item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_images, parent,false);
        }

        final int[] i = {position};

       // final View view = layoutInflater.inflate(R.layout.list_item_images,parent);
        final ImageView imageView = convertView.findViewById(R.id.image_item);

        Glide.with(getContext()).load(getItem(((i[0]) % 4))).into(imageView);

        Button mNext = convertView.findViewById(R.id.next);
        Button mPrev = convertView.findViewById(R.id.previous);

        parent.addView(convertView);

        View finalConvertView = convertView;
        mNext.setOnClickListener(v -> {
            setImage(parent, finalConvertView,i[0],imageView);
            i[0]++;
        });
        mPrev.setOnClickListener(v -> {
            setImage(parent,finalConvertView,i[0],imageView);
            --i[0];
        });

        return convertView;
    }

    private void setImage(ViewGroup container, View view, int num, final ImageView imageView) {
        container.removeView(view);
        Glide.with(getContext()).load(getItem(((Math.abs(num) + 1) % 4))).into(imageView);
        container.addView(view);
    }

 */
}