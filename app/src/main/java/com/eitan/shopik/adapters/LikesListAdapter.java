package com.eitan.shopik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesListAdapter extends ArrayAdapter<LikedUser> implements Serializable {

    public LikesListAdapter(@NonNull Context context, int resource, List<LikedUser> items) {
        super(context, resource, items);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        LikedUser user = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.likes_list_item, parent,false);
        }

        CircleImageView user_icon = convertView.findViewById(R.id.user_icon);
        TextView seller_name = convertView.findViewById(R.id.user_name);
        ImageView fav_icon = convertView.findViewById(R.id.fav_sign);

        assert user != null;
        seller_name.setText(user.getFirst_name() + " " + user.getLast_name());
        if(user.getProfile_image() != null)
            Glide.with(getContext()).load(user.getProfile_image()).into(user_icon);
        else
            Glide.with(getContext()).load(Macros.DEFAULT_PROFILE_IMAGE).into(user_icon);

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
    public LikedUser getItem(int position) {
        return super.getItem(position);
    }
}
