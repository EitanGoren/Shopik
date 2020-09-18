package com.eitan.shopik.customer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.FullscreenAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_fullscreen_image);

        init();

        Button mClose = findViewById(R.id.fullscreen_close_x);
        mClose.setOnClickListener(v -> this.supportFinishAfterTransition());
    }

    private void init() {

        ImageView mFavorite = findViewById(R.id.favorite_sign);
        CircleImageView company = findViewById(R.id.comp_logo);
        TextView name = findViewById(R.id.comp_name);

        String brand = getIntent().getStringExtra("brand");
        name.setText(brand);
        String seller_logo = getIntent().getStringExtra("seller_logo");

        if(seller_logo != null )
            Glide.with(this).load(seller_logo).into(company);

        String seller_name = getIntent().getStringExtra("seller");
        boolean isFavorite = getIntent().getBooleanExtra("isFav", false);
        String description = getIntent().getStringExtra("description");

        if(seller_logo == null && seller_name == null){
            CardView comp_info = findViewById(R.id.comp_info);
            comp_info.setVisibility(View.GONE);
        }

        ArrayList<String> imagesUrl = new ArrayList<>();
        imagesUrl.add(getIntent().getStringExtra("img1"));
        imagesUrl.add(getIntent().getStringExtra("img2"));
        imagesUrl.add(getIntent().getStringExtra("img3"));
        imagesUrl.add(getIntent().getStringExtra("img4"));

        if(isFavorite) {
            mFavorite.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            mFavorite.startAnimation(animation);
        }
        else
            mFavorite.setVisibility(View.INVISIBLE);

        ViewPager2 viewPager = findViewById(R.id.fullscreen_image_viewPager);
        viewPager.setAdapter(new FullscreenAdapter(this, imagesUrl, description));
        startPostponedEnterTransition();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }
}
