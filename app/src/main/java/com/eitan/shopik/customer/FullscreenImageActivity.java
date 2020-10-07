package com.eitan.shopik.customer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.eitan.shopik.R;
import com.eitan.shopik.adapters.FullscreenAdapter;

import java.util.ArrayList;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_fullscreen_image);

        init();

        ImageView mClose = findViewById(R.id.fullscreen_close_x);
        mClose.setOnClickListener(v -> this.supportFinishAfterTransition());
    }

    private void init() {

        ImageView mFavorite = findViewById(R.id.favorite_sign);

        boolean isFavorite = getIntent().getBooleanExtra("isFav", false);
        String description = getIntent().getStringExtra("description") != null ?
                getIntent().getStringExtra("description") : "No Info";

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
