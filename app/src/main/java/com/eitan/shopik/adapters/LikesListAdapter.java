package com.eitan.shopik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.eitan.shopik.PublicUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesListAdapter extends ArrayAdapter<PublicUser> implements Serializable {

    // TODO: ADD VIEW HOLDER

    public LikesListAdapter(@NonNull Context context, int resource, Set<PublicUser> items) {
        super(context, resource, new ArrayList<>(items));
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        PublicUser user = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.likes_list_item, parent,false);
        }

        CircleImageView user_icon = convertView.findViewById(R.id.user_icon);
        TextView seller_name = convertView.findViewById(R.id.user_name);
        ImageView fav_icon = convertView.findViewById(R.id.fav_sign);

        assert user != null;
        user_icon.setBorderColor(user.getGender().equals("male") ? Color.BLUE : Color.RED);
        user_icon.setBorderWidth(2);
        seller_name.setText(user.getName());

        if(user.getProfile_image() != null)
            Macros.Functions.GlidePicture(getContext(), user.getProfile_image(), user_icon, 350);
        else
            Macros.Functions.GlidePicture(getContext(), Macros.DEFAULT_PROFILE_IMAGE, user_icon, 350);

        user_icon.setOnClickListener(v -> Toast.makeText(getContext(),"Will Be Added...", Toast.LENGTH_SHORT ).show());
        fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_favorite_black_24dp));

        if (user.isFavorite()) {
            fav_icon.setVisibility(View.VISIBLE);
            fav_icon.bringToFront();
        }
        else {
            fav_icon.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    @Nullable
    @Override
    public PublicUser getItem(int position) {
        return super.getItem(position);
    }
}
